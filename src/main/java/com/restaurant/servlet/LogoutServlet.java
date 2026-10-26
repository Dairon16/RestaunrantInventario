package com.restaurant.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Servlet de cierre de sesión.
 * URL mapeada: /logout
 *
 * Invalida la sesión HTTP y redirige al login.
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Invalidar sesión actual
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            System.out.println("[LogoutServlet] Sesión invalidada correctamente.");
        }

        // Limpiar la cookie JSESSIONID por seguridad
        Cookie cookie = new Cookie("JSESSIONID", "");
        cookie.setMaxAge(0);
        cookie.setPath(request.getContextPath());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        // Evitar caché del navegador
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Redirigir al login
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
