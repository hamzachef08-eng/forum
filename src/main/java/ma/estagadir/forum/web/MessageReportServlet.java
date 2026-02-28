package ma.estagadir.forum.web;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.estagadir.forum.dao.MessageReportDao;
import ma.estagadir.forum.dao.UserDao;
import ma.estagadir.forum.model.ModuleMessage;
import ma.estagadir.forum.model.User;
import ma.estagadir.forum.model.UserRole;

public class MessageReportServlet extends HttpServlet {
    private final MessageReportDao reportDao = new MessageReportDao();
    private final UserDao userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String messageIdParam = req.getParameter("messageId");
        String moduleIdParam = req.getParameter("moduleId");
        String reason = req.getParameter("reason");

        if (messageIdParam == null || moduleIdParam == null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        try {
            long messageId = Long.parseLong(messageIdParam);
            long moduleId = Long.parseLong(moduleIdParam);

            ModuleMessage message = reportDao.findMessageById(messageId);
            if (message == null || message.getModuleId() != moduleId) {
                resp.sendRedirect(req.getContextPath() + "/module/chat?id=" + moduleId);
                return;
            }

            HttpSession session = req.getSession(false);
            long reporterId = (Long) session.getAttribute(SessionKeys.CURRENT_USER_ID);
            String currentRole = String.valueOf(session.getAttribute(SessionKeys.CURRENT_USER_ROLE));

            if ("ADMIN".equalsIgnoreCase(currentRole) && "ban".equalsIgnoreCase(req.getParameter("action"))) {
                if (reporterId == message.getUserId()) {
                    resp.sendRedirect(req.getContextPath() + "/module/chat?id=" + moduleId);
                    return;
                }
                User target = userDao.findById(message.getUserId());
                if (target != null && target.getRole() == UserRole.ADMIN) {
                    resp.sendRedirect(req.getContextPath() + "/module/chat?id=" + moduleId);
                    return;
                }
                userDao.banUser(message.getUserId());
                resp.sendRedirect(req.getContextPath() + "/module/chat?id=" + moduleId + "&ban=1");
                return;
            }

            if (reporterId == message.getUserId()) {
                resp.sendRedirect(req.getContextPath() + "/module/chat?id=" + moduleId + "&report=own");
                return;
            }
            User reported = userDao.findById(message.getUserId());
            if (reported != null && reported.getRole() == UserRole.ADMIN) {
                resp.sendRedirect(req.getContextPath() + "/module/chat?id=" + moduleId + "&report=admin");
                return;
            }

            String cleanReason = (reason == null || reason.trim().isEmpty()) ? "Inappropriate message" : reason.trim();
            reportDao.createReport(messageId, message.getUserId(), reporterId, cleanReason);

            resp.sendRedirect(req.getContextPath() + "/module/chat?id=" + moduleId + "&report=1");
        } catch (NumberFormatException | SQLException e) {
            throw new ServletException("Unable to report message", e);
        }
    }
}
