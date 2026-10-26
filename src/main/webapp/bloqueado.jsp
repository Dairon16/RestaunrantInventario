<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cuenta Bloqueada</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/login.css">
    <style>
        .bloqueo-card {
            max-width: 420px;
            text-align: center;
            padding: 48px 40px;
        }
        .icono-bloqueo { font-size: 4rem; margin-bottom: 16px; }
        .titulo-bloqueo { color: #e65100; font-size: 1.3rem; font-weight: 700; margin-bottom: 12px; }
        .desc-bloqueo { color: #666; font-size: 0.95rem; line-height: 1.6; margin-bottom: 24px; }
        .btn-volver { display: inline-block; padding: 12px 28px; background: #1a73e8;
                      color: #fff; border-radius: 8px; text-decoration: none;
                      font-weight: 600; font-size: 0.95rem; transition: background 0.2s; }
        .btn-volver:hover { background: #1248a0; }
        .contador { font-size: 0.85rem; color: #999; margin-top: 16px; }
    </style>
</head>
<body>
<div class="page-wrapper">
    <div class="login-card bloqueo-card">
        <div class="icono-bloqueo">🔒</div>
        <h1 class="titulo-bloqueo">Cuenta Bloqueada</h1>
        <p class="desc-bloqueo">
            Tu cuenta ha sido bloqueada temporalmente durante <strong>15 minutos</strong>
            debido a 3 intentos fallidos de inicio de sesión consecutivos.<br><br>
            Por tu seguridad, la sesión ha sido cerrada.
        </p>
        <a href="<%= request.getContextPath() %>/login" class="btn-volver">
            Intentar de nuevo
        </a>
        <p class="contador" id="contador"></p>
    </div>
</div>

<script>
/* Cuenta regresiva de 15 minutos */
var segundosTotales = 15 * 60;
var intervalo = setInterval(function () {
    if (segundosTotales <= 0) {
        clearInterval(intervalo);
        document.getElementById("contador").textContent = "Ya puedes intentar ingresar.";
        return;
    }
    var min = Math.floor(segundosTotales / 60);
    var seg = segundosTotales % 60;
    document.getElementById("contador").textContent =
        "Tiempo restante: " + min + ":" + (seg < 10 ? "0" : "") + seg;
    segundosTotales--;
}, 1000);
</script>
</body>
</html>
