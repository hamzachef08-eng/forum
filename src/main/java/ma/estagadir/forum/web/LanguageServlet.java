package ma.estagadir.forum.web;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LanguageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String lang = req.getParameter("lang");
        if (!"en".equalsIgnoreCase(lang) && !"fr".equalsIgnoreCase(lang)) {
            lang = "fr";
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("lang", lang.toLowerCase());

        String back = sanitizeBackPath(req.getParameter("back"));
        resp.sendRedirect(req.getContextPath() + back);
    }

    private String sanitizeBackPath(String back) {
        if (back == null) {
            return "/login";
        }

        String value = back.trim();
        if (value.isEmpty() || !value.startsWith("/")) {
            return "/login";
        }

        // Never redirect to internal JSP paths.
        if (value.startsWith("/WEB-INF/") || value.contains("/WEB-INF/") || value.endsWith(".jsp")) {
            return "/login";
        }

        // Allow only known public routes.
        if ("/".equals(value)
                || value.startsWith("/login")
                || value.startsWith("/dashboard")
                || value.startsWith("/register")
                || value.startsWith("/forgot-password")
                || value.startsWith("/reset-password")
                || value.startsWith("/blog")
                || value.startsWith("/article")
                || value.startsWith("/module/chat")
                || value.startsWith("/account")
                || value.startsWith("/student/semester")
                || value.startsWith("/admin/reports")
                || value.startsWith("/admin/banned")) {
            return value;
        }

        return "/login";
    }
}
