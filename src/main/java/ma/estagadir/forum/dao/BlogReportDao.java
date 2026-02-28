package ma.estagadir.forum.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ma.estagadir.forum.model.BlogReport;
import ma.estagadir.forum.util.DbUtil;

public class BlogReportDao {
    private static volatile boolean blogReportSchemaReady;

    public boolean hasOpenReport(long articleId, long reporterUserId) throws SQLException {
        ensureBlogReportSchema();
        String sql = "SELECT 1 FROM blog_reports WHERE article_id = ? AND reporter_user_id = ? AND status = 'OPEN'";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, articleId);
            stmt.setLong(2, reporterUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void createReport(long articleId, long reportedUserId, long reporterUserId, String reason) throws SQLException {
        ensureBlogReportSchema();
        String sql = "INSERT INTO blog_reports (article_id, reported_user_id, reporter_user_id, reason, status) VALUES (?, ?, ?, ?, 'OPEN')";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, articleId);
            stmt.setLong(2, reportedUserId);
            stmt.setLong(3, reporterUserId);
            stmt.setString(4, reason);
            stmt.executeUpdate();
        }
    }

    public List<BlogReport> findOpenReports() throws SQLException {
        ensureBlogReportSchema();
        String sql = "SELECT r.id, r.article_id, r.reported_user_id, r.reporter_user_id, r.reason, r.status, r.created_at, "
                + "a.title AS article_title, a.content AS article_content, "
                + "CONCAT(ru.prenom, ' ', ru.nom) AS reported_name, "
                + "CONCAT(rr.prenom, ' ', rr.nom) AS reporter_name "
                + "FROM blog_reports r "
                + "JOIN articles a ON a.id = r.article_id "
                + "JOIN users ru ON ru.id = r.reported_user_id "
                + "JOIN users rr ON rr.id = r.reporter_user_id "
                + "WHERE r.status = 'OPEN' ORDER BY r.created_at DESC";
        List<BlogReport> reports = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                BlogReport r = new BlogReport();
                r.setId(rs.getLong("id"));
                r.setArticleId(rs.getLong("article_id"));
                r.setReportedUserId(rs.getLong("reported_user_id"));
                r.setReporterUserId(rs.getLong("reporter_user_id"));
                r.setReason(rs.getString("reason"));
                r.setStatus(rs.getString("status"));
                r.setArticleTitle(rs.getString("article_title"));
                r.setArticleContent(rs.getString("article_content"));
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
        ensureBlogReportSchema();
        String sql = "UPDATE blog_reports SET status = 'BANNED', reviewed_by = ?, reviewed_at = NOW() WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, adminUserId);
            stmt.setLong(2, reportId);
            stmt.executeUpdate();
        }
    }

    private void ensureBlogReportSchema() throws SQLException {
        if (blogReportSchemaReady) {
            return;
        }
        synchronized (BlogReportDao.class) {
            if (blogReportSchemaReady) {
                return;
            }
            ArticleDao.ensureBlogSchema();
            try (Connection conn = DbUtil.getConnection();
                    Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS blog_reports ("
                                + "id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
                                + "article_id BIGINT UNSIGNED NOT NULL,"
                                + "reported_user_id BIGINT UNSIGNED NOT NULL,"
                                + "reporter_user_id BIGINT UNSIGNED NOT NULL,"
                                + "reason VARCHAR(255) NOT NULL,"
                                + "status ENUM('OPEN','BANNED','REJECTED') NOT NULL DEFAULT 'OPEN',"
                                + "reviewed_by BIGINT UNSIGNED NULL,"
                                + "reviewed_at TIMESTAMP NULL,"
                                + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                                + "PRIMARY KEY (id),"
                                + "KEY idx_br_article (article_id),"
                                + "KEY idx_br_reported (reported_user_id),"
                                + "KEY idx_br_reporter (reporter_user_id),"
                                + "KEY idx_br_status (status),"
                                + "CONSTRAINT fk_br_article FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE ON UPDATE CASCADE,"
                                + "CONSTRAINT fk_br_reported FOREIGN KEY (reported_user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,"
                                + "CONSTRAINT fk_br_reporter FOREIGN KEY (reporter_user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,"
                                + "CONSTRAINT fk_br_admin FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE"
                                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            }
            blogReportSchemaReady = true;
        }
    }
}
