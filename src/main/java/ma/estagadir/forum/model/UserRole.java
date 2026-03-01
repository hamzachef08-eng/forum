package ma.estagadir.forum.model;

public enum UserRole {
    STUDENT,
    ADMIN,
    TEACHER;

    public static UserRole fromString(String value) {
        for (UserRole role : values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        return STUDENT;
    }
}
