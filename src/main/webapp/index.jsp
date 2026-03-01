<%@ page import="ma.estagadir.forum.util.I18n" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    Object currentUser = session.getAttribute("currentUserId");
    if (currentUser != null) {
        response.sendRedirect(request.getContextPath() + "/dashboard");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title>EST Agadir Forum</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/style.css?v=20260301-1" />
</head>
<body class="app-bg home-bg">
  <main class="home-shell">
    <section class="home-card">
      <div class="home-top">
        <div class="dash-actions">
          <a class="btn btn-ghost" href="<%=request.getContextPath()%>/lang?lang=fr&back=/"><%= I18n.t(request, "lang.fr") %></a>
          <a class="btn btn-ghost" href="<%=request.getContextPath()%>/lang?lang=en&back=/"><%= I18n.t(request, "lang.en") %></a>
        </div>
      </div>

      <p class="eyebrow"><%= I18n.t(request, "home.kicker") %></p>
      <h1 class="home-title"><%= I18n.t(request, "home.title") %></h1>
      <p class="home-subtitle"><%= I18n.t(request, "home.subtitle") %></p>

      <div class="home-grid">
        <article class="home-feature">
          <span class="home-pill">MODULES</span>
          <h3><%= I18n.t(request, "home.card.modules") %></h3>
          <p><%= I18n.t(request, "home.card.modules_text") %></p>
        </article>
        <article class="home-feature">
          <span class="home-pill">BLOG</span>
          <h3><%= I18n.t(request, "home.card.blog") %></h3>
          <p><%= I18n.t(request, "home.card.blog_text") %></p>
        </article>
        <article class="home-feature">
          <span class="home-pill">ADMIN</span>
          <h3><%= I18n.t(request, "home.card.admin") %></h3>
          <p><%= I18n.t(request, "home.card.admin_text") %></p>
        </article>
        <article class="home-feature">
          <span class="home-pill">SECURE</span>
          <h3><%= I18n.t(request, "home.card.secure") %></h3>
          <p><%= I18n.t(request, "home.card.secure_text") %></p>
        </article>
      </div>

      <div class="home-actions">
        <a class="btn btn-primary home-btn-primary" href="<%=request.getContextPath()%>/login"><%= I18n.t(request, "home.login") %></a>
        <a class="btn btn-ghost home-btn-secondary" href="<%=request.getContextPath()%>/register"><%= I18n.t(request, "home.register") %></a>
      </div>
    </section>
  </main>
</body>
</html>
