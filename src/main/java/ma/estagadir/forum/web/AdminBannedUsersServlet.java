package ma.estagadir.forum.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.estagadir.forum.dao.UserDao;
import ma.estagadir.forum.model.User;
import ma.estagadir.forum.model.UserRole;

public class AdminBannedUsersServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            if (!isAdmin(req.getSession(false))) {
                resp.sendRedirect(req.getContextPath() + "/dashboard");
                return;
            }
            List<User> bannedUsers = userDao.findBannedUsers();
            req.setAttribute("bannedUsers", bannedUsers);
            req.getRequestDispatcher("/WEB-INF/views/admin-banned-users.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Unable to load banned users", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HttpSession session = req.getSession(false);
            if (!isAdmin(session)) {
                resp.sendRedirect(req.getContextPath() + "/dashboard");
                return;
            }

            long userId = Long.parseLong(req.getParameter("userId"));
            userDao.unbanUser(userId);
            resp.sendRedirect(req.getContextPath() + "/admin/banned?done=1");
        } catch (NumberFormatException | SQLException e) {
            throw new ServletException("Unable to unban user", e);
        }
    }

    private boolean isAdmin(HttpSession session) throws SQLException {
        if (session == null || session.getAttribute(SessionKeys.CURRENT_USER_ID) == null) {
            return false;
        }
        long userId = (Long) session.getAttribute(SessionKeys.CURRENT_USER_ID);
        User user = userDao.findById(userId);
        return user != null && user.getRole() == UserRole.ADMIN;
    }
}
