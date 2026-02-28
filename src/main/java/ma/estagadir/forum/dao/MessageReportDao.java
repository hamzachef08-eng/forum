package ma.estagadir.forum.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ma.estagadir.forum.model.MessageReport;
import ma.estagadir.forum.model.ModuleMessage;
import ma.estagadir.forum.util.DbUtil;

public class MessageReportDao {
    public ModuleMessage findMessageById(long messageId) throws SQLException {
        String sql = "SELECT id, module_id, user_id, content FROM module_messages WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, messageId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                ModuleMessage m = new ModuleMessage();
                m.setId(rs.getLong("id"));
                m.setModuleId(rs.getLong("module_id"));
                m.setUserId(rs.getLong("user_id"));
                m.setContent(rs.getString("content"));
                return m;
            }
        }
    }

    public void createReport(long messageId, long reportedUserId, long reporterUserId, String reason) throws SQLException {
        String sql = "INSERT INTO message_reports (message_id, reported_user_id, reporter_user_id, reason, status) VALUES (?, ?, ?, ?, 'OPEN')";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, messageId);
            stmt.setLong(2, reportedUserId);
            stmt.setLong(3, reporterUserId);
            stmt.setString(4, reason);
            stmt.executeUpdate();
        }
    }

    public List<MessageReport> findOpenReports() throws SQLException {
        String sql = "SELECT r.id, r.message_id, r.reported_user_id, r.reporter_user_id, r.reason, r.status, r.created_at, "
                + "rm.content AS message_content, CONCAT(ru.prenom, ' ', ru.nom) AS reported_name, "
                + "CONCAT(rr.prenom, ' ', rr.nom) AS reporter_name "
                + "FROM message_reports r "
                + "JOIN module_messages rm ON rm.id = r.message_id "
                + "JOIN users ru ON ru.id = r.reported_user_id "
                + "JOIN users rr ON rr.id = r.reporter_user_id "
                + "WHERE r.status = 'OPEN' ORDER BY r.created_at DESC";

        List<MessageReport> reports = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                MessageReport r = new MessageReport();
                r.setId(rs.getLong("id"));
                r.setMessageId(rs.getLong("message_id"));
                r.setReportedUserId(rs.getLong("reported_user_id"));
                r.setReporterUserId(rs.getLong("reporter_user_id"));
                r.setReason(rs.getString("reason"));
                r.setStatus(rs.getString("status"));
                r.setMessageContent(rs.getString("message_content"));
                r.setReportedUserName(rs.getString("reported_name"));
                r.setReporterUserName(rs.getString("reporter_name"));
                if (rs.getTimestamp("created_at") != null) {
                    r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
                reports.add(r);
            }
        }
        return reports;
    }

    public void closeReportAsBanned(long reportId, long adminUserId) throws SQLException {
        String sql = "UPDATE message_reports SET status = 'BANNED', reviewed_by = ?, reviewed_at = NOW() WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, adminUserId);
            stmt.setLong(2, reportId);
            stmt.executeUpdate();
        }
    }
}
