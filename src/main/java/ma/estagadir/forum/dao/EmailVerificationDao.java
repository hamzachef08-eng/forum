package ma.estagadir.forum.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import ma.estagadir.forum.util.DbUtil;

public class EmailVerificationDao {
    public long create(long userId, String tokenHash, LocalDateTime expiresAt) throws SQLException {
        String sql = "INSERT INTO email_verification_tokens (user_id, token_hash, expires_at) VALUES (?, ?, ?)";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, userId);
            stmt.setString(2, tokenHash);
            stmt.setObject(3, expiresAt);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return -1L;
    }

    public boolean consumeValidToken(long userId, String tokenHash) throws SQLException {
        String sql = "UPDATE email_verification_tokens SET used_at = NOW() "
                + "WHERE user_id = ? AND token_hash = ? AND used_at IS NULL AND expires_at > NOW()";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setString(2, tokenHash);
            return stmt.executeUpdate() > 0;
        }
    }
}
