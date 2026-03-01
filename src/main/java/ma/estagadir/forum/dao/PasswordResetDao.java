package ma.estagadir.forum.dao;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;

import ma.estagadir.forum.util.DbUtil;
import ma.estagadir.forum.util.PasswordUtil;

public class PasswordResetDao {
    private static final SecureRandom RANDOM = new SecureRandom();

    public int getRemainingCooldownSeconds(long userId) throws SQLException {
        String sql = "SELECT last_sent_at FROM password_reset_codes WHERE user_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next() || rs.getTimestamp("last_sent_at") == null) {
                    return 0;
                }
                LocalDateTime lastSentAt = rs.getTimestamp("last_sent_at").toLocalDateTime();
                long seconds = Duration.between(LocalDateTime.now(), lastSentAt.plusSeconds(60)).getSeconds();
                return (int) Math.max(seconds, 0);
            }
        }
    }

    public String createAndStoreCode(long userId) throws SQLException {
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        String codeHash = PasswordUtil.hash(code);

        String invalidateSql = "UPDATE password_reset_codes SET used_at = NOW() WHERE user_id = ? AND used_at IS NULL";
        String insertSql = "INSERT INTO password_reset_codes (user_id, code_hash, expires_at, last_sent_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = DbUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement invalidate = conn.prepareStatement(invalidateSql);
                    PreparedStatement insert = conn.prepareStatement(insertSql)) {
                invalidate.setLong(1, userId);
                invalidate.executeUpdate();

                insert.setLong(1, userId);
                insert.setString(2, codeHash);
                insert.setObject(3, LocalDateTime.now().plusMinutes(10));
                insert.setObject(4, LocalDateTime.now());
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

    public Long findValidTokenId(long userId, String plainCode) throws SQLException {
        String sql = "SELECT id FROM password_reset_codes "
                + "WHERE user_id = ? AND code_hash = ? AND used_at IS NULL AND expires_at >= NOW() "
                + "ORDER BY id DESC LIMIT 1";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setString(2, PasswordUtil.hash(plainCode));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
                return null;
            }
        }
    }

    public void markUsed(long tokenId) throws SQLException {
        String sql = "UPDATE password_reset_codes SET used_at = NOW() WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, tokenId);
            stmt.executeUpdate();
        }
    }
}
