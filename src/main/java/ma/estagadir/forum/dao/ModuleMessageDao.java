package ma.estagadir.forum.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import ma.estagadir.forum.model.ModuleMessage;
import ma.estagadir.forum.util.DbUtil;

public class ModuleMessageDao {
    public List<ModuleMessage> findByModuleId(long moduleId) throws SQLException {
        String sql = "SELECT mm.id, mm.module_id, mm.user_id, mm.content, mm.created_at, mm.parent_message_id, "
                + "CONCAT(u.prenom, ' ', u.nom) AS user_name, u.role AS user_role, u.is_banned AS user_banned, "
                + "CONCAT(pu.prenom, ' ', pu.nom) AS parent_user_name, pu.role AS parent_user_role, pu.is_banned AS parent_user_banned, pm.content AS parent_content "
                + "FROM module_messages mm "
                + "JOIN users u ON u.id = mm.user_id "
                + "LEFT JOIN module_messages pm ON pm.id = mm.parent_message_id "
                + "LEFT JOIN users pu ON pu.id = pm.user_id "
                + "WHERE mm.module_id = ? ORDER BY mm.created_at ASC, mm.id ASC";

        List<ModuleMessage> messages = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, moduleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ModuleMessage msg = new ModuleMessage();
                    msg.setId(rs.getLong("id"));
                    msg.setModuleId(rs.getLong("module_id"));
                    msg.setUserId(rs.getLong("user_id"));
                    msg.setUserName(rs.getString("user_name"));
                    msg.setUserRole(rs.getString("user_role"));
                    msg.setUserBanned(rs.getBoolean("user_banned"));
                    msg.setContent(rs.getString("content"));
                    long parentId = rs.getLong("parent_message_id");
                    if (!rs.wasNull()) {
                        msg.setParentMessageId(parentId);
                    }
                    msg.setParentUserName(rs.getString("parent_user_name"));
                    msg.setParentUserRole(rs.getString("parent_user_role"));
                    msg.setParentUserBanned(rs.getBoolean("parent_user_banned"));
                    msg.setParentContent(rs.getString("parent_content"));
                    if (rs.getTimestamp("created_at") != null) {
                        msg.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }
                    messages.add(msg);
                }
            }
        }
        return messages;
    }

    public void create(long moduleId, long userId, String content, Long parentMessageId) throws SQLException {
        Long validParentId = resolveParentInSameModule(moduleId, parentMessageId);
        String sql = "INSERT INTO module_messages (module_id, user_id, content, parent_message_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, moduleId);
            stmt.setLong(2, userId);
            stmt.setString(3, content);
            if (validParentId == null) {
                stmt.setNull(4, Types.BIGINT);
            } else {
                stmt.setLong(4, validParentId);
            }
            stmt.executeUpdate();
        }
    }

    private Long resolveParentInSameModule(long moduleId, Long parentMessageId) throws SQLException {
        if (parentMessageId == null) {
            return null;
        }
        String sql = "SELECT id FROM module_messages WHERE id = ? AND module_id = ?";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, parentMessageId);
            stmt.setLong(2, moduleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parentMessageId;
                }
            }
        }
        return null;
    }
}
