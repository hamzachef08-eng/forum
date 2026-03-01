package ma.estagadir.forum.model;

import java.time.LocalDateTime;

public class ModuleMessage {
    private long id;
    private long moduleId;
    private long userId;
    private String userName;
    private String userRole;
    private boolean userBanned;
    private String content;
    private LocalDateTime createdAt;
    private Long parentMessageId;
    private String parentUserName;
    private String parentUserRole;
    private boolean parentUserBanned;
    private String parentContent;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getModuleId() {
        return moduleId;
    }

    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public boolean isUserBanned() {
        return userBanned;
    }

    public void setUserBanned(boolean userBanned) {
        this.userBanned = userBanned;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getParentMessageId() {
        return parentMessageId;
    }

    public void setParentMessageId(Long parentMessageId) {
        this.parentMessageId = parentMessageId;
    }

    public String getParentUserName() {
        return parentUserName;
    }

    public void setParentUserName(String parentUserName) {
        this.parentUserName = parentUserName;
    }

    public String getParentUserRole() {
        return parentUserRole;
    }

    public void setParentUserRole(String parentUserRole) {
        this.parentUserRole = parentUserRole;
    }

    public boolean isParentUserBanned() {
        return parentUserBanned;
    }

    public void setParentUserBanned(boolean parentUserBanned) {
        this.parentUserBanned = parentUserBanned;
    }

    public String getParentContent() {
        return parentContent;
    }

    public void setParentContent(String parentContent) {
        this.parentContent = parentContent;
    }
}
