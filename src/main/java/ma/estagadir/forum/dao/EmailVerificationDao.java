package ma.estagadir.forum.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.security.SecureRandom;

import ma.estagadir.forum.util.DbUtil;
import ma.estagadir.forum.util.PasswordUtil;

public class EmailVerificationDao {
    private static final SecureRandom RANDOM = new SecureRandom();

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

    public int getRemainingCooldownSeconds(long userId) throws SQLException {
        String sql = "SELECT created_at FROM email_verification_tokens WHERE user_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next() || rs.getTimestamp("created_at") == null) {
                    return 0;
                }
                LocalDateTime lastSentAt = rs.getTimestamp("created_at").toLocalDateTime();
                long seconds = Duration.between(LocalDateTime.now(), lastSentAt.plusSeconds(60)).getSeconds();
                return (int) Math.max(seconds, 0);
            }
        }
    }

    public String createAndStoreCode(long userId) throws SQLException {
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        String codeHash = PasswordUtil.hash(code);

        String invalidateSql = "UPDATE email_verification_tokens SET used_at = NOW() WHERE user_id = ? AND used_at IS NULL";
        String insertSql = "INSERT INTO email_verification_tokens (user_id, token_hash, expires_at) VALUES (?, ?, ?)";

        try (Connection conn = DbUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement invalidate = conn.prepareStatement(invalidateSql);
                    PreparedStatement insert = conn.prepareStatement(insertSql)) {
                invalidate.setLong(1, userId);
                invalidate.executeUpdate();

                insert.setLong(1, userId);
                insert.setString(2, codeHash);
                insert.setObject(3, LocalDateTime.now().plusMinutes(10));
                insert.executeUpdate();

                conn.commit();
                return code;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
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
