<%@ page import="java.util.List" %>
<%@ page import="ma.estagadir.forum.model.Article" %>
<%@ page import="ma.estagadir.forum.util.HtmlUtil" %>
<%@ page import="ma.estagadir.forum.util.I18n" %>
<%@ page import="ma.estagadir.forum.util.DateTimeUtil" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    List<Article> articles = (List<Article>) request.getAttribute("articles");
    String role = String.valueOf(session.getAttribute("currentUserRole"));
    long currentUserId = (Long) session.getAttribute("currentUserId");
    String scope = String.valueOf(request.getAttribute("scope"));
    boolean composeOpen = Boolean.TRUE.equals(request.getAttribute("composeOpen"));
    boolean myScope = "my".equalsIgnoreCase(scope);
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title><%= I18n.t(request, "blog.title") %></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/style.css?v=20260228-2" />
</head>
<body class="app-bg">
<div class="container lg">
  <div class="card option-bar">
    <div class="option-bar-left">
      <a class="btn btn-ghost" href="<%=request.getContextPath()%>/dashboard"><%= I18n.t(request, "app.dashboard") %></a>
      <a class="btn btn-ghost <%= !myScope ? "btn-nav-active" : "" %>" href="<%=request.getContextPath()%>/blog?scope=community"><%= I18n.t(request, "blog.feed.community") %></a>
      <a class="btn btn-ghost <%= myScope ? "btn-nav-active" : "" %>" href="<%=request.getContextPath()%>/blog?scope=my"><%= I18n.t(request, "blog.feed.my") %></a>
    </div>
    <div class="option-bar-right">
      <a class="btn btn-ghost" href="<%=request.getContextPath()%>/lang?lang=fr&back=/blog">FR</a>
      <a class="btn btn-ghost" href="<%=request.getContextPath()%>/lang?lang=en&back=/blog">EN</a>
      <a class="btn btn-logout" href="<%=request.getContextPath()%>/logout"><%= I18n.t(request, "app.logout") %></a>
    </div>
  </div>

  <div class="card dash-hero">
    <div>
      <p class="eyebrow">EST Blog</p>
      <h2><%= I18n.t(request, "blog.title") %></h2>
      <p class="meta"><%= myScope ? I18n.t(request, "blog.feed.my") : I18n.t(request, "blog.feed.community") %></p>
    </div>
    <% if (myScope) { %>
      <div>
        <a class="btn btn-primary" href="#" id="toggleComposeBtn"><%= composeOpen ? I18n.t(request, "blog.hide_button") : I18n.t(request, "blog.add_button") %></a>
      </div>
    <% } else { %>
      <div>
        <a class="btn btn-primary" href="<%=request.getContextPath()%>/blog?scope=my&compose=1#addBlogForm"><%= I18n.t(request, "blog.add_button") %></a>
      </div>
    <% } %>
  </div>

  <% if (myScope) { %>
  <div class="card compose-card <%= composeOpen ? "open" : "" %>" id="addBlogForm">
    <h3><%= I18n.t(request, "blog.new") %></h3>
    <% if (request.getAttribute("error") != null) { %>
      <div class="alert-error"><%= request.getAttribute("error") %></div>
    <% } %>
    <form method="post" action="<%=request.getContextPath()%>/blog" class="stack-md">
      <input type="hidden" name="scope" value="my" />
      <label><%= I18n.t(request, "blog.articleTitle") %></label>
      <input class="input" name="title" maxlength="180" required />
      <label><%= I18n.t(request, "blog.articleContent") %></label>
      <textarea name="content" rows="4" required></textarea>
      <button class="btn btn-primary" type="submit"><%= I18n.t(request, "blog.publish") %></button>
    </form>
  </div>
  <% } %>

  <div class="community-feed-area">
    <h3><%= I18n.t(request, "blog.list") %></h3>
    <% if ("1".equals(request.getParameter("banned"))) { %>
      <div class="alert-success"><%= I18n.t(request, "blog.ban_done") %></div>
    <% } %>
    <div class="stack-lg">
      <% if (articles == null || articles.isEmpty()) { %>
        <div class="card">
          <p class="meta"><%= I18n.t(request, "blog.none") %></p>
        </div>
      <% } else {
           for (Article a : articles) {
             boolean articleGhosted = a.isAuthorBanned();
             String shownTitle = articleGhosted ? I18n.t(request, "blog.ghosted_title") : a.getTitle();
             String shownContent = articleGhosted ? I18n.t(request, "blog.ghosted_content") : a.getContent();
      %>
        <article class="card inset <%= articleGhosted ? "ghosted-surface" : "" %>">
          <h3><a href="<%=request.getContextPath()%>/article?id=<%=a.getId()%>"><%= HtmlUtil.escape(shownTitle) %></a></h3>
          <p class="meta">
            <%= I18n.t(request, "blog.by") %>
            <strong><%= "ADMIN".equalsIgnoreCase(String.valueOf(a.getAuthorRole())) ? "ADMIN" : HtmlUtil.escape(a.getAuthorName()) %></strong>
            - <%= DateTimeUtil.format(a.getCreatedAt()) %>
          </p>
          <p class="<%= articleGhosted ? "ghosted-text" : "" %>"><%= HtmlUtil.escape(shownContent) %></p>
          <% if (a.getAuthorId() == currentUserId || "ADMIN".equalsIgnoreCase(role)) { %>
            <div class="blog-actions">
              <a class="btn btn-ghost" href="<%=request.getContextPath()%>/article?id=<%=a.getId()%>"><%= I18n.t(request, "blog.follow_comments") %></a>
              <form method="post" action="<%=request.getContextPath()%>/blog">
                <input type="hidden" name="action" value="delete" />
                <input type="hidden" name="scope" value="<%= HtmlUtil.escape(scope) %>" />
                <input type="hidden" name="articleId" value="<%=a.getId()%>" />
                <button class="btn btn-ghost" type="submit"><%= I18n.t(request, "blog.delete") %></button>
              </form>
              <% if ("ADMIN".equalsIgnoreCase(role) && a.getAuthorId() != currentUserId && !articleGhosted) { %>
                <form method="post" action="<%=request.getContextPath()%>/blog">
                  <input type="hidden" name="action" value="banUser" />
                  <input type="hidden" name="scope" value="<%= HtmlUtil.escape(scope) %>" />
                  <input type="hidden" name="targetUserId" value="<%=a.getAuthorId()%>" />
                  <button class="btn btn-logout" type="submit"><%= I18n.t(request, "blog.ban_user") %></button>
                </form>
              <% } %>
            </div>
          <% } %>
        </article>
      <%   }
         } %>
    </div>
  </div>
</div>
<script>
  (function () {
    const toggleBtn = document.getElementById('toggleComposeBtn');
    const formCard = document.getElementById('addBlogForm');
    if (!toggleBtn || !formCard) return;

    const showText = '<%= I18n.t(request, "blog.add_button") %>';
    const hideText = '<%= I18n.t(request, "blog.hide_button") %>';

    function isOpen() {
      return formCard.classList.contains('open');
    }

    toggleBtn.addEventListener('click', function (e) {
      e.preventDefault();
      if (isOpen()) {
        formCard.classList.remove('open');
        toggleBtn.textContent = showText;
      } else {
        formCard.classList.add('open');
        toggleBtn.textContent = hideText;
        const titleInput = formCard.querySelector('input[name=\"title\"]');
        if (titleInput) titleInput.focus();
      }
    });
  })();
</script>
</body>
</html>


