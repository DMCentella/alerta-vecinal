-- Script BD - Alerta Vecinal
-- Ojo: Correr este script completo en MySQL antes de levantar el backend.
-- 
-- Orden sugerido para darle run a los proyectos en Spring:
-- 1) incident-service (8082 - de este dependen los demas)
-- 2) auth-service (8081)
-- 3) serenazgo-service (8080)
-- 4) admin-service (8083)

-- BD de Autenticacion (8081)
CREATE DATABASE IF NOT EXISTS alerta_auth_db;
USE alerta_auth_db;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(255),
    nombres VARCHAR(255),
    apellidos VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    telefono VARCHAR(255),
    dni VARCHAR(255),
    activo BIT(1) DEFAULT 1,
    fecha_registro DATETIME(6)
);

-- BD de Incidentes (8082)
CREATE DATABASE IF NOT EXISTS bd_alerta_incidentes;
USE bd_alerta_incidentes;

CREATE TABLE IF NOT EXISTS incidentes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombres_ciudadano VARCHAR(255),
    apellidos_ciudadano VARCHAR(255),
    tipo VARCHAR(255),
    descripcion VARCHAR(255),
    ubicacion_exacta VARCHAR(255),
    prioridad VARCHAR(255),
    estado ENUM('PENDIENTE','EN_PROCESO','ATENDIDO','ANULADO') DEFAULT 'PENDIENTE',
    comentario_serenazgo VARCHAR(255),
    fecha_creacion DATETIME(6),
    fecha_actualizacion DATETIME(6)
);

-- BD para el Serenazgo (8080)
CREATE DATABASE IF NOT EXISTS serenazgo_bd;
USE serenazgo_bd;

CREATE TABLE IF NOT EXISTS historial_estados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    incidente_id BIGINT,
    serenazgo_id BIGINT,
    estado_anterior ENUM('PENDIENTE','EN_PROCESO','ATENDIDO','ANULADO'),
    estado_nuevo ENUM('PENDIENTE','EN_PROCESO','ATENDIDO','ANULADO'),
    fecha_cambio DATETIME(6)
);

-- BD para el Backoffice/Admin (8083)
CREATE DATABASE IF NOT EXISTS bd_alerta_admin;
USE bd_alerta_admin;

CREATE TABLE IF NOT EXISTS parametricas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(255),
    valor VARCHAR(255),
    descripcion VARCHAR(255),
    activo BIT(1) DEFAULT 1,
    fecha_creacion DATETIME(6)
);
