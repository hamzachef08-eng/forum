package ma.estagadir.forum.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ma.estagadir.forum.model.ArticleComment;
import ma.estagadir.forum.util.DbUtil;

public class ArticleCommentDao {
    public long create(long articleId, long authorId, String content) throws SQLException {
        ArticleDao.ensureBlogSchema();
        String sql = "INSERT INTO article_comments (article_id, author_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, articleId);
            stmt.setLong(2, authorId);
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

    public List<ArticleComment> findByArticle(long articleId) throws SQLException {
        ArticleDao.ensureBlogSchema();
        String sql = "SELECT c.id, c.article_id, c.author_id, c.content, c.created_at, CONCAT(u.prenom, ' ', u.nom) AS author_name, u.role AS author_role, u.is_banned AS author_banned "
                + "FROM article_comments c JOIN users u ON u.id = c.author_id "
                + "WHERE c.article_id = ? ORDER BY c.created_at ASC";
        List<ArticleComment> rows = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, articleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapRow(rs));
                }
            }
        }
        return rows;
    }

    public void delete(long commentId) throws SQLException {
        ArticleDao.ensureBlogSchema();
        String sql = "DELETE FROM article_comments WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, commentId);
            stmt.executeUpdate();
        }
    }

    public ArticleComment findById(long commentId) throws SQLException {
        ArticleDao.ensureBlogSchema();
        String sql = "SELECT c.id, c.article_id, c.author_id, c.content, c.created_at, CONCAT(u.prenom, ' ', u.nom) AS author_name, u.role AS author_role, u.is_banned AS author_banned "
                + "FROM article_comments c JOIN users u ON u.id = c.author_id WHERE c.id = ?";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, commentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    private ArticleComment mapRow(ResultSet rs) throws SQLException {
        ArticleComment c = new ArticleComment();
        c.setId(rs.getLong("id"));
        c.setArticleId(rs.getLong("article_id"));
        c.setAuthorId(rs.getLong("author_id"));
        c.setAuthorName(rs.getString("author_name"));
        c.setAuthorRole(rs.getString("author_role"));
        c.setAuthorBanned(rs.getBoolean("author_banned"));
        c.setContent(rs.getString("content"));
        if (rs.getTimestamp("created_at") != null) {
            c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        return c;
    }
}
