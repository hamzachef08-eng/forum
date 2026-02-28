package ma.estagadir.forum.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.estagadir.forum.dao.BlogReportDao;
import ma.estagadir.forum.dao.MessageReportDao;
import ma.estagadir.forum.dao.UserDao;
import ma.estagadir.forum.model.BlogReport;
import ma.estagadir.forum.model.MessageReport;
import ma.estagadir.forum.model.User;
import ma.estagadir.forum.model.UserRole;

public class AdminReportsServlet extends HttpServlet {
    private final MessageReportDao reportDao = new MessageReportDao();
    private final BlogReportDao blogReportDao = new BlogReportDao();
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            if (!isAdmin(req.getSession(false))) {
                resp.sendRedirect(req.getContextPath() + "/dashboard");
                return;
            }
            List<MessageReport> reports = reportDao.findOpenReports();
            List<BlogReport> blogReports = blogReportDao.findOpenReports();
            req.setAttribute("reports", reports);
            req.setAttribute("blogReports", blogReports);
            req.getRequestDispatcher("/WEB-INF/views/admin-reports.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Unable to load reports", e);
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

            long reportId = Long.parseLong(req.getParameter("reportId"));
            long reportedUserId = Long.parseLong(req.getParameter("reportedUserId"));
            long adminUserId = (Long) session.getAttribute(SessionKeys.CURRENT_USER_ID);
            String source = req.getParameter("source");

            userDao.banUser(reportedUserId);
            if ("blog".equalsIgnoreCase(source)) {
                blogReportDao.closeReportAsBanned(reportId, adminUserId);
            } else {
                reportDao.closeReportAsBanned(reportId, adminUserId);
            }
            resp.sendRedirect(req.getContextPath() + "/admin/reports?done=1");
        } catch (NumberFormatException | SQLException e) {
            throw new ServletException("Unable to ban reported user", e);
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
