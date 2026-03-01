package ma.estagadir.forum.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ma.estagadir.forum.model.Answer;
import ma.estagadir.forum.util.DbUtil;

public class AnswerDao {
    public Map<Long, List<Answer>> findByModuleIdGrouped(long moduleId) throws SQLException {
        String sql = "SELECT a.id, a.question_id, a.author_id, a.content, a.created_at, u.full_name AS author_name "
                + "FROM answers a "
                + "JOIN questions q ON a.question_id = q.id "
                + "JOIN users u ON a.author_id = u.id "
                + "WHERE q.module_id = ? "
                + "ORDER BY a.created_at ASC";

        Map<Long, List<Answer>> groupedAnswers = new HashMap<>();
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, moduleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Answer answer = new Answer();
                    answer.setId(rs.getLong("id"));
                    answer.setQuestionId(rs.getLong("question_id"));
                    answer.setAuthorId(rs.getLong("author_id"));
                    answer.setContent(rs.getString("content"));
                    answer.setAuthorName(rs.getString("author_name"));
                    if (rs.getTimestamp("created_at") != null) {
                        answer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }

                    groupedAnswers.computeIfAbsent(answer.getQuestionId(), key -> new ArrayList<>()).add(answer);
                }
            }
        }
        return groupedAnswers;
    }

    public void create(Answer answer) throws SQLException {
        String sql = "INSERT INTO answers (question_id, author_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, answer.getQuestionId());
            stmt.setLong(2, answer.getAuthorId());
            stmt.setString(3, answer.getContent());
            stmt.executeUpdate();
        }
    }
}
