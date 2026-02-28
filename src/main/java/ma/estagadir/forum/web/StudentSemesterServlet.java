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
import ma.estagadir.forum.util.AcademicCatalog;

public class StudentSemesterServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (Long) session.getAttribute(SessionKeys.CURRENT_USER_ID);
        try {
            User student = userDao.findById(userId);
            req.setAttribute("student", student);
            req.setAttribute("semestres", AcademicCatalog.semestres());
            req.getRequestDispatcher("/WEB-INF/views/semester.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Unable to load semester page", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String semestre = req.getParameter("semestre");
        if (!AcademicCatalog.isValidSemestre(semestre)) {
            resp.sendRedirect(req.getContextPath() + "/student/semester?error=invalid");
            return;
        }

        HttpSession session = req.getSession(false);
        long userId = (Long) session.getAttribute(SessionKeys.CURRENT_USER_ID);

        try {
            userDao.updateSemestre(userId, semestre);
            resp.sendRedirect(req.getContextPath() + "/dashboard?updated=1");
        } catch (SQLException e) {
            throw new ServletException("Unable to update semester", e);
        }
    }
}
