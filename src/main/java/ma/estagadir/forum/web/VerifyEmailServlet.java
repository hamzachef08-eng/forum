package ma.estagadir.forum.web;

import java.io.IOException;
import java.sql.SQLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.estagadir.forum.dao.EmailVerificationDao;
import ma.estagadir.forum.dao.UserDao;
import ma.estagadir.forum.model.User;
import ma.estagadir.forum.util.MailUtil;
import ma.estagadir.forum.util.PasswordUtil;

public class VerifyEmailServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();
    private final EmailVerificationDao verificationDao = new EmailVerificationDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        if (email != null && !email.trim().isEmpty()) {
            req.getRequestDispatcher("/WEB-INF/views/verify-email.jsp").forward(req, resp);
            return;
        }

        // Backward compatibility for old verification links.
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String action = req.getParameter("action");
        String email = normalize(req.getParameter("email"));
        if (email == null) {
            resp.sendRedirect(req.getContextPath() + "/login?verify=invalid");
            return;
        }

        try {
            User user = userDao.findByEmail(email);
            if (user == null) {
                resp.sendRedirect(req.getContextPath() + "/login?verify=invalid");
                return;
            }

            if ("resend".equals(action)) {
                int remaining = verificationDao.getRemainingCooldownSeconds(user.getId());
                if (remaining > 0) {
                    redirectVerify(resp, req, email, "wait=" + remaining);
                    return;
                }
                String code = verificationDao.createAndStoreCode(user.getId());
                MailUtil.sendVerificationCode(req.getServletContext(), user.getEmail(), code);
                redirectVerify(resp, req, email, "sent=1");
                return;
            }

            String code = req.getParameter("code");
            if (code == null || !code.trim().matches("\\d{6}")) {
                redirectVerify(resp, req, email, "bad=1");
                return;
            }

            boolean ok = verificationDao.consumeValidToken(user.getId(), PasswordUtil.hash(code.trim()));
            if (!ok) {
                redirectVerify(resp, req, email, "bad=1");
                return;
            }
            userDao.verifyEmail(user.getId());
            resp.sendRedirect(req.getContextPath() + "/login?verify=ok");
        } catch (SQLException e) {
            throw new ServletException("Unable to verify email", e);
        } catch (MessagingException e) {
            redirectVerify(resp, req, email, "smtp=1");
        }
    }

    public static void createAndSendVerification(HttpServletRequest req, long userId, String email)
            throws SQLException, MessagingException {
        EmailVerificationDao dao = new EmailVerificationDao();
        String code = dao.createAndStoreCode(userId);
        MailUtil.sendVerificationCode(req.getServletContext(), email, code);
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

    private String normalize(String email) {
        if (email == null) {
            return null;
        }
        String clean = email.trim().toLowerCase();
        return clean.isEmpty() ? null : clean;
    }

    private void redirectVerify(HttpServletResponse resp, HttpServletRequest req, String email, String extra)
            throws IOException {
        String encoded = URLEncoder.encode(email, StandardCharsets.UTF_8);
        resp.sendRedirect(req.getContextPath() + "/verify-email?email=" + encoded + "&" + extra);
    }
}
