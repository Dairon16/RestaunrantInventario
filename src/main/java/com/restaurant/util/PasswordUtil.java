package com.restaurant.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad para encriptar contraseñas con SHA-256.
 * Patrón Singleton implícito: solo métodos estáticos, no instanciable.
 *
 * Etapa 3 - Seguridad: La contraseña se almacena como hash SHA-256
 * en representación hexadecimal de 64 caracteres.
 */
public final class PasswordUtil {

    // Clase utilitaria: constructor privado para evitar instanciación
    private PasswordUtil() {
        throw new UnsupportedOperationException("Clase utilitaria, no instanciable.");
    }

    /**
     * Genera el hash SHA-256 de una contraseña en texto plano.
     *
     * @param passwordPlain Contraseña en texto plano.
     * @return Hash hexadecimal de 64 caracteres, o null si hay error.
     */
    public static String hashPassword(String passwordPlain) {
        if (passwordPlain == null || passwordPlain.isEmpty()) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(passwordPlain.getBytes("UTF-8"));
            // Convertir bytes a representación hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            System.err.println("[PasswordUtil] Error al generar hash: " + e.getMessage());
            return null;
        }
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash almacenado.
     *
     * @param passwordPlain   Contraseña ingresada por el usuario.
     * @param storedHash      Hash SHA-256 almacenado en la base de datos.
     * @return true si coinciden, false en caso contrario.
     */
    public static boolean verifyPassword(String passwordPlain, String storedHash) {
        if (passwordPlain == null || storedHash == null) {
            return false;
        }
        String hashIngresado = hashPassword(passwordPlain);
        return storedHash.equalsIgnoreCase(hashIngresado);
    }

    // ----------------------------------------------------------------
    //  MAIN de prueba: ejecutar para obtener hashes de contraseñas
    //  Úsalo para generar los INSERT del archivo database.sql
    // ----------------------------------------------------------------
    public static void main(String[] args) {
        String[] contraseñas = {"Admin123!", "Empleado456@"};
        for (String pass : contraseñas) {
            System.out.println("SHA-256(\"" + pass + "\") = " + hashPassword(pass));
        }
    }
}
