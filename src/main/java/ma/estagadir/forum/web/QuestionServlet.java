package ma.estagadir.forum.web;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.estagadir.forum.dao.QuestionDao;
import ma.estagadir.forum.model.Question;

public class QuestionServlet extends HttpServlet {
    private final QuestionDao questionDao = new QuestionDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String moduleIdParam = req.getParameter("moduleId");
        String title = req.getParameter("title");
        String content = req.getParameter("content");

        if (isBlank(moduleIdParam) || isBlank(title) || isBlank(content)) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        try {
            long moduleId = Long.parseLong(moduleIdParam);
            HttpSession session = req.getSession(false);
            long userId = (Long) session.getAttribute(SessionKeys.CURRENT_USER_ID);

            Question question = new Question();
            question.setModuleId(moduleId);
            question.setAuthorId(userId);
            question.setTitle(title.trim());
            question.setContent(content.trim());
            questionDao.create(question);

            resp.sendRedirect(req.getContextPath() + "/modules?id=" + moduleId);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } catch (SQLException e) {
            throw new ServletException("Unable to create question", e);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
