<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.LocalDateTime, java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login – Sistema de Gestión de Inventario</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/login.css">
</head>
<body>

<%
    /* ────────────────────────────────────────────────────────────
       Si ya hay sesión activa, redirigir al dashboard
    ──────────────────────────────────────────────────────────── */
    HttpSession sesionActual = request.getSession(false);
    if (sesionActual != null && sesionActual.getAttribute("usuarioLogueado") != null) {
        response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
        return;
    }

    /* Leer atributos enviados por LoginServlet */
    String errorMensaje   = (String)  request.getAttribute("errorMensaje");
    Boolean bloqueado     = (Boolean) request.getAttribute("cuentaBloqueada");
    Boolean accionBloqueo = (Boolean) request.getAttribute("accionBloqueo");
    if (bloqueado      == null) bloqueado      = false;
    if (accionBloqueo  == null) accionBloqueo  = false;
%>

<!-- ══════════════════════════════════════════════════
     CONTENEDOR PRINCIPAL DE LA PÁGINA
══════════════════════════════════════════════════ -->
<div class="page-wrapper">
    <div class="login-card">

        <!-- ── LOGO DEL RESTAURANT ─────────────────────────── -->
        <div class="logo-area">
            <!-- SVG inline del logo: círculo oscuro con tenedor y cuchillo -->
            <svg xmlns="http://www.w3.org/2000/svg"
                 viewBox="0 0 120 120"
                 class="logo-svg"
                 aria-label="Logo Restaurant">
                <!-- Círculo oscuro de fondo -->
                <circle cx="60" cy="55" r="50" fill="#3d3d3d"/>
                <!-- TENEDOR: mango + 3 dientes -->
                <g fill="#ffffff">
                    <!-- Mango del tenedor -->
                    <rect x="43" y="65" width="6" height="22" rx="3"/>
                    <!-- Varilla central del tenedor -->
                    <rect x="45" y="32" width="2" height="25"/>
                    <!-- Diente izquierdo -->
                    <rect x="38" y="28" width="2" height="18" rx="1"/>
                    <!-- Diente central -->
                    <rect x="45" y="28" width="2" height="18" rx="1"/>
                    <!-- Diente derecho -->
                    <rect x="52" y="28" width="2" height="18" rx="1"/>
                    <!-- Base de los dientes (horizontal) -->
                    <rect x="38" y="44" width="18" height="3" rx="1"/>
                </g>
                <!-- CUCHILLO -->
                <g fill="#ffffff">
                    <!-- Mango del cuchillo -->
                    <rect x="69" y="65" width="6" height="22" rx="3"/>
                    <!-- Hoja del cuchillo (forma de cuchillo) -->
                    <path d="M69 28 L75 28 Q80 38 75 55 L69 55 Z"/>
                </g>
            </svg>
            <p class="logo-text">Restaurant</p>
        </div>

        <!-- ── TÍTULO ──────────────────────────────────────── -->
        <h1 class="titulo-principal">Sistema de Gestión de Inventario</h1>

        <!-- ── MENSAJE DE ERROR (si existe) ────────────────── -->
        <% if (errorMensaje != null && !errorMensaje.isEmpty()) { %>
            <div class="alert-error <%= bloqueado ? "alert-bloqueado" : "" %>">
                <span class="alert-icon"><%= bloqueado ? "🔒" : "⚠️" %></span>
                <%= errorMensaje %>
            </div>
        <% } %>

        <!-- ── FORMULARIO DE LOGIN ──────────────────────────── -->
        <form id="loginForm"
              action="<%= request.getContextPath() %>/login"
              method="POST"
              novalidate>

            <div class="form-group">
                <input type="email"
                       id="correo"
                       name="correo"
                       class="form-input"
                       placeholder="Correo Gmail"
                       autocomplete="email"
                       required
                       maxlength="150"
                       value="<%= request.getParameter("correo") != null
                                   ? org.apache.commons.lang3.StringEscapeUtils
                                       .escapeHtml4(request.getParameter("correo"))
                                   : "" %>"/>
            </div>

            <div class="form-group">
                <input type="password"
                       id="password"
                       name="password"
                       class="form-input"
                       placeholder="Contraseña"
                       autocomplete="current-password"
                       required
                       maxlength="100"/>
            </div>

            <!-- Botón principal -->
            <button type="submit" class="btn-ingresar" id="btnIngresar">
                Ingresar
            </button>

        </form>

        <!-- ── ENLACE CREAR CUENTA ─────────────────────────── -->
        <p class="crear-cuenta-texto">
            ¿No tienes cuenta?
            <a href="<%= request.getContextPath() %>/registro.jsp"
               class="crear-cuenta-link">Crear cuenta</a>
        </p>

        <!-- ── PIE DE PÁGINA ───────────────────────────────── -->
        <footer class="footer">
            &copy; 2026 Sistema de Inventario Restaurant
        </footer>

    </div><!-- fin login-card -->
</div><!-- fin page-wrapper -->

<!-- ══════════════════════════════════════════════════
     SCRIPTS
══════════════════════════════════════════════════ -->
<script>
/* ────────────────────────────────────────────────────────────
   Etapa 3 – Validación del lado cliente + cierre de sesión
   si la cuenta fue bloqueada
──────────────────────────────────────────────────────────── */

// Indicador de bloqueo enviado desde el Servlet
var cuentaBloqueada = <%= accionBloqueo %>;

if (cuentaBloqueada) {
    /* Mostrar alerta y cerrar la ventana/tab por seguridad */
    setTimeout(function () {
        alert("⚠️ Tu cuenta ha sido bloqueada temporalmente.\n" +
              "La sesión se cerrará por seguridad.");
        // Intentar cerrar la ventana (funciona si fue abierta por JS)
        window.close();
        // Si window.close() no cierra (pestañas directas),
        // redirigir a página de bloqueo como alternativa
        window.location.href = "<%= request.getContextPath() %>/bloqueado.jsp";
    }, 800);
}

/* ── Validación del formulario en el cliente ── */
document.getElementById("loginForm").addEventListener("submit", function (e) {
    var correo   = document.getElementById("correo").value.trim();
    var password = document.getElementById("password").value;

    if (!correo || !password) {
        e.preventDefault();
        mostrarErrorCliente("Por favor complete todos los campos.");
        return;
    }

    // Validación de formato de correo
    var regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!regexEmail.test(correo)) {
        e.preventDefault();
        mostrarErrorCliente("Ingrese un correo electrónico válido.");
        return;
    }

    // Deshabilitar botón para evitar doble submit
    document.getElementById("btnIngresar").disabled = true;
    document.getElementById("btnIngresar").textContent = "Verificando...";
});

function mostrarErrorCliente(mensaje) {
    // Reutilizar el div de error si ya existe, o crear uno nuevo
    var existente = document.querySelector(".alert-error");
    if (existente) {
        existente.innerHTML = "<span class='alert-icon'>⚠️</span> " + mensaje;
        existente.style.display = "flex";
    } else {
        var div = document.createElement("div");
        div.className = "alert-error";
        div.innerHTML = "<span class='alert-icon'>⚠️</span> " + mensaje;
        var form = document.getElementById("loginForm");
        form.parentNode.insertBefore(div, form);
    }
}
</script>

</body>
</html>
