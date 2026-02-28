package ma.estagadir.forum.web;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import ma.estagadir.forum.util.DbUtil;

public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        String driver = firstNonBlank(System.getenv("DB_DRIVER"), ctx.getInitParameter("db.driver"));
        String url = firstNonBlank(System.getenv("DB_URL"), ctx.getInitParameter("db.url"));
        String user = firstNonBlank(System.getenv("DB_USER"), ctx.getInitParameter("db.user"));
        String password = firstNonBlank(System.getenv("DB_PASSWORD"), ctx.getInitParameter("db.password"));
        try {
            DbUtil.configure(driver, url, user, password);
        } catch (ClassNotFoundException e) {
            // Do not fail deployment when DB driver is missing.
            ctx.log("JDBC driver not found: " + driver + ". Add driver JAR to WEB-INF/lib.", e);
        }
    }

    private String firstNonBlank(String preferred, String fallback) {
        if (preferred != null && !preferred.isBlank()) {
            return preferred;
        }
        return fallback;
    }
}
