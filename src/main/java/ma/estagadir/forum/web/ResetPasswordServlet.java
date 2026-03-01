package ma.estagadir.forum.web;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.estagadir.forum.dao.PasswordResetDao;
import ma.estagadir.forum.dao.UserDao;
import ma.estagadir.forum.model.User;
import ma.estagadir.forum.util.I18n;
import ma.estagadir.forum.util.PasswordUtil;

public class ResetPasswordServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();
    private final PasswordResetDao resetDao = new PasswordResetDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");
        String code = req.getParameter("code");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (isBlank(email) || isBlank(newPassword) || isBlank(confirmPassword)) {
            req.setAttribute("error", I18n.t(req, "err.all_fields_required"));
            req.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(req, resp);
            return;
        }

        if (isBlank(code) || !code.trim().matches("\\d{6}")) {
            req.setAttribute("error", I18n.t(req, "err.code_6_digits"));
            req.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(req, resp);
            return;
        }

        String normalizedEmail = email.trim().toLowerCase();
        if (!normalizedEmail.endsWith("@gmail.com")) {
            req.setAttribute("error", I18n.t(req, "err.gmail_only"));
            req.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(req, resp);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("error", I18n.t(req, "err.password_mismatch"));
            req.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(req, resp);
            return;
        }

        try {
            User user = userDao.findByEmail(normalizedEmail);
            if (user == null) {
                req.setAttribute("error", I18n.t(req, "err.invalid_email_or_code"));
                req.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(req, resp);
                return;
            }

            Long tokenId = resetDao.findValidTokenId(user.getId(), code.trim());
            if (tokenId == null) {
                req.setAttribute("error", I18n.t(req, "err.invalid_or_expired_code"));
                req.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(req, resp);
                return;
            }

            userDao.updatePassword(user.getId(), PasswordUtil.hash(newPassword));
            resetDao.markUsed(tokenId);

            resp.sendRedirect(req.getContextPath() + "/login?reset=1");
        } catch (SQLException e) {
            throw new ServletException("Unable to reset password", e);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
