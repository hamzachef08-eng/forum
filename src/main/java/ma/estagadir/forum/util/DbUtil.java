package ma.estagadir.forum.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DbUtil {
    private static String url;
    private static String user;
    private static String password;

    private DbUtil() {
    }

    public static void configure(String driver, String jdbcUrl, String jdbcUser, String jdbcPassword)
            throws ClassNotFoundException {
        url = jdbcUrl;
        user = jdbcUser;
        password = jdbcPassword;
        if (driver != null && !driver.isBlank()) {
            Class.forName(driver);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (url == null) {
            throw new IllegalStateException("Database is not configured.");
        }
        return DriverManager.getConnection(url, user, password);
    }
}
