package ma.estagadir.forum.web;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.estagadir.forum.dao.UserDao;
import ma.estagadir.forum.model.User;
import ma.estagadir.forum.util.AcademicCatalog;
import ma.estagadir.forum.util.I18n;
import ma.estagadir.forum.util.PasswordUtil;

public class AccountServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (Long) session.getAttribute(SessionKeys.CURRENT_USER_ID);
        try {
            User user = userDao.findById(userId);
            req.setAttribute("user", user);
            req.setAttribute("filieres", AcademicCatalog.filieres());
            req.setAttribute("semestres", AcademicCatalog.semestres());
            req.getRequestDispatcher("/WEB-INF/views/account.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Unable to load account page", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        long userId = (Long) session.getAttribute(SessionKeys.CURRENT_USER_ID);
        String role = String.valueOf(session.getAttribute(SessionKeys.CURRENT_USER_ROLE));
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

        String nom = req.getParameter("nom");
        String prenom = req.getParameter("prenom");
        String email = req.getParameter("email");
        String semestre = req.getParameter("semestre");
        String filiere = req.getParameter("filiere");
        String currentPassword = req.getParameter("currentPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (isBlank(nom) || isBlank(prenom) || isBlank(email) || isBlank(semestre)) {
            req.setAttribute("error", I18n.t(req, "err.profile_required"));
            doGet(req, resp);
            return;
        }
        if (!AcademicCatalog.isValidSemestre(semestre)) {
            req.setAttribute("error", I18n.t(req, "semester.invalid"));
            doGet(req, resp);
            return;
        }

        try {
            User currentUser = userDao.findById(userId);
            if (currentUser == null) {
                session.invalidate();
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            String normalizedEmail = email.trim().toLowerCase();
            if (userDao.emailExistsForOtherUser(normalizedEmail, userId)) {
                req.setAttribute("error", I18n.t(req, "err.email_used"));
                doGet(req, resp);
                return;
            }

            userDao.updateProfile(userId, nom.trim(), prenom.trim(), normalizedEmail);
            userDao.updateSemestre(userId, semestre);
            if (isAdmin && !isBlank(filiere)) {
                if (!AcademicCatalog.isValidFiliere(filiere)) {
                    req.setAttribute("error", I18n.t(req, "err.invalid_track_sem"));
                    doGet(req, resp);
                    return;
                }
                userDao.updateFiliere(userId, filiere);
            }
            session.setAttribute(SessionKeys.CURRENT_USER_NAME, (prenom.trim() + " " + nom.trim()).trim());

            boolean wantsPasswordChange = !isBlank(newPassword) || !isBlank(confirmPassword) || !isBlank(currentPassword);
            if (wantsPasswordChange) {
                if (isBlank(currentPassword) || isBlank(newPassword) || isBlank(confirmPassword)) {
                    req.setAttribute("error", I18n.t(req, "err.password_fields_required"));
                    doGet(req, resp);
                    return;
                }
                if (!PasswordUtil.hash(currentPassword).equals(currentUser.getPasswordHash())) {
                    req.setAttribute("error", I18n.t(req, "err.current_password_wrong"));
                    doGet(req, resp);
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    req.setAttribute("error", I18n.t(req, "err.password_mismatch"));
                    doGet(req, resp);
                    return;
                }
                userDao.updatePassword(userId, PasswordUtil.hash(newPassword));
            }

            resp.sendRedirect(req.getContextPath() + "/account?updated=1");
        } catch (SQLException e) {
            throw new ServletException("Unable to update account", e);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
