package ma.estagadir.forum.web;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.estagadir.forum.dao.AnswerDao;
import ma.estagadir.forum.model.Answer;

public class AnswerServlet extends HttpServlet {
    private final AnswerDao answerDao = new AnswerDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String moduleIdParam = req.getParameter("moduleId");
        String questionIdParam = req.getParameter("questionId");
        String content = req.getParameter("content");

        if (isBlank(moduleIdParam) || isBlank(questionIdParam) || isBlank(content)) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        try {
            long moduleId = Long.parseLong(moduleIdParam);
            long questionId = Long.parseLong(questionIdParam);
            HttpSession session = req.getSession(false);
            long userId = (Long) session.getAttribute(SessionKeys.CURRENT_USER_ID);

            Answer answer = new Answer();
            answer.setQuestionId(questionId);
            answer.setAuthorId(userId);
            answer.setContent(content.trim());
            answerDao.create(answer);

            resp.sendRedirect(req.getContextPath() + "/modules?id=" + moduleId);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } catch (SQLException e) {
            throw new ServletException("Unable to create answer", e);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
