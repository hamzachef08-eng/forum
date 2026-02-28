package ma.estagadir.forum.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.estagadir.forum.dao.PasswordResetDao;
import ma.estagadir.forum.dao.UserDao;
import ma.estagadir.forum.model.User;
import ma.estagadir.forum.util.I18n;
import ma.estagadir.forum.util.MailUtil;

public class ForgotPasswordServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();
    private final PasswordResetDao resetDao = new PasswordResetDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            req.setAttribute("error", I18n.t(req, "err.required_email"));
            req.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(req, resp);
            return;
        }

        String normalizedEmail = email.trim().toLowerCase();
        if (!normalizedEmail.endsWith("@gmail.com")) {
            req.setAttribute("error", I18n.t(req, "err.gmail_only"));
            req.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(req, resp);
            return;
        }

        try {
            User user = userDao.findByEmail(normalizedEmail);
            if (user == null) {
                req.setAttribute("error", I18n.t(req, "err.no_account_email"));
                req.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(req, resp);
                return;
            }

            int remaining = resetDao.getRemainingCooldownSeconds(user.getId());
            if (remaining > 0) {
                String encodedEmail = URLEncoder.encode(normalizedEmail, StandardCharsets.UTF_8);
                resp.sendRedirect(req.getContextPath() + "/reset-password?email=" + encodedEmail + "&wait=" + remaining);
                return;
            }

            String code = resetDao.createAndStoreCode(user.getId());
            MailUtil.sendResetCode(getServletContext(), user.getEmail(), code);

            String encodedEmail = URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/reset-password?email=" + encodedEmail + "&sent=1");
        } catch (SQLException e) {
            throw new ServletException("Unable to process forgot password", e);
        } catch (MessagingException e) {
            req.setAttribute("error", I18n.t(req, "err.smtp"));
            req.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(req, resp);
        }
    }
}
