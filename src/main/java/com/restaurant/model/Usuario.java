package com.restaurant.model;

import java.sql.Timestamp;

/**
 * Modelo de la entidad Usuario.
 * Refleja exactamente los campos de la tabla "usuarios" en la BD.
 *
 * Etapa 3 - Reestructuración: incluye campos de seguridad
 * (intentosFallidos, bloqueadoHasta) para gestión de bloqueo.
 */
public class Usuario {

    // ----------------------------------------------------------------
    //  Atributos (mapean columnas de la tabla usuarios)
    // ----------------------------------------------------------------
    private int       id;
    private String    nombre;
    private String    apellido;
    private String    correo;
    private String    contrasena;          // Hash SHA-256
    private String    rol;                 // ADMIN | EMPLEADO
    private boolean   activo;
    private int       intentosFallidos;    // Contador de intentos erróneos
    private Timestamp bloqueadoHasta;      // NULL = cuenta no bloqueada
    private Timestamp fechaCreacion;
    private Timestamp fechaActualizacion;

    // ----------------------------------------------------------------
    //  Constructor vacío
    // ----------------------------------------------------------------
    public Usuario() {}

    // ----------------------------------------------------------------
    //  Constructor principal (para creación desde BD)
    // ----------------------------------------------------------------
    public Usuario(int id, String nombre, String apellido, String correo,
                   String contrasena, String rol, boolean activo,
                   int intentosFallidos, Timestamp bloqueadoHasta,
                   Timestamp fechaCreacion, Timestamp fechaActualizacion) {
        this.id                  = id;
        this.nombre              = nombre;
        this.apellido            = apellido;
        this.correo              = correo;
        this.contrasena          = contrasena;
        this.rol                 = rol;
        this.activo              = activo;
        this.intentosFallidos    = intentosFallidos;
        this.bloqueadoHasta      = bloqueadoHasta;
        this.fechaCreacion       = fechaCreacion;
        this.fechaActualizacion  = fechaActualizacion;
    }

    // ----------------------------------------------------------------
    //  Método de conveniencia: ¿La cuenta está actualmente bloqueada?
    // ----------------------------------------------------------------
    public boolean estaBloqueado() {
        if (bloqueadoHasta == null) return false;
        return bloqueadoHasta.after(new java.util.Date());
    }

    // ----------------------------------------------------------------
    //  Getters y Setters
    // ----------------------------------------------------------------
    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public String getNombre()                   { return nombre; }
    public void setNombre(String nombre)        { this.nombre = nombre; }

    public String getApellido()                 { return apellido; }
    public void setApellido(String apellido)    { this.apellido = apellido; }

    public String getCorreo()                   { return correo; }
    public void setCorreo(String correo)        { this.correo = correo; }

    public String getContrasena()               { return contrasena; }
    public void setContrasena(String c)         { this.contrasena = c; }

    public String getRol()                      { return rol; }
    public void setRol(String rol)              { this.rol = rol; }

    public boolean isActivo()                   { return activo; }
    public void setActivo(boolean activo)       { this.activo = activo; }

    public int getIntentosFallidos()            { return intentosFallidos; }
    public void setIntentosFallidos(int i)      { this.intentosFallidos = i; }

    public Timestamp getBloqueadoHasta()        { return bloqueadoHasta; }
    public void setBloqueadoHasta(Timestamp t)  { this.bloqueadoHasta = t; }

    public Timestamp getFechaCreacion()         { return fechaCreacion; }
    public void setFechaCreacion(Timestamp t)   { this.fechaCreacion = t; }

    public Timestamp getFechaActualizacion()    { return fechaActualizacion; }
    public void setFechaActualizacion(Timestamp t) { this.fechaActualizacion = t; }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", correo='" + correo + "', rol='" + rol + "'}";
    }
}
