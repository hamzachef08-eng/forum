package ma.estagadir.forum.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ma.estagadir.forum.model.Article;
import ma.estagadir.forum.util.DbUtil;

public class ArticleDao {
    private static volatile boolean blogSchemaReady;

    public long create(long authorId, String title, String content) throws SQLException {
        ensureBlogSchema();
        String sql = "INSERT INTO articles (author_id, title, content) VALUES (?, ?, ?)";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, authorId);
            stmt.setString(2, title);
            stmt.setString(3, content);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            return -1L;
        }
    }

    public List<Article> findAll() throws SQLException {
        ensureBlogSchema();
        String sql = "SELECT a.id, a.author_id, a.title, a.content, a.created_at, CONCAT(u.prenom, ' ', u.nom) AS author_name, u.role AS author_role, u.is_banned AS author_banned "
                + "FROM articles a JOIN users u ON u.id = a.author_id ORDER BY a.created_at DESC";
        List<Article> rows = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                rows.add(mapRow(rs));
            }
        }
        return rows;
    }

    public List<Article> findAllExceptAuthor(long userId) throws SQLException {
        ensureBlogSchema();
        String sql = "SELECT a.id, a.author_id, a.title, a.content, a.created_at, CONCAT(u.prenom, ' ', u.nom) AS author_name, u.role AS author_role, u.is_banned AS author_banned "
                + "FROM articles a JOIN users u ON u.id = a.author_id "
                + "WHERE a.author_id <> ? ORDER BY a.created_at DESC";
        List<Article> rows = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapRow(rs));
                }
            }
        }
        return rows;
    }

    public List<Article> findAllByAuthor(long userId) throws SQLException {
        ensureBlogSchema();
        String sql = "SELECT a.id, a.author_id, a.title, a.content, a.created_at, CONCAT(u.prenom, ' ', u.nom) AS author_name, u.role AS author_role, u.is_banned AS author_banned "
                + "FROM articles a JOIN users u ON u.id = a.author_id "
                + "WHERE a.author_id = ? ORDER BY a.created_at DESC";
        List<Article> rows = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapRow(rs));
                }
            }
        }
        return rows;
    }

    public Article findLatest() throws SQLException {
        ensureBlogSchema();
        String sql = "SELECT a.id, a.author_id, a.title, a.content, a.created_at, CONCAT(u.prenom, ' ', u.nom) AS author_name, u.role AS author_role, u.is_banned AS author_banned "
                + "FROM articles a JOIN users u ON u.id = a.author_id ORDER BY a.created_at DESC LIMIT 1";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return mapRow(rs);
            }
            return null;
        }
    }

    public Article findById(long articleId) throws SQLException {
        ensureBlogSchema();
        String sql = "SELECT a.id, a.author_id, a.title, a.content, a.created_at, CONCAT(u.prenom, ' ', u.nom) AS author_name, u.role AS author_role, u.is_banned AS author_banned "
                + "FROM articles a JOIN users u ON u.id = a.author_id WHERE a.id = ?";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, articleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public void deleteById(long articleId) throws SQLException {
        ensureBlogSchema();
        String sql = "DELETE FROM articles WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, articleId);
            stmt.executeUpdate();
        }
    }

    private Article mapRow(ResultSet rs) throws SQLException {
        Article a = new Article();
        a.setId(rs.getLong("id"));
        a.setAuthorId(rs.getLong("author_id"));
        a.setAuthorName(rs.getString("author_name"));
        a.setAuthorRole(rs.getString("author_role"));
        a.setAuthorBanned(rs.getBoolean("author_banned"));
        a.setTitle(rs.getString("title"));
        a.setContent(rs.getString("content"));
        if (rs.getTimestamp("created_at") != null) {
            a.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        return a;
    }

    static void ensureBlogSchema() throws SQLException {
        if (blogSchemaReady) {
            return;
        }
        synchronized (ArticleDao.class) {
            if (blogSchemaReady) {
                return;
            }
            try (Connection conn = DbUtil.getConnection(); Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS articles ("
                                + "id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
                                + "author_id BIGINT UNSIGNED NOT NULL,"
                                + "title VARCHAR(180) NOT NULL,"
                                + "content TEXT NOT NULL,"
                                + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                                + "PRIMARY KEY (id),"
                                + "KEY idx_articles_author (author_id),"
                                + "CONSTRAINT fk_articles_author FOREIGN KEY (author_id) REFERENCES users(id) "
                                + "ON DELETE CASCADE ON UPDATE CASCADE"
                                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS article_comments ("
                                + "id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
                                + "article_id BIGINT UNSIGNED NOT NULL,"
                                + "author_id BIGINT UNSIGNED NOT NULL,"
                                + "content TEXT NOT NULL,"
                                + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                                + "PRIMARY KEY (id),"
                                + "KEY idx_ac_article (article_id),"
                                + "KEY idx_ac_author (author_id),"
                                + "CONSTRAINT fk_ac_article FOREIGN KEY (article_id) REFERENCES articles(id) "
                                + "ON DELETE CASCADE ON UPDATE CASCADE,"
                                + "CONSTRAINT fk_ac_author FOREIGN KEY (author_id) REFERENCES users(id) "
                                + "ON DELETE CASCADE ON UPDATE CASCADE"
                                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            }
            blogSchemaReady = true;
        }
    }
}
