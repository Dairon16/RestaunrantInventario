-- ============================================================
--  BASE DE DATOS: Sistema de Gestión de Inventario Restaurant
--  Compatible con MySQL 8.x
-- ============================================================

CREATE DATABASE IF NOT EXISTS restaurant_inventario
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE restaurant_inventario;

-- ------------------------------------------------------------
--  TABLA: usuarios
--  Campos básicos obligatorios (Etapa 3 - reestructuración)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(100)        NOT NULL,
    apellido        VARCHAR(100)        NOT NULL,
    correo          VARCHAR(150)        NOT NULL UNIQUE,
    contrasena      VARCHAR(255)        NOT NULL,   -- almacenada con SHA-256 hex
    rol             ENUM('ADMIN','EMPLEADO') NOT NULL DEFAULT 'EMPLEADO',
    activo          TINYINT(1)          NOT NULL DEFAULT 1,
    intentos_fallidos INT               NOT NULL DEFAULT 0,
    bloqueado_hasta DATETIME            NULL,       -- NULL = no bloqueado
    fecha_creacion  DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
--  DATOS DE PRUEBA
--  Contraseña "Admin123!" encriptada con SHA-256
--  SHA-256("Admin123!") = a7b9c2d4e1f08362... (ver PasswordUtil.java)
--  Ejecuta el siguiente INSERT SOLO después de obtener el hash
--  desde la clase PasswordUtil o usa el valor ya calculado:
-- ------------------------------------------------------------

-- Hash SHA-256 de "Admin123!" (calculado con Java MessageDigest):
-- Puedes verificarlo ejecutando PasswordUtil.hashPassword("Admin123!")
INSERT INTO usuarios (nombre, apellido, correo, contrasena, rol, activo)
VALUES (
    'Administrador',
    'Sistema',
    'admin@restaurant.com',
    '3eb3fe66b31e3b4d10fa70b5cad49c7112294af6ae4e476a1c405155d45aa121',  -- SHA-256 de "Admin123!"
    'ADMIN',
    1
) ON DUPLICATE KEY UPDATE contrasena = VALUES(contrasena), intentos_fallidos = 0, bloqueado_hasta = NULL;

-- Usuario empleado de prueba (contraseña: "Empleado456@")
INSERT INTO usuarios (nombre, apellido, correo, contrasena, rol, activo)
VALUES (
    'Juan',
    'Pérez',
    'juan.perez@restaurant.com',
    'd85a9db41e73d9f251c10904ac53ad9064aa1b7e67e460eb6bf696101b8090fb',  -- SHA-256 de "Empleado456@"
    'EMPLEADO',
    1
) ON DUPLICATE KEY UPDATE contrasena = VALUES(contrasena), intentos_fallidos = 0, bloqueado_hasta = NULL;

-- ============================================================
--  NOTA: Reemplaza los hashes con los generados por
--  PasswordUtil.hashPassword("tuContraseña") al iniciar el
--  servidor por primera vez.
-- ============================================================
