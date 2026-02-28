<%@ page import="java.util.List" %>
<%@ page import="ma.estagadir.forum.model.Article" %>
<%@ page import="ma.estagadir.forum.model.ArticleComment" %>
<%@ page import="ma.estagadir.forum.util.HtmlUtil" %>
<%@ page import="ma.estagadir.forum.util.I18n" %>
<%@ page import="ma.estagadir.forum.util.DateTimeUtil" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    Article article = (Article) request.getAttribute("article");
    List<ArticleComment> comments = (List<ArticleComment>) request.getAttribute("comments");
    long currentUserId = (Long) session.getAttribute("currentUserId");
    String role = String.valueOf(session.getAttribute("currentUserRole"));
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title><%= article == null ? I18n.t(request, "blog.list") : HtmlUtil.escape(article.getTitle()) %></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/style.css?v=20260228-2" />
</head>
<body class="app-bg">
<div class="container lg">
  <div class="card">
    <a href="<%=request.getContextPath()%>/blog">&larr; <%= I18n.t(request, "blog.back") %></a>
<% if (article != null) { %>
      <% boolean articleGhosted = article.isAuthorBanned(); %>
      <% String shownArticleTitle = articleGhosted ? I18n.t(request, "blog.ghosted_title") : article.getTitle(); %>
      <% String shownArticleContent = articleGhosted ? I18n.t(request, "blog.ghosted_content") : article.getContent(); %>
      <h2 class="<%= articleGhosted ? "ghosted-text" : "" %>"><%= HtmlUtil.escape(shownArticleTitle) %></h2>
      <p class="meta">
        <%= I18n.t(request, "blog.by") %>
        <strong><%= "ADMIN".equalsIgnoreCase(String.valueOf(article.getAuthorRole())) ? "ADMIN" : HtmlUtil.escape(article.getAuthorName()) %></strong>
        - <%= DateTimeUtil.format(article.getCreatedAt()) %>
      </p>
      <p class="<%= articleGhosted ? "ghosted-text" : "" %>"><%= HtmlUtil.escape(shownArticleContent) %></p>
      <% if (article.getAuthorId() != currentUserId
              && !articleGhosted
              && !"ADMIN".equalsIgnoreCase(String.valueOf(article.getAuthorRole()))) { %>
        <form method="post" action="<%=request.getContextPath()%>/article" class="top-gap">
          <input type="hidden" name="action" value="reportArticle" />
          <input type="hidden" name="articleId" value="<%=article.getId()%>" />
          <button class="btn btn-ghost" type="submit"><%= I18n.t(request, "blog.report") %></button>
        </form>
      <% } %>
      <% if ("ADMIN".equalsIgnoreCase(role) && article.getAuthorId() != currentUserId && !articleGhosted) { %>
        <form method="post" action="<%=request.getContextPath()%>/article" class="top-gap">
          <input type="hidden" name="action" value="banArticleAuthor" />
          <input type="hidden" name="articleId" value="<%=article.getId()%>" />
          <button class="btn btn-logout" type="submit"><%= I18n.t(request, "blog.ban_user") %></button>
        </form>
      <% } %>
    <% } %>
  </div>

  <div class="card">
    <h3><%= I18n.t(request, "blog.comments") %></h3>
    <% if ("1".equals(request.getParameter("report"))) { %>
      <div class="alert-success"><%= I18n.t(request, "blog.report_ok") %></div>
    <% } %>
    <% if ("own".equals(request.getParameter("report"))) { %>
      <div class="alert-error"><%= I18n.t(request, "blog.report_own") %></div>
    <% } %>
    <% if ("admin".equals(request.getParameter("report"))) { %>
      <div class="alert-error"><%= I18n.t(request, "blog.report_admin") %></div>
    <% } %>
    <% if ("exists".equals(request.getParameter("report"))) { %>
      <div class="alert-error"><%= I18n.t(request, "blog.report_exists") %></div>
    <% } %>
    <% if ("1".equals(request.getParameter("banned"))) { %>
      <div class="alert-success"><%= I18n.t(request, "blog.ban_done") %></div>
    <% } %>
    <% if ("1".equals(request.getParameter("selfComment"))) { %>
      <div class="alert-error"><%= I18n.t(request, "blog.self_comment_blocked") %></div>
    <% } %>
    <div class="stack-md">
      <% if (comments == null || comments.isEmpty()) { %>
        <p class="meta"><%= I18n.t(request, "blog.no_comment") %></p>
      <% } else {
           for (ArticleComment c : comments) {
             boolean commentGhosted = c.isAuthorBanned();
             String shownCommentContent = commentGhosted ? I18n.t(request, "chat.ghosted") : c.getContent();
        %>
        <div class="card inset <%= commentGhosted ? "ghosted-surface" : "" %>">
          <p class="<%= commentGhosted ? "ghosted-text" : "" %>"><%= HtmlUtil.escape(shownCommentContent) %></p>
          <p class="meta">
            <strong><%= "ADMIN".equalsIgnoreCase(String.valueOf(c.getAuthorRole())) ? "ADMIN" : HtmlUtil.escape(c.getAuthorName()) %></strong>
            - <%= DateTimeUtil.format(c.getCreatedAt()) %>
          </p>
          <% if (c.getAuthorId() == currentUserId || "ADMIN".equalsIgnoreCase(role)) { %>
            <div class="blog-actions">
              <form method="post" action="<%=request.getContextPath()%>/article">
                <input type="hidden" name="action" value="deleteComment" />
                <input type="hidden" name="articleId" value="<%=article.getId()%>" />
                <input type="hidden" name="commentId" value="<%=c.getId()%>" />
                <button class="btn btn-ghost" type="submit"><%= I18n.t(request, "blog.delete") %></button>
              </form>
              <% if ("ADMIN".equalsIgnoreCase(role) && c.getAuthorId() != currentUserId && !commentGhosted) { %>
                <form method="post" action="<%=request.getContextPath()%>/article">
                  <input type="hidden" name="action" value="banCommentAuthor" />
                  <input type="hidden" name="articleId" value="<%=article.getId()%>" />
                  <input type="hidden" name="commentId" value="<%=c.getId()%>" />
                  <button class="btn btn-logout" type="submit"><%= I18n.t(request, "blog.ban_user") %></button>
                </form>
              <% } %>
            </div>
          <% } %>
        </div>
      <%   }
         } %>
    </div>

    <% if (article != null && article.getAuthorId() != currentUserId) { %>
      <form method="post" action="<%=request.getContextPath()%>/article" class="stack-md top-gap">
        <input type="hidden" name="articleId" value="<%=article.getId()%>" />
        <label><%= I18n.t(request, "blog.addComment") %></label>
        <textarea name="content" rows="3" required></textarea>
        <button class="btn btn-primary" type="submit"><%= I18n.t(request, "chat.send") %></button>
      </form>
    <% } else { %>
      <p class="meta top-gap"><%= I18n.t(request, "blog.self_comment_blocked") %></p>
    <% } %>
  </div>
</div>
</body>
</html>


