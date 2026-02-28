package ma.estagadir.forum.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ma.estagadir.forum.model.Question;
import ma.estagadir.forum.util.DbUtil;

public class QuestionDao {
    public List<Question> findByModuleId(long moduleId) throws SQLException {
        String sql = "SELECT q.id, q.module_id, q.author_id, q.title, q.content, q.created_at, u.full_name AS author_name "
                + "FROM questions q JOIN users u ON q.author_id = u.id "
                + "WHERE q.module_id = ? ORDER BY q.created_at DESC";

        List<Question> questions = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, moduleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Question question = new Question();
                    question.setId(rs.getLong("id"));
                    question.setModuleId(rs.getLong("module_id"));
                    question.setAuthorId(rs.getLong("author_id"));
                    question.setTitle(rs.getString("title"));
                    question.setContent(rs.getString("content"));
                    question.setAuthorName(rs.getString("author_name"));
                    if (rs.getTimestamp("created_at") != null) {
                        question.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }
                    questions.add(question);
                }
            }
        }
        return questions;
    }

    public void create(Question question) throws SQLException {
        String sql = "INSERT INTO questions (module_id, author_id, title, content) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, question.getModuleId());
            stmt.setLong(2, question.getAuthorId());
            stmt.setString(3, question.getTitle());
            stmt.setString(4, question.getContent());
            stmt.executeUpdate();
        }
    }
}
