<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.restaurant.model.Usuario" %>
<%
    /* ── Protección de ruta: solo usuarios autenticados ── */
    HttpSession sesion = request.getSession(false);
    if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    Usuario usuario = (Usuario) sesion.getAttribute("usuarioLogueado");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard – Sistema de Inventario Restaurant</title>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Arial, sans-serif;
               background: #f0f2f5; margin: 0; }
        .header { background: #1a73e8; color: #fff; padding: 16px 32px;
                  display: flex; justify-content: space-between; align-items: center; }
        .header h2 { font-size: 1.1rem; font-weight: 600; }
        .header .user-info { font-size: 0.9rem; display: flex; align-items: center; gap: 16px; }
        .btn-logout { background: rgba(255,255,255,0.2); border: 1px solid rgba(255,255,255,0.4);
                      color: #fff; padding: 8px 16px; border-radius: 8px; cursor: pointer;
                      font-size: 0.85rem; text-decoration: none; transition: background 0.2s; }
        .btn-logout:hover { background: rgba(255,255,255,0.3); }
        .container { max-width: 900px; margin: 40px auto; padding: 0 20px; }
        .welcome-card { background: #fff; border-radius: 12px;
                        box-shadow: 0 2px 12px rgba(0,0,0,0.08); padding: 32px; text-align: center; }
        .welcome-card h1 { color: #1a1a2e; font-size: 1.6rem; margin-bottom: 8px; }
        .badge { display: inline-block; background: #e8f0fe; color: #1a73e8;
                 padding: 4px 12px; border-radius: 20px; font-size: 0.8rem; font-weight: 600;
                 margin-top: 8px; }
        .info { margin-top: 24px; color: #666; font-size: 0.95rem; }
    </style>
</head>
<body>

<header class="header">
    <h2>🍽️ Sistema de Gestión de Inventario – Restaurant</h2>
    <div class="user-info">
        <span>👤 <%= usuario.getNombre() %> <%= usuario.getApellido() %></span>
        <a href="<%= request.getContextPath() %>/logout" class="btn-logout">Cerrar sesión</a>
    </div>
</header>

<div class="container">
    <div class="welcome-card">
        <h1>¡Bienvenido, <%= usuario.getNombre() %>!</h1>
        <span class="badge"><%= usuario.getRol() %></span>
        <p class="info">
            Has iniciado sesión correctamente en el Sistema de Gestión de Inventario.<br>
            <small style="color:#aaa">Correo: <%= usuario.getCorreo() %></small>
        </p>
        <p style="margin-top:24px; color:#999; font-size:0.85rem;">
            ℹ️ El módulo de inventario se integrará en las siguientes etapas del proyecto.
        </p>
    </div>
</div>

</body>
</html>
