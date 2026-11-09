# Microservicio de autenticacion (auth-service)

Microservicio encargado del login y registro de usuarios para el proyecto de alerta vecinal.

## Tecnologías
- Java 25
- Spring Boot 4.0.6
- Spring Security (JWT)
- Spring Data JPA
- MySQL

## Base de Datos
Se creó una base de datos propia para no mezclar tablas con los otros servicios:
- Nombre: `alerta_auth_db`
- Puerto: 3306

*(la tablas se generan al correr el proyecto por primera vez gracias al ddl-auto=update de hibernate)*

## Puerto
El servicio levanta en el puerto **8081**.

## Rutas para probar en Postman

### 1. Registro
- POST `http://localhost:8081/api/auth/register`
Body (JSON):
```json
{
    "username": "hector",
    "password": "admin123",
    "rol": "ROLE_ADMIN" // ROLE_VECINO, ROLE_SERENO, ROLE_ADMIN
}
```

### 2. Login
- POST `http://localhost:8081/api/auth/login`
Body (JSON):
```json
{
    "username": "hector",
    "password": "admin123"
}
```
*esto devuelve el token JWT que se usará como Bearer Token para consumir los demás servicios*

## Detalles tecnicos
- contraseñas encriptadas con BCrypt.
- uso de clases DTO (GenericResponseDto y ErrorMessage) con lombok `@Builder`  que vi en clase

