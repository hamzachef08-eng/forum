<%@ page import="java.util.List" %>
<%@ page import="ma.estagadir.forum.model.User" %>
<%@ page import="ma.estagadir.forum.model.Module" %>
<%@ page import="ma.estagadir.forum.util.HtmlUtil" %>
<%@ page import="ma.estagadir.forum.util.I18n" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    User student = (User) request.getAttribute("student");
    List<Module> modules = (List<Module>) request.getAttribute("modules");
    boolean isAdmin = Boolean.TRUE.equals(request.getAttribute("isAdmin"));
    int modulesCount = request.getAttribute("modulesCount") == null ? 0 : (Integer) request.getAttribute("modulesCount");
    int myArticlesCount = request.getAttribute("myArticlesCount") == null ? 0 : (Integer) request.getAttribute("myArticlesCount");
    int totalArticlesCount = request.getAttribute("totalArticlesCount") == null ? 0 : (Integer) request.getAttribute("totalArticlesCount");
    int openReportsCount = request.getAttribute("openReportsCount") == null ? 0 : (Integer) request.getAttribute("openReportsCount");
    int bannedUsersCount = request.getAttribute("bannedUsersCount") == null ? 0 : (Integer) request.getAttribute("bannedUsersCount");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title><%= I18n.t(request, "dashboard.title") %></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/style.css?v=20260228-1" />
</head>
<body class="app-bg">
<div class="container lg">
  <div class="card option-bar">
    <div class="option-bar-left">
      <a href="<%=request.getContextPath()%>/account" class="btn btn-ghost"><%= I18n.t(request, "dashboard.account") %></a>
    </div>
    <div class="option-bar-right">
      <a href="<%=request.getContextPath()%>/lang?lang=fr&back=/dashboard" class="btn btn-ghost">FR</a>
      <a href="<%=request.getContextPath()%>/lang?lang=en&back=/dashboard" class="btn btn-ghost">EN</a>
      <a href="<%=request.getContextPath()%>/logout" class="btn btn-logout"><%= I18n.t(request, "app.logout") %></a>
    </div>
  </div>

  <div class="card dash-hero">
    <div>
      <p class="eyebrow"><%= I18n.t(request, "dashboard.eyebrow") %></p>
      <h2><%= I18n.t(request, "dashboard.welcome") %> <%= HtmlUtil.escape(student.getFullName()) %></h2>
      <p class="meta"><%= I18n.t(request, "dashboard.filiere") %>: <strong><%= HtmlUtil.escape(student.getFiliere()) %></strong> | <%= I18n.t(request, "dashboard.semestre") %>: <strong><%= HtmlUtil.escape(student.getSemestre()) %></strong></p>
    </div>
  </div>

  <div class="card center-actions">
    <div class="center-actions-wrap">
      <% if (isAdmin) { %>
        <a href="<%=request.getContextPath()%>/admin/reports" class="btn btn-ghost"><%= I18n.t(request, "dashboard.admin_reports") %></a>
        <a href="<%=request.getContextPath()%>/admin/banned" class="btn btn-ghost"><%= I18n.t(request, "admin.banned.title") %></a>
      <% } %>
    </div>
  </div>

  <div class="stats-grid">
    <article class="card stat-card">
      <p class="eyebrow"><%= I18n.t(request, "dashboard.stat.modules") %></p>
      <h3 class="stat-number" data-target="<%= modulesCount %>">0</h3>
      <p class="meta"><%= I18n.t(request, "dashboard.modules") %></p>
    </article>
    <article class="card stat-card">
      <p class="eyebrow"><%= I18n.t(request, "dashboard.stat.my_articles") %></p>
      <h3 class="stat-number" data-target="<%= myArticlesCount %>">0</h3>
      <p class="meta"><%= I18n.t(request, "blog.feed.my") %></p>
    </article>
    <article class="card stat-card">
      <p class="eyebrow"><%= I18n.t(request, "dashboard.stat.community_articles") %></p>
      <h3 class="stat-number" data-target="<%= totalArticlesCount %>">0</h3>
      <p class="meta"><%= I18n.t(request, "blog.feed.community") %></p>
    </article>
    <% if (isAdmin) { %>
      <article class="card stat-card">
        <p class="eyebrow"><%= I18n.t(request, "dashboard.stat.open_reports") %></p>
        <h3 class="stat-number" data-target="<%= openReportsCount %>">0</h3>
        <p class="meta"><%= I18n.t(request, "admin.reports.title") %></p>
      </article>
      <article class="card stat-card">
        <p class="eyebrow"><%= I18n.t(request, "dashboard.stat.banned_users") %></p>
        <h3 class="stat-number" data-target="<%= bannedUsersCount %>">0</h3>
        <p class="meta"><%= I18n.t(request, "admin.banned.title") %></p>
      </article>
    <% } %>
  </div>

  <% if (request.getParameter("updated") != null) { %>
    <div class="alert-success"><%= I18n.t(request, "dashboard.sem_updated") %></div>
  <% } %>

  <div class="card">
    <h3><%= I18n.t(request, "dashboard.modules") %></h3>
    <div class="grid module-grid">
      <% if (modules == null || modules.isEmpty()) { %>
        <p class="meta"><%= I18n.t(request, "dashboard.noModules") %></p>
      <% } else {
           for (Module m : modules) { %>
        <a class="module-btn" href="<%=request.getContextPath()%>/module/chat?id=<%=m.getId()%>">
          <span class="module-title"><%= HtmlUtil.escape(m.getNomModule()) %></span>
          <span class="module-cta"><%= I18n.t(request, "dashboard.open_chat") %></span>
        </a>
      <%   }
         } %>
    </div>
  </div>

  <div class="dashboard-blog-bottom">
    <a class="module-btn module-blog-btn" href="<%=request.getContextPath()%>/blog?scope=community">
      <span class="module-title"><%= I18n.t(request, "dashboard.blog") %></span>
      <span class="module-cta"><%= I18n.t(request, "dashboard.open_blog") %></span>
    </a>
  </div>

</div>
<script>
  (function () {
    const nodes = document.querySelectorAll('.stat-number[data-target]');
    nodes.forEach(function (node) {
      const target = parseInt(node.getAttribute('data-target'), 10) || 0;
      const duration = 850;
      const start = performance.now();

      function tick(now) {
        const progress = Math.min(1, (now - start) / duration);
        const eased = 1 - Math.pow(1 - progress, 3);
        node.textContent = Math.floor(target * eased).toString();
        if (progress < 1) {
          requestAnimationFrame(tick);
        } else {
          node.textContent = target.toString();
        }
      }
      requestAnimationFrame(tick);
    });
  })();
</script>
</body>
</html>


