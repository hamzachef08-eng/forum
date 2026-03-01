package ma.estagadir.forum.web;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Base64;

import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.estagadir.forum.dao.EmailVerificationDao;
import ma.estagadir.forum.dao.UserDao;
import ma.estagadir.forum.util.MailUtil;
import ma.estagadir.forum.util.PasswordUtil;

public class VerifyEmailServlet extends HttpServlet {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserDao userDao = new UserDao();
    private final EmailVerificationDao verificationDao = new EmailVerificationDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long userId = parseLong(req.getParameter("u"));
        String token = req.getParameter("token");

        if (userId <= 0 || token == null || token.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/login?verify=invalid");
            return;
        }

        try {
            boolean ok = verificationDao.consumeValidToken(userId, PasswordUtil.hash(token));
            if (!ok) {
                resp.sendRedirect(req.getContextPath() + "/login?verify=invalid");
                return;
            }
            userDao.verifyEmail(userId);
            resp.sendRedirect(req.getContextPath() + "/login?verify=ok");
        } catch (SQLException e) {
            throw new ServletException("Unable to verify email", e);
        }
    }

    public static String generateRawToken() {
        byte[] bytes = new byte[18];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static void createAndSendVerification(HttpServletRequest req, long userId, String email)
            throws SQLException, MessagingException {
        String rawToken = generateRawToken();
        String tokenHash = PasswordUtil.hash(rawToken);

        EmailVerificationDao dao = new EmailVerificationDao();
        dao.create(userId, tokenHash, LocalDateTime.now().plusHours(24));

        String baseUrl = req.getScheme() + "://" + req.getServerName()
                + ((req.getServerPort() == 80 || req.getServerPort() == 443) ? "" : ":" + req.getServerPort())
                + req.getContextPath();
        String link = baseUrl + "/verify-email?u=" + userId + "&token="
                + java.net.URLEncoder.encode(rawToken, StandardCharsets.UTF_8);

        MailUtil.sendVerificationEmail(req.getServletContext(), email, link);
    }

    private long parseLong(String raw) {
        if (raw == null) {
            return -1;
        }
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
