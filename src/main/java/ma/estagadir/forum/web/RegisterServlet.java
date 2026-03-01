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
import ma.estagadir.forum.dao.UserDao;
import ma.estagadir.forum.model.User;
import ma.estagadir.forum.util.AcademicCatalog;
import ma.estagadir.forum.util.I18n;
import ma.estagadir.forum.util.PasswordUtil;

public class RegisterServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("filieres", AcademicCatalog.filieres());
        req.setAttribute("semestres", AcademicCatalog.semestres());
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String nom = req.getParameter("nom");
        String prenom = req.getParameter("prenom");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String filiere = req.getParameter("filiere");
        String semestre = req.getParameter("semestre");

        if (isBlank(nom) || isBlank(prenom) || isBlank(email) || isBlank(password) || isBlank(filiere) || isBlank(semestre)) {
            req.setAttribute("error", I18n.t(req, "err.required_fields"));
            refillLists(req);
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        if (!AcademicCatalog.isValidFiliere(filiere) || !AcademicCatalog.isValidSemestre(semestre)) {
            req.setAttribute("error", I18n.t(req, "err.invalid_track_sem"));
            refillLists(req);
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        try {
            String normalizedEmail = email.trim().toLowerCase();
            User existing = userDao.findByEmail(normalizedEmail);
            if (existing != null) {
                if (!existing.isEmailVerified()) {
                    try {
                        VerifyEmailServlet.createAndSendVerification(req, existing.getId(), existing.getEmail());
                    } catch (MessagingException ex) {
                        req.setAttribute("error", I18n.t(req, "err.verify_not_sent"));
                        refillLists(req);
                        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
                        return;
                    }
                    String encoded = URLEncoder.encode(existing.getEmail(), StandardCharsets.UTF_8);
                    resp.sendRedirect(req.getContextPath() + "/verify-email?email=" + encoded + "&sent=1");
                    return;
                }

                req.setAttribute("error", I18n.t(req, "err.email_exists"));
                refillLists(req);
                req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
                return;
            }

            User user = new User();
            user.setNom(nom.trim());
            user.setPrenom(prenom.trim());
            user.setEmail(normalizedEmail);
            user.setPasswordHash(PasswordUtil.hash(password));
            user.setFiliere(filiere);
            user.setSemestre(semestre);
            user.setEmailVerified(false);
            long userId = userDao.create(user);
            if (userId <= 0) {
                throw new SQLException("Unable to create user record");
            }
            try {
                VerifyEmailServlet.createAndSendVerification(req, userId, user.getEmail());
                String encoded = URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8);
                resp.sendRedirect(req.getContextPath() + "/verify-email?email=" + encoded + "&sent=1");
            } catch (MessagingException ex) {
                req.setAttribute("error", I18n.t(req, "err.verify_not_sent"));
                refillLists(req);
                req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException("Unable to register user", e);
        }
    }

    private void refillLists(HttpServletRequest req) {
        req.setAttribute("filieres", AcademicCatalog.filieres());
        req.setAttribute("semestres", AcademicCatalog.semestres());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
