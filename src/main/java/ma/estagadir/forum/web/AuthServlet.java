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
import ma.estagadir.forum.util.I18n;
import ma.estagadir.forum.util.PasswordUtil;

public class AuthServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(SessionKeys.CURRENT_USER_ID) != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (isBlank(email) || isBlank(password)) {
            req.setAttribute("error", I18n.t(req, "err.required_email_password"));
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }

        try {
            User user = userDao.findByEmail(email.trim());
            if (user == null || !user.getPasswordHash().equals(PasswordUtil.hash(password))) {
                req.setAttribute("error", I18n.t(req, "err.invalid_credentials"));
                req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
                return;
            }
            if (!user.isEmailVerified()) {
                req.setAttribute("error", I18n.t(req, "err.email_not_verified"));
                req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
                return;
            }

            if (user.isBanned()) {
                req.setAttribute("error", I18n.t(req, "err.account_banned"));
                req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
                return;
            }

            HttpSession session = req.getSession(true);
            session.setAttribute(SessionKeys.CURRENT_USER_ID, user.getId());
            session.setAttribute(SessionKeys.CURRENT_USER_NAME, user.getFullName());
            session.setAttribute(SessionKeys.CURRENT_USER_ROLE, user.getRole().name());

            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } catch (SQLException e) {
            throw new ServletException("Unable to login user", e);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
