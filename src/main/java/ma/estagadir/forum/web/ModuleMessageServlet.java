package ma.estagadir.forum.web;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.estagadir.forum.dao.ModuleMessageDao;

public class ModuleMessageServlet extends HttpServlet {
    private final ModuleMessageDao messageDao = new ModuleMessageDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String moduleIdParam = req.getParameter("moduleId");
        String content = req.getParameter("content");
        String replyToParam = req.getParameter("replyToMessageId");
        if (moduleIdParam == null || content == null || content.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        try {
            long moduleId = Long.parseLong(moduleIdParam);
            Long replyToMessageId = parseNullableLong(replyToParam);
            HttpSession session = req.getSession(false);
            long userId = (Long) session.getAttribute(SessionKeys.CURRENT_USER_ID);

            messageDao.create(moduleId, userId, content.trim(), replyToMessageId);
            resp.sendRedirect(req.getContextPath() + "/module/chat?id=" + moduleId);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } catch (SQLException e) {
            throw new ServletException("Unable to post module chat message", e);
        }
    }

    private Long parseNullableLong(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        return Long.parseLong(raw.trim());
    }
}
