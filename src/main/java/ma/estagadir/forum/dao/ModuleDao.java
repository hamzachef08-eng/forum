package ma.estagadir.forum.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ma.estagadir.forum.model.Module;
import ma.estagadir.forum.util.DbUtil;

public class ModuleDao {
    private static final Pattern TRACK_CODE_PATTERN = Pattern.compile("\\(([^)]+)\\)");

    public List<Module> findAll() throws SQLException {
        String sql = "SELECT id, filiere, semestre, nom_module FROM modules ORDER BY filiere, semestre, id";
        List<Module> modules = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Module module = mapRow(rs);
                modules.add(module);
            }
        }
        return modules;
    }

    public Module findById(long id) throws SQLException {
        String sql = "SELECT id, filiere, semestre, nom_module FROM modules WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    public List<Module> findByFiliereAndSemestre(String filiere, String semestre) throws SQLException {
        String trackCode = extractTrackCode(filiere);
        String sql = "SELECT id, filiere, semestre, nom_module FROM modules "
                + "WHERE TRIM(semestre) = TRIM(?) AND filiere LIKE ? ORDER BY id";
        List<Module> modules = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, semestre);
            stmt.setString(2, "%(" + trackCode + ")%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Module module = mapRow(rs);
                    modules.add(module);
                }
            }
        }
        return modules;
    }

    private String extractTrackCode(String filiere) {
        if (filiere == null) {
            return "";
        }
        Matcher matcher = TRACK_CODE_PATTERN.matcher(filiere);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return filiere.trim();
    }

    private Module mapRow(ResultSet rs) throws SQLException {
        Module module = new Module();
        module.setId(rs.getLong("id"));
        module.setFiliere(rs.getString("filiere"));
        module.setSemestre(rs.getString("semestre"));
        module.setNomModule(rs.getString("nom_module"));
        module.setTitle(module.getNomModule());
        module.setCode(module.getSemestre());
        module.setDescription(module.getFiliere());
        return module;
    }
}
