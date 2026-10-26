package com.restaurant.db;

import com.restaurant.model.Usuario;
import com.restaurant.util.PasswordUtil;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * DAO (Data Access Object) para la entidad Usuario.
 *
 * Centraliza todas las operaciones SQL relacionadas con la tabla "usuarios".
 * Usa ConexionDB (Singleton) para obtener la conexión.
 *
 * Etapa 2 – Lógica de autenticación
 * Etapa 3 – Gestión de intentos fallidos y bloqueo temporal
 */
public class UsuarioDAO {

    // Máximo de intentos antes de bloquear la cuenta (Etapa 3)
    private static final int MAX_INTENTOS   = 3;
    // Duración del bloqueo en minutos
    private static final int BLOQUEO_MINUTOS = 15;

    // ----------------------------------------------------------------
    //  Obtiene la conexión desde el Singleton
    // ----------------------------------------------------------------
    private Connection getConn() throws SQLException {
        return ConexionDB.getInstance().getConnection();
    }

    // ----------------------------------------------------------------
    //  Busca un usuario por su correo electrónico
    // ----------------------------------------------------------------
    public Usuario buscarPorCorreo(String correo) {
        String sql = "SELECT id, nombre, apellido, correo, contrasena, rol, activo, "
                   + "intentos_fallidos, bloqueado_hasta, fecha_creacion, fecha_actualizacion "
                   + "FROM usuarios WHERE correo = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, correo.trim().toLowerCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearResultado(rs);
            }
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al buscar usuario: " + e.getMessage());
        }
        return null;
    }

    // ----------------------------------------------------------------
    //  AUTENTICACIÓN PRINCIPAL (Etapa 2 + Etapa 3)
    //
    //  Retorna:
    //    - El objeto Usuario si las credenciales son válidas
    //    - null si el usuario no existe o la contraseña es incorrecta
    //
    //  Lanza AuthException con mensaje descriptivo si:
    //    - La cuenta está bloqueada
    //    - La cuenta está inactiva
    //    - Se alcanza el máximo de intentos (bloquea automáticamente)
    // ----------------------------------------------------------------
    public Usuario autenticar(String correo, String passwordIngresado) throws AuthException {
        Usuario usuario = buscarPorCorreo(correo);

        // 1. Usuario no existe
        if (usuario == null) {
            throw new AuthException("Correo o contraseña incorrectos.");
        }

        // 2. Cuenta inactiva
        if (!usuario.isActivo()) {
            throw new AuthException("La cuenta está desactivada. Contacte al administrador.");
        }

        // 3. Cuenta bloqueada temporalmente
        if (usuario.estaBloqueado()) {
            String hasta = usuario.getBloqueadoHasta().toLocalDateTime()
                              .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            throw new AuthException("Cuenta bloqueada por " + BLOQUEO_MINUTOS
                    + " minutos tras 3 intentos fallidos. Intente de nuevo después de las " + hasta + ".");
        }

        // 4. Verificar contraseña (SHA-256)
        boolean passwordCorrecta = PasswordUtil.verifyPassword(passwordIngresado, usuario.getContrasena());

        if (!passwordCorrecta) {
            // Incrementar contador de intentos fallidos
            int nuevosIntentos = usuario.getIntentosFallidos() + 1;

            if (nuevosIntentos >= MAX_INTENTOS) {
                // Bloquear cuenta por BLOQUEO_MINUTOS minutos
                bloquearCuenta(usuario.getId(), BLOQUEO_MINUTOS);
                throw new AuthException("Has alcanzado 3 intentos fallidos. "
                        + "Tu cuenta ha sido bloqueada por " + BLOQUEO_MINUTOS
                        + " minutos por seguridad.");
            } else {
                // Registrar intento fallido
                actualizarIntentosFallidos(usuario.getId(), nuevosIntentos);
                int restantes = MAX_INTENTOS - nuevosIntentos;
                throw new AuthException("Correo o contraseña incorrectos. "
                        + "Te quedan " + restantes + " intento(s) antes del bloqueo.");
            }
        }

        // 5. Login exitoso: resetear contador de intentos
        resetearIntentos(usuario.getId());
        return usuario;
    }

    // ----------------------------------------------------------------
    //  Actualiza el contador de intentos fallidos en BD
    // ----------------------------------------------------------------
    private void actualizarIntentosFallidos(int idUsuario, int intentos) {
        String sql = "UPDATE usuarios SET intentos_fallidos = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, intentos);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al actualizar intentos: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    //  Bloquea la cuenta por N minutos
    // ----------------------------------------------------------------
    private void bloquearCuenta(int idUsuario, int minutos) {
        String sql = "UPDATE usuarios SET intentos_fallidos = ?, bloqueado_hasta = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            Timestamp hastaTs = Timestamp.valueOf(LocalDateTime.now().plusMinutes(minutos));
            ps.setInt(1, MAX_INTENTOS);
            ps.setTimestamp(2, hastaTs);
            ps.setInt(3, idUsuario);
            ps.executeUpdate();
            System.out.println("[UsuarioDAO] Cuenta ID=" + idUsuario + " bloqueada hasta " + hastaTs);
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al bloquear cuenta: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    //  Resetea intentos fallidos y desbloquea la cuenta tras login OK
    // ----------------------------------------------------------------
    private void resetearIntentos(int idUsuario) {
        String sql = "UPDATE usuarios SET intentos_fallidos = 0, bloqueado_hasta = NULL WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al resetear intentos: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    //  Mapea un ResultSet a un objeto Usuario
    // ----------------------------------------------------------------
    private Usuario mapearResultado(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("apellido"),
            rs.getString("correo"),
            rs.getString("contrasena"),
            rs.getString("rol"),
            rs.getBoolean("activo"),
            rs.getInt("intentos_fallidos"),
            rs.getTimestamp("bloqueado_hasta"),
            rs.getTimestamp("fecha_creacion"),
            rs.getTimestamp("fecha_actualizacion")
        );
    }

    // ----------------------------------------------------------------
    //  Excepción interna para errores de autenticación
    // ----------------------------------------------------------------
    public static class AuthException extends Exception {
        public AuthException(String mensaje) {
            super(mensaje);
        }
    }
}
