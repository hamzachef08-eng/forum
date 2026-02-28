package ma.estagadir.forum.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ma.estagadir.forum.model.User;
import ma.estagadir.forum.model.UserRole;
import ma.estagadir.forum.util.DbUtil;

public class UserDao {
    private static final String SELECT_USER_WITH_VERIFY =
            "SELECT id, nom, prenom, email, mot_de_passe, filiere, semestre, role, is_banned, email_verified, created_at FROM users WHERE %s = ?";
    private static final String SELECT_USER_LEGACY =
            "SELECT id, nom, prenom, email, mot_de_passe, filiere, semestre, role, is_banned, created_at FROM users WHERE %s = ?";

    public User findByEmail(String email) throws SQLException {
        return findOneBy("email", email);
    }

    public User findById(long id) throws SQLException {
        return findOneBy("id", id);
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean emailExistsForOtherUser(String email, long userId) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ? AND id <> ?";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setLong(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public long create(User user) throws SQLException {
        String sql = "INSERT INTO users (nom, prenom, email, mot_de_passe, filiere, semestre, role, is_banned, email_verified) VALUES (?, ?, ?, ?, ?, ?, ?, 0, ?)";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPasswordHash());
            stmt.setString(5, user.getFiliere());
            stmt.setString(6, user.getSemestre());
            stmt.setString(7, user.getRole().name());
            stmt.setBoolean(8, user.isEmailVerified());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return -1L;
    }

    public void updateSemestre(long userId, String semestre) throws SQLException {
        String sql = "UPDATE users SET semestre = ? WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, semestre);
            stmt.setLong(2, userId);
            stmt.executeUpdate();
        }
    }

    public void updateFiliere(long userId, String filiere) throws SQLException {
        String sql = "UPDATE users SET filiere = ? WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, filiere);
            stmt.setLong(2, userId);
            stmt.executeUpdate();
        }
    }

    public void updatePassword(long userId, String passwordHash) throws SQLException {
        String sql = "UPDATE users SET mot_de_passe = ? WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, passwordHash);
            stmt.setLong(2, userId);
            stmt.executeUpdate();
        }
    }

    public void updateProfile(long userId, String nom, String prenom, String email) throws SQLException {
        String sql = "UPDATE users SET nom = ?, prenom = ?, email = ? WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, email);
            stmt.setLong(4, userId);
            stmt.executeUpdate();
        }
    }

    public void banUser(long userId) throws SQLException {
        String sql = "UPDATE users SET is_banned = 1 WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.executeUpdate();
        }
    }

    public void unbanUser(long userId) throws SQLException {
        String sql = "UPDATE users SET is_banned = 0 WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.executeUpdate();
        }
    }

    public List<User> findBannedUsers() throws SQLException {
        String sql = "SELECT id, nom, prenom, email, mot_de_passe, filiere, semestre, role, is_banned, email_verified, created_at "
                + "FROM users WHERE is_banned = 1 ORDER BY created_at DESC";
        List<User> users = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;
        } catch (SQLSyntaxErrorException ex) {
            if (!isUnknownEmailVerified(ex)) {
                throw ex;
            }
            String fallbackSql = "SELECT id, nom, prenom, email, mot_de_passe, filiere, semestre, role, is_banned, created_at "
                    + "FROM users WHERE is_banned = 1 ORDER BY created_at DESC";
            try (Connection conn = DbUtil.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(fallbackSql);
                    ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRow(rs));
                }
                return users;
            }
        }
    }

    public void verifyEmail(long userId) throws SQLException {
        String sql = "UPDATE users SET email_verified = 1 WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.executeUpdate();
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("mot_de_passe"));
        user.setFiliere(rs.getString("filiere"));
        user.setSemestre(rs.getString("semestre"));
        user.setRole(UserRole.fromString(rs.getString("role")));
        user.setBanned(rs.getBoolean("is_banned"));
        user.setEmailVerified(hasColumn(rs, "email_verified") ? rs.getBoolean("email_verified") : true);
        if (rs.getTimestamp("created_at") != null) {
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        return user;
    }

    private User findOneBy(String fieldName, Object value) throws SQLException {
        String sql = String.format(SELECT_USER_WITH_VERIFY, fieldName);
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, value);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLSyntaxErrorException ex) {
            if (!isUnknownEmailVerified(ex)) {
                throw ex;
            }
            String fallbackSql = String.format(SELECT_USER_LEGACY, fieldName);
            try (Connection conn = DbUtil.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(fallbackSql)) {
                stmt.setObject(1, value);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapRow(rs);
                    }
                    return null;
                }
            }
        }
    }

    private boolean hasColumn(ResultSet rs, String colName) {
        try {
            rs.findColumn(colName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean isUnknownEmailVerified(SQLException ex) {
        String msg = ex.getMessage();
        return msg != null && msg.toLowerCase().contains("email_verified");
    }
}
