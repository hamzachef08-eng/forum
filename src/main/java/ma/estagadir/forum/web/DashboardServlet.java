package ma.estagadir.forum.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.estagadir.forum.dao.ArticleDao;
import ma.estagadir.forum.dao.MessageReportDao;
import ma.estagadir.forum.dao.ModuleDao;
import ma.estagadir.forum.dao.UserDao;
import ma.estagadir.forum.model.Module;
import ma.estagadir.forum.model.User;
import ma.estagadir.forum.model.UserRole;

public class DashboardServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();
    private final ModuleDao moduleDao = new ModuleDao();
    private final ArticleDao articleDao = new ArticleDao();
    private final MessageReportDao reportDao = new MessageReportDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (Long) session.getAttribute(SessionKeys.CURRENT_USER_ID);

        try {
            User user = userDao.findById(userId);
            if (user == null) {
                session.invalidate();
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            List<Module> modules = moduleDao.findByFiliereAndSemestre(user.getFiliere(), user.getSemestre());
            boolean isAdmin = user.getRole() == UserRole.ADMIN;
            int modulesCount = modules == null ? 0 : modules.size();
            int myArticlesCount = articleDao.findAllByAuthor(userId).size();
            int totalArticlesCount = articleDao.findAll().size();
            int openReportsCount = isAdmin ? reportDao.findOpenReports().size() : 0;
            int bannedUsersCount = isAdmin ? userDao.findBannedUsers().size() : 0;

            req.setAttribute("student", user);
            req.setAttribute("modules", modules);
            req.setAttribute("isAdmin", isAdmin);
            req.setAttribute("modulesCount", modulesCount);
            req.setAttribute("myArticlesCount", myArticlesCount);
            req.setAttribute("totalArticlesCount", totalArticlesCount);
            req.setAttribute("openReportsCount", openReportsCount);
            req.setAttribute("bannedUsersCount", bannedUsersCount);
            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Unable to load dashboard", e);
        }
    }
}
