package ma.estagadir.forum.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.estagadir.forum.dao.AnswerDao;
import ma.estagadir.forum.dao.ModuleDao;
import ma.estagadir.forum.dao.QuestionDao;
import ma.estagadir.forum.model.Answer;
import ma.estagadir.forum.model.Module;
import ma.estagadir.forum.model.Question;

public class ModuleServlet extends HttpServlet {
    private final ModuleDao moduleDao = new ModuleDao();
    private final QuestionDao questionDao = new QuestionDao();
    private final AnswerDao answerDao = new AnswerDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String moduleIdParam = req.getParameter("id");
        if (moduleIdParam == null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        try {
            long moduleId = Long.parseLong(moduleIdParam);
            Module module = moduleDao.findById(moduleId);
            if (module == null) {
                resp.sendRedirect(req.getContextPath() + "/dashboard");
                return;
            }

            List<Question> questions = questionDao.findByModuleId(moduleId);
            Map<Long, List<Answer>> answersByQuestion = answerDao.findByModuleIdGrouped(moduleId);

            req.setAttribute("module", module);
            req.setAttribute("questions", questions);
            req.setAttribute("answersByQuestion", answersByQuestion);
            req.getRequestDispatcher("/WEB-INF/views/module.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } catch (SQLException e) {
            throw new ServletException("Unable to load module page", e);
        }
    }
}
