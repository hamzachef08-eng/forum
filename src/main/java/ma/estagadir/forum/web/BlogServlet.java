package ma.estagadir.forum.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.estagadir.forum.dao.ArticleDao;
import ma.estagadir.forum.dao.UserDao;
import ma.estagadir.forum.model.Article;
import ma.estagadir.forum.util.I18n;

public class BlogServlet extends HttpServlet {
    private final ArticleDao articleDao = new ArticleDao();
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        long userId = (Long) session.getAttribute(SessionKeys.CURRENT_USER_ID);
        String scope = resolveScope(req, "community");
        boolean composeOpen = "1".equals(req.getParameter("compose")) || Boolean.TRUE.equals(req.getAttribute("composeOpen"));
        try {
            List<Article> articles;
            if ("my".equalsIgnoreCase(scope)) {
                articles = articleDao.findAllByAuthor(userId);
            } else if ("community".equalsIgnoreCase(scope)) {
                articles = articleDao.findAllExceptAuthor(userId);
            } else {
                articles = articleDao.findAll();
            }
            req.setAttribute("articles", articles);
            req.setAttribute("scope", scope);
            req.setAttribute("composeOpen", composeOpen);
            req.getRequestDispatcher("/WEB-INF/views/blog.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Unable to load blog", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        long userId = (Long) session.getAttribute(SessionKeys.CURRENT_USER_ID);
        String role = String.valueOf(session.getAttribute(SessionKeys.CURRENT_USER_ROLE));
        String scope = resolveScope(req, "my");

        String action = req.getParameter("action");
        boolean banned = false;
        try {
            if ("delete".equals(action)) {
                handleDelete(req, userId);
            } else if ("banUser".equals(action)) {
                banned = handleBanUser(req, userId, role);
            } else {
                String title = req.getParameter("title");
                String content = req.getParameter("content");
                if (isBlank(title) || isBlank(content)) {
                    req.setAttribute("error", I18n.t(req, "err.required_fields"));
                    req.setAttribute("composeOpen", true);
                    req.setAttribute("scope", "my");
                    doGet(req, resp);
                    return;
                }
                articleDao.create(userId, title.trim(), content.trim());
            }
            String redirect = req.getContextPath() + "/blog?scope=" + scope;
            if (banned) {
                redirect += "&banned=1";
            }
            resp.sendRedirect(redirect);
        } catch (SQLException e) {
            throw new ServletException("Unable to update blog", e);
        }
    }

    private void handleDelete(HttpServletRequest req, long userId) throws SQLException {
        long articleId = parseLong(req.getParameter("articleId"));
        if (articleId <= 0) {
            return;
        }
        Article article = articleDao.findById(articleId);
        if (article == null) {
            return;
        }
        String role = String.valueOf(req.getSession(false).getAttribute(SessionKeys.CURRENT_USER_ROLE));
        if (article.getAuthorId() == userId || "ADMIN".equalsIgnoreCase(role)) {
            articleDao.deleteById(articleId);
        }
    }

    private boolean handleBanUser(HttpServletRequest req, long currentUserId, String role) throws SQLException {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return false;
        }
        long targetUserId = parseLong(req.getParameter("targetUserId"));
        if (targetUserId <= 0 || targetUserId == currentUserId) {
            return false;
        }
        userDao.banUser(targetUserId);
        return true;
    }

    private long parseLong(String raw) {
        if (raw == null) {
            return -1;
        }
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }

    private String resolveScope(HttpServletRequest req, String fallback) {
        Object attr = req.getAttribute("scope");
        String scope = attr == null ? req.getParameter("scope") : String.valueOf(attr);
        if ("my".equalsIgnoreCase(scope) || "community".equalsIgnoreCase(scope)) {
            return scope.toLowerCase();
        }
        return fallback;
    }
}
