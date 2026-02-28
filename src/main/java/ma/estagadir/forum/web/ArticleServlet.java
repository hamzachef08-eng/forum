package ma.estagadir.forum.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.estagadir.forum.dao.BlogReportDao;
import ma.estagadir.forum.dao.ArticleCommentDao;
import ma.estagadir.forum.dao.ArticleDao;
import ma.estagadir.forum.dao.UserDao;
import ma.estagadir.forum.model.Article;
import ma.estagadir.forum.model.ArticleComment;
import ma.estagadir.forum.util.I18n;

public class ArticleServlet extends HttpServlet {
    private final ArticleDao articleDao = new ArticleDao();
    private final ArticleCommentDao commentDao = new ArticleCommentDao();
    private final BlogReportDao blogReportDao = new BlogReportDao();
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long articleId = parseLong(req.getParameter("id"));
        if (articleId <= 0) {
            resp.sendRedirect(req.getContextPath() + "/blog");
            return;
        }

        try {
            Article article = articleDao.findById(articleId);
            if (article == null) {
                resp.sendRedirect(req.getContextPath() + "/blog");
                return;
            }
            List<ArticleComment> comments = commentDao.findByArticle(articleId);
            req.setAttribute("article", article);
            req.setAttribute("comments", comments);
            req.getRequestDispatcher("/WEB-INF/views/article.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Unable to load article", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        long userId = (Long) session.getAttribute(SessionKeys.CURRENT_USER_ID);
        String role = String.valueOf(session.getAttribute(SessionKeys.CURRENT_USER_ROLE));
        long articleId = parseLong(req.getParameter("articleId"));

        if (articleId <= 0) {
            resp.sendRedirect(req.getContextPath() + "/blog");
            return;
        }

        String action = req.getParameter("action");
        boolean banned = false;
        try {
            Article article = articleDao.findById(articleId);
            if (article == null) {
                resp.sendRedirect(req.getContextPath() + "/blog");
                return;
            }

            if ("banArticleAuthor".equals(action)) {
                if ("ADMIN".equalsIgnoreCase(role) && article.getAuthorId() != userId) {
                    userDao.banUser(article.getAuthorId());
                    banned = true;
                }
            } else if ("reportArticle".equals(action)) {
                if (article.getAuthorId() == userId) {
                    resp.sendRedirect(req.getContextPath() + "/article?id=" + articleId + "&report=own");
                    return;
                }
                if ("ADMIN".equalsIgnoreCase(String.valueOf(article.getAuthorRole()))) {
                    resp.sendRedirect(req.getContextPath() + "/article?id=" + articleId + "&report=admin");
                    return;
                }
                if (blogReportDao.hasOpenReport(articleId, userId)) {
                    resp.sendRedirect(req.getContextPath() + "/article?id=" + articleId + "&report=exists");
                    return;
                }
                blogReportDao.createReport(articleId, article.getAuthorId(), userId, I18n.t(req, "blog.report_reason"));
                resp.sendRedirect(req.getContextPath() + "/article?id=" + articleId + "&report=1");
                return;
            } else if ("banCommentAuthor".equals(action)) {
                long commentId = parseLong(req.getParameter("commentId"));
                ArticleComment c = commentDao.findById(commentId);
                if (c != null && c.getArticleId() == articleId
                        && "ADMIN".equalsIgnoreCase(role)
                        && c.getAuthorId() != userId) {
                    userDao.banUser(c.getAuthorId());
                    banned = true;
                }
            } else if ("deleteComment".equals(action)) {
                long commentId = parseLong(req.getParameter("commentId"));
                ArticleComment c = commentDao.findById(commentId);
                if (c != null && c.getArticleId() == articleId
                        && (c.getAuthorId() == userId || "ADMIN".equalsIgnoreCase(role))) {
                    commentDao.delete(commentId);
                }
            } else {
                if (article.getAuthorId() == userId) {
                    resp.sendRedirect(req.getContextPath() + "/article?id=" + articleId + "&selfComment=1");
                    return;
                }
                String content = req.getParameter("content");
                if (content != null && !content.trim().isEmpty()) {
                    commentDao.create(articleId, userId, content.trim());
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Unable to update comments", e);
        }

        String redirect = req.getContextPath() + "/article?id=" + articleId;
        if (banned) {
            redirect += "&banned=1";
        }
        resp.sendRedirect(redirect);
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
}
