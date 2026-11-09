# Alerta Vecinal

Sistema de gestión de incidentes vecinales construido sobre una arquitectura de **microservicios REST puros** con Spring Boot.

Permite a los ciudadanos reportar incidentes de seguridad en tiempo real, a los agentes de Serenazgo atenderlos desde un panel operativo, y a los administradores municipales supervisar estadísticas y configurar parámetros del sistema.

---

## Tabla de Contenidos

- [Arquitectura](#arquitectura)
- [Stack Tecnológico](#stack-tecnológico)
- [Flujo de Negocio](#flujo-de-negocio)
- [Modelo de Seguridad](#modelo-de-seguridad)
- [Configuración del Entorno](#configuración-del-entorno)
- [Guía de Arranque](#guía-de-arranque)
- [Referencia de Endpoints](#referencia-de-endpoints)
- [Colección Postman](#colección-postman)
- [Decisiones de Diseño](#decisiones-de-diseño)

---

## Arquitectura

```
┌────────────────────────────────────────────────────────────────────┐
│                        ALERTA VECINAL                              │
│                                                                    │
│  ┌──────────────┐    ┌──────────────────┐    ┌──────────────────┐  │
│  │ auth-service │    │ incident-service │    │ serenazgo-service│  │
│  │    :8081     │    │      :8082       │    │      :8080       │  │
│  │              │    │                  │    │                  │  │
│  │  Registro    │    │  CRUD Incidentes │◄───│  Panel Operativo │  │
│  │  Login JWT   │◄───│ WebSockets STOMP │    │  Historial Local │  │
│  │  Validación  │    │                  │    │                  │  │
│  └──────┬───────┘    └────────▲─────────┘    └──────────────────┘  │
│         │                     │                                    │
│         │ /validate    REST   │ RestTemplate                       │
│         │                     │                                    │
│         │            ┌────────┴─────────┐                          │
│         └────────────│  admin-service   │                          │
│                      │      :8083       │                          │
│                      │                  │                          │
│                      │  Estadísticas    │                          │
│                      │  Paramétricas    │                          │
│                      └──────────────────┘                          │
└────────────────────────────────────────────────────────────────────┘
```

Cada servicio opera de forma independiente con su propia base de datos MySQL.
La comunicación entre servicios se realiza mediante llamadas HTTP directas con `RestTemplate`.
No se utiliza Spring Cloud, Eureka, ni API Gateway; la simplicidad es intencional y adecuada para el alcance del sistema.

| Servicio | Puerto | Base de Datos | Responsabilidad |
|---|---|---|---|
| `auth-service` | 8081 | `alerta_auth_db` | Identidad, JWT, validación de tokens |
| `incident-service` | 8082 | `bd_alerta_incidentes` | CRUD de incidentes, WebSockets (STOMP) |
| `serenazgo-service` | 8080 | `serenazgo_bd` | Panel operativo, auditoría de atención |
| `admin-service` | 8083 | `bd_alerta_admin` | Reportes estadísticos, paramétricas |

---

## Stack Tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 25 |
| Framework | Spring Boot 4.0.6 |
| ORM | Spring Data JPA + Hibernate |
| Base de Datos | MySQL 8+ |
| Seguridad | JWT (JSON Web Tokens) con validación inter-servicio |
| Comunicación Síncrona | RestTemplate con propagación automática de contexto |
| Comunicación Asíncrona | STOMP sobre WebSockets (Spring WebSocket) |
| Testing | JUnit 5 + @DataJpaTest + H2 (base de datos en memoria) |
| Boilerplate | Lombok (`@RequiredArgsConstructor`, `@Data`, `@Builder`) |

---

## Flujo de Negocio

El sistema modela el ciclo de vida completo de un incidente de seguridad ciudadana:

### 1. Registro e Identificación
Un ciudadano, agente de serenazgo o administrador se registra en el `auth-service` indicando su rol (`ROLE_VECINO`, `ROLE_SERENAZGO` o `ROLE_ADMIN`). Al iniciar sesión recibe un token JWT que lo identifica en todo el ecosistema.

### 2. Reporte del Incidente
El ciudadano reporta un incidente indicando tipo, descripción, ubicación y prioridad. Opcionalmente incluye su nombre; si no lo hace, el sistema lo considera un reporte anónimo de forma implícita (sin necesidad de un campo booleano explícito).

Al persistir el incidente, el estado se asigna automáticamente como `PENDIENTE` y la fecha de creación se registra vía `@PrePersist`. De forma simultánea, el `incident-service` envía un mensaje por WebSocket a todos los agentes suscritos al tópico `/topic/alertas`.

### 3. Atención del Serenazgo
El agente de serenazgo consulta los incidentes activos desde su propio servicio, que internamente consume al `incident-service` mediante `RestTemplate`. Cuando atiende un caso, ocurren dos cosas:

- Se actualiza el estado del incidente en la base de datos central (`bd_alerta_incidentes`).
- Se persiste un registro de auditoría en la base de datos local del serenazgo (`serenazgo_bd`), conservando el estado anterior, el estado nuevo, el ID del agente y la fecha del cambio.

Esta separación garantiza que si la base de datos de incidentes sufre algún problema, el serenazgo conserva evidencia independiente de su trabajo.

### 4. Supervisión Administrativa
El administrador consulta estadísticas globales: totales por estado (pendientes, en proceso, atendidos, anulados) y agrupaciones por prioridad. Esta data se genera dinámicamente consumiendo al `incident-service` en tiempo real. Adicionalmente, gestiona las paramétricas del sistema (tipos de incidente, prioridades) desde su propio CRUD local.

### Estados del Incidente

```
PENDIENTE ──► EN_PROCESO ──► ATENDIDO
                 │
                 └──────────► ANULADO
```

---

## Modelo de Seguridad

La seguridad se implementa con un patrón de **validación delegada inter-servicio**:

1. El usuario se autentica contra `auth-service` y recibe un JWT.
2. Al llamar a cualquier endpoint protegido, el servicio receptor intercepta la petición antes de que llegue al controlador.
3. El interceptor (`TokenInterceptor`) extrae el token de la cabecera `Authorization` y lo valida contra `GET /api/auth/validate?token=xxx`.
4. El `auth-service` responde con el estado del token y el rol del usuario.
5. El interceptor permite o bloquea el acceso según el rol requerido.

| Servicio | Rol Requerido | Justificación |
|---|---|---|
| `incident-service` | Cualquier rol autenticado | Todo usuario registrado puede reportar incidentes |
| `serenazgo-service` | `ROLE_SERENAZGO` o `ROLE_ADMIN` | Solo personal autorizado atiende casos |
| `admin-service` | `ROLE_ADMIN` | Solo el administrador accede a reportes y paramétricas |

### Propagación de Contexto
Cuando el `admin-service` o el `serenazgo-service` necesitan consultar al `incident-service`, el token JWT del usuario original se propaga automáticamente a través de un interceptor configurado en el bean `RestTemplate`. Esto evita que las llamadas internas sean rechazadas por falta de autenticación.

---

## Configuración del Entorno

### Requisitos

- JDK 25
- Apache Maven 3.9+
- MySQL 8+ corriendo en `localhost:3306`

### Base de Datos

El usuario por defecto de MySQL debe ser `root` con contraseña `hectorpalacios`. 

**Para inicializar el proyecto, simplemente abre MySQL Workbench (o similar) y ejecuta el archivo `script_bd_alerta_vecinal.sql`** que se encuentra en la raíz del proyecto. Este script preparará los 4 esquemas limpios. Las tablas internas se crearán automáticamente al arrancar los servicios gracias a `ddl-auto=update`.

> Si tu contraseña de MySQL es diferente, recuerda actualizar la propiedad `spring.datasource.password` en el archivo `application.properties` de los 4 servicios.

```
auth-service       → alerta_auth_db
incident-service   → bd_alerta_incidentes
serenazgo-service  → serenazgo_bd
admin-service      → bd_alerta_admin
```

---

## Guía de Arranque

Levanta los servicios en este orden para evitar errores de conexión entre dependencias:

```bash
# 1. Servicio central de incidentes (los demás dependen de este)
cd incident-service
mvn spring-boot:run

# 2. Servicio de autenticación
cd auth-service
mvn spring-boot:run

# 3. Servicio operativo de serenazgo
cd serenazgo-service
mvn spring-boot:run

# 4. Servicio administrativo
cd admin-service
mvn spring-boot:run
```

Al iniciar `incident-service` verás que el bus de mensajes WebSocket arranca en el endpoint `/ws`, listo para emitir alertas en tiempo real.

---

## Referencia de Endpoints

### auth-service (:8081)

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `POST` | `/api/auth/register` | No | Registrar usuario con rol |
| `POST` | `/api/auth/login` | No | Iniciar sesión, obtener JWT |
| `GET` | `/api/auth/profile` | JWT | Consultar perfil del usuario autenticado |
| `GET` | `/api/auth/validate?token=xxx` | No | Validar token (uso inter-servicio) |

### incident-service (:8082)

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/incidentes` | Reportar incidente |
| `GET` | `/api/incidentes` | Listar todos los incidentes |
| `GET` | `/api/incidentes/{id}` | Consultar incidente por ID |
| `GET` | `/api/incidentes/estado/{estado}` | Filtrar por estado |
| `GET` | `/api/incidentes/prioridad/{prioridad}` | Filtrar por prioridad |
| `PUT` | `/api/incidentes/{id}/atencion` | Actualizar estado y comentario |
| `DELETE` | `/api/incidentes/{id}` | Eliminar incidente |

### serenazgo-service (:8080)

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/serenazgo/incidentes` | Panel de incidentes activos |
| `GET` | `/api/serenazgo/incidentes/{id}` | Detalle operativo |
| `PUT` | `/api/serenazgo/incidentes/{id}/estado?serenazgoId=N` | Cambiar estado de atención |
| `GET` | `/api/serenazgo/incidentes/{id}/historial` | Historial de auditoría local |

### admin-service (:8083)

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/admin/reportes/estadisticas` | Dashboard estadístico global |
| `GET` | `/api/admin/reportes/incidentes` | Todos los incidentes (vista admin) |
| `GET` | `/api/admin/reportes/incidentes/filtrar?estado=X` | Filtrar incidentes por estado |
| `POST` | `/api/admin/parametricas` | Crear parámetro del sistema |
| `GET` | `/api/admin/parametricas` | Listar parámetros |
| `GET` | `/api/admin/parametricas?tipo=X` | Filtrar parámetros por tipo |
| `GET` | `/api/admin/parametricas/{id}` | Consultar parámetro por ID |
| `PUT` | `/api/admin/parametricas/{id}` | Actualizar parámetro |
| `DELETE` | `/api/admin/parametricas/{id}` | Eliminar parámetro |

Todos los endpoints de los servicios `incident`, `serenazgo` y `admin` requieren enviar la cabecera `Authorization: Bearer <token>`.

---

## Colección Postman

El archivo `Alerta_Vecinal.postman_collection.json` ubicado en la raíz del proyecto contiene 29 peticiones organizadas en 4 carpetas, listas para importar en Postman.

La petición de Login incluye un script automático que captura el JWT y lo asigna a la variable `{{jwt_token}}`, de forma que las demás peticiones lo utilizan sin intervención manual.

**Flujo de prueba sugerido:**

1. Registrar los 3 usuarios (vecino, serenazgo, admin).
2. Hacer Login con `admin1` → el token se asigna automáticamente.
3. Crear 2 o 3 incidentes desde la carpeta de Incident Service.
4. Hacer Login con `agente1` → cambiar a rol de serenazgo.
5. Consultar y atender incidentes desde la carpeta de Serenazgo Service.
6. Hacer Login con `admin1` nuevamente.
7. Consultar el dashboard estadístico y gestionar paramétricas.

Para validar la seguridad por roles, intentar acceder al Admin Service con un token de `ROLE_VECINO` producirá un `403 Forbidden`.

---

## Decisiones de Diseño

### ¿Por qué no usar Spring Cloud / Eureka?
Eureka es una herramienta de Service Discovery diseñada para entornos Cloud con escalamiento horizontal masivo. En un ecosistema de 4 servicios con direcciones fijas, introduce complejidad innecesaria sin aportar valor. La comunicación punto a punto con `RestTemplate` es la solución correcta para este alcance.

### ¿Por qué STOMP sobre WebSockets para las alertas?
Se adoptó STOMP con Spring WebSocket para enviar alertas en tiempo real porque provee una mensajería estándar (Publish/Subscribe) altamente eficiente. A diferencia de obligar a los clientes o consumidores de la API a consultar repetidamente la base de datos (Pooling), este enfoque push-based notifica instantáneamente a cualquier cliente suscrito al tópico `/topic/alertas`.


### ¿Por qué cada servicio tiene su propia base de datos?
Es la regla fundamental de microservicios: cada servicio es dueño exclusivo de sus datos (Bounded Context). Si todos compartieran una sola base de datos, sería un monolito distribuido, no una arquitectura de microservicios.

### ¿Por qué el serenazgo guarda un historial local?
Si la base de datos de incidentes se corrompe o se elimina un registro, el serenazgo conserva evidencia independiente de todas las acciones realizadas. Esta separación de responsabilidades es lo que justifica la existencia del servicio como microservicio independiente.

### ¿Por qué GenericResponseDto en todos los endpoints?
Garantiza que el consumidor (Frontend o servicio externo) siempre reciba la misma estructura JSON: `{ "response": ... }`. Esto simplifica la deserialización del lado cliente y estandariza el contrato de la API.

### ¿Por qué @RequiredArgsConstructor en lugar de @Autowired?
La inyección por constructor con campos `final` es la práctica recomendada por el equipo de Spring Framework. Garantiza inmutabilidad, facilita las pruebas unitarias y permite que el compilador detecte dependencias faltantes en tiempo de compilación.

---
