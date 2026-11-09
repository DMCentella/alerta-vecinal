# Microservicio de Incidentes (incident-service)

Este microservicio forma parte del sistema "Alerta Vecinal" y es el núcleo para la gestión de incidentes de seguridad reportados por los ciudadanos.

## Funcionalidades Principales

- **Registro de Incidentes**: Permite a los ciudadanos (de forma anónima o identificada) reportar nuevos incidentes de seguridad.
- **Consulta de Incidentes**: Ofrece endpoints para listar todos los incidentes o buscar uno específico por su ID.
- **Actualización de Estado**: Permite al personal de serenazgo atender un incidente, cambiando su estado y añadiendo comentarios.
- **Eliminación de Incidentes**: Funcionalidad administrativa para purgar reportes falsos o incorrectos.
- **Notificaciones en Tiempo Real**: Utiliza un servidor de Sockets para notificar a las consolas de los agentes de serenazgo conectados cada vez que se registra un nuevo incidente.

## Tecnologías Utilizadas

- **Lenguaje**: Java 25
- **Framework**: Spring Boot 3+
- **Persistencia**: Spring Data JPA con Hibernate
- **Base de Datos**: MySQL
- **Comunicación**: API RESTful y Sockets TCP
- **Service Discovery**: Cliente Eureka para registro en el servidor de descubrimiento.

---

## Pruebas con Postman

Para probar el microservicio, asegúrate de que la aplicación esté corriendo (puerto `8082` por defecto) y que la base de datos MySQL esté accesible.

### 1. Reportar un Nuevo Incidente

- **Método**: `POST`
- **URL**: `http://localhost:8082/api/incidentes`
- **Body**: `raw` > `JSON`

**Ejemplo de Body:**
```json
{
    "nombresCiudadano": "Ana",
    "apellidosCiudadano": "García",
    "esAnonimo": false,
    "descripcion": "Actividad sospechosa en el parque. Un grupo de individuos merodeando vehículos.",
    "ubicacionExacta": "Parque Central, frente a la fuente",
    "prioridad": "MEDIA"
}
```

### 2. Listar Todos los Incidentes

- **Método**: `GET`
- **URL**: `http://localhost:8082/api/incidentes`

### 3. Buscar un Incidente por ID

- **Método**: `GET`
- **URL**: `http://localhost:8082/api/incidentes/1` (reemplaza `1` con un ID válido)

### 4. Atender un Incidente (Acción de Serenazgo)

Permite cambiar el estado de un incidente y agregar un comentario.

- **Método**: `PUT`
- **URL**: `http://localhost:8082/api/incidentes/1/atencion` (reemplaza `1` con un ID válido)
- **Body**: `raw` > `JSON`

**Ejemplo de Body:**
```json
{
    "estado": "EN_PROCESO",
    "comentario": "Patrulla asignada. Se dirige al lugar."
}
```
*Valores de `estado` permitidos: `PENDIENTE`, `EN_PROCESO`, `ATENDIDO`, `ANULADO`.*

### 5. Eliminar un Incidente (Acción de Administrador)

- **Método**: `DELETE`
- **URL**: `http://localhost:8082/api/incidentes/1` (reemplaza `1` con un ID válido)
