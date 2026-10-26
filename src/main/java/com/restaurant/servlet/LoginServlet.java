package com.restaurant.servlet;

import com.restaurant.db.UsuarioDAO;
import com.restaurant.db.UsuarioDAO.AuthException;
import com.restaurant.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * ============================================================
 *  Servlet de Autenticación – LoginServlet
 * ============================================================
 *  URL mapeada:  /login
 *
 *  GET  → Muestra la página de login (login.jsp)
 *  POST → Procesa las credenciales ingresadas
 *
 *  Etapa 2: Lógica de autenticación con BD
 *  Etapa 3: Manejo de bloqueo y seguridad de sesión
 * ============================================================
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // ----------------------------------------------------------------
    //  GET: mostrar formulario de login
    // ----------------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Si ya tiene sesión activa, redirigir al dashboard
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuarioLogueado") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
            return;
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    // ----------------------------------------------------------------
    //  POST: procesar credenciales del formulario
    // ----------------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Codificación UTF-8
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String correo    = request.getParameter("correo");
        String password  = request.getParameter("password");

        // ── Validación básica de campos vacíos ──────────────────────
        if (correo == null || correo.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMensaje", "Por favor complete todos los campos.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // ── Validación de formato de correo Gmail ────────────────────
        if (!correo.trim().toLowerCase().endsWith("@gmail.com")
                && !correo.trim().toLowerCase().endsWith("@restaurant.com")) {
            request.setAttribute("errorMensaje", "Ingrese un correo Gmail válido.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        try {
            // ── Autenticación (Etapa 2 + 3) ──────────────────────────
            Usuario usuario = usuarioDAO.autenticar(correo.trim(), password);

            // LOGIN EXITOSO ─────────────────────────────────────────
            // Invalidar sesión anterior y crear nueva (seguridad)
            HttpSession sessionAnterior = request.getSession(false);
            if (sessionAnterior != null) {
                sessionAnterior.invalidate();
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("usuarioLogueado", usuario);
            session.setAttribute("nombreUsuario",   usuario.getNombre() + " " + usuario.getApellido());
            session.setAttribute("rolUsuario",      usuario.getRol());
            session.setMaxInactiveInterval(30 * 60); // 30 minutos de inactividad

            // Redirigir al dashboard
            response.sendRedirect(request.getContextPath() + "/dashboard.jsp");

        } catch (AuthException e) {
            // LOGIN FALLIDO ─────────────────────────────────────────
            boolean cuentaBloqueada = e.getMessage().contains("bloqueada");

            request.setAttribute("errorMensaje",    e.getMessage());
            request.setAttribute("cuentaBloqueada", cuentaBloqueada);

            // Si la cuenta fue bloqueada, redirigir con flag para cerrar ventana (JS)
            if (cuentaBloqueada) {
                request.setAttribute("accionBloqueo", true);
            }

            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
