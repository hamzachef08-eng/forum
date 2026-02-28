package ma.estagadir.forum.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.estagadir.forum.dao.ModuleDao;
import ma.estagadir.forum.dao.ModuleMessageDao;
import ma.estagadir.forum.dao.UserDao;
import ma.estagadir.forum.model.Module;
import ma.estagadir.forum.model.ModuleMessage;
import ma.estagadir.forum.model.User;

public class ModuleChatServlet extends HttpServlet {
    private final ModuleDao moduleDao = new ModuleDao();
    private final ModuleMessageDao messageDao = new ModuleMessageDao();
    private final UserDao userDao = new UserDao();

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

            HttpSession session = req.getSession(false);
            long userId = (Long) session.getAttribute(SessionKeys.CURRENT_USER_ID);
            User student = userDao.findById(userId);

            List<Module> sidebarModules = moduleDao.findByFiliereAndSemestre(student.getFiliere(), student.getSemestre());
            List<ModuleMessage> messages = messageDao.findByModuleId(moduleId);
            req.setAttribute("module", module);
            req.setAttribute("messages", messages);
            req.setAttribute("sidebarModules", sidebarModules);
            req.getRequestDispatcher("/WEB-INF/views/module-chat.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } catch (SQLException e) {
            throw new ServletException("Unable to load module chat", e);
        }
    }
}
