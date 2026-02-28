package ma.estagadir.forum.web;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.estagadir.forum.dao.ModuleDao;
import ma.estagadir.forum.model.Module;
import ma.estagadir.forum.util.AcademicCatalog;

public class ModuleOptionsServlet extends HttpServlet {
    private final ModuleDao moduleDao = new ModuleDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filiere = req.getParameter("filiere");
        String semestre = req.getParameter("semestre");

        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json");

        if (!AcademicCatalog.isValidFiliere(filiere) || !AcademicCatalog.isValidSemestre(semestre)) {
            resp.getWriter().write("[]");
            return;
        }

        try {
            List<Module> modules = moduleDao.findByFiliereAndSemestre(filiere, semestre);
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < modules.size(); i++) {
                if (i > 0) {
                    json.append(',');
                }
                json.append('"').append(escapeJson(modules.get(i).getNomModule())).append('"');
            }
            json.append(']');
            resp.getWriter().write(json.toString());
        } catch (SQLException e) {
            throw new ServletException("Unable to load modules for selected choice", e);
        }
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
