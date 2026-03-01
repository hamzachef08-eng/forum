package ma.estagadir.forum.web;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.estagadir.forum.dao.PasswordResetDao;
import ma.estagadir.forum.dao.UserDao;
import ma.estagadir.forum.model.User;

public class VerifyResetCodeServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();
    private final PasswordResetDao resetDao = new PasswordResetDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String code = req.getParameter("code");

        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json");

        if (isBlank(email) || isBlank(code) || !isGmail(email) || !code.trim().matches("\\d{6}")) {
            resp.getWriter().write("{\"valid\":false}");
            return;
        }

        try {
            User user = userDao.findByEmail(email.trim().toLowerCase());
            if (user == null) {
                resp.getWriter().write("{\"valid\":false}");
                return;
            }

            Long tokenId = resetDao.findValidTokenId(user.getId(), code.trim());
            resp.getWriter().write(tokenId == null ? "{\"valid\":false}" : "{\"valid\":true}");
        } catch (SQLException e) {
            throw new ServletException("Unable to verify reset code", e);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isGmail(String email) {
        return email != null && email.trim().toLowerCase().endsWith("@gmail.com");
    }
}
