<%@ page import="ma.estagadir.forum.util.I18n" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title><%= I18n.t(request, "auth.login.title") %></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/style.css" />
</head>
<body class="app-bg">
<div class="auth-layout">
  <section class="auth-brand">
    <p class="eyebrow">EST Agadir</p>
    <h1><%= I18n.t(request, "auth.brand.title") %></h1>
    <p><%= I18n.t(request, "auth.brand.subtitle") %></p>
  </section>

  <section class="auth-card card">
    <div class="dash-actions" style="justify-content:flex-end; margin-bottom:10px;">
      <a class="btn btn-ghost" href="<%=request.getContextPath()%>/lang?lang=fr&back=<%=request.getServletPath()%>"><%= I18n.t(request, "lang.fr") %></a>
      <a class="btn btn-ghost" href="<%=request.getContextPath()%>/lang?lang=en&back=<%=request.getServletPath()%>"><%= I18n.t(request, "lang.en") %></a>
    </div>

    <h2><%= I18n.t(request, "auth.login.heading") %></h2>
    <p class="meta"><%= I18n.t(request, "auth.login.welcome") %></p>

    <% if (request.getParameter("registered") != null) { %>
      <div class="alert-success"><%= I18n.t(request, "register.verify.sent") %></div>
    <% } %>

    <% if ("ok".equals(request.getParameter("verify"))) { %>
      <div class="alert-success"><%= I18n.t(request, "verify.success") %></div>
    <% } %>

    <% if ("invalid".equals(request.getParameter("verify"))) { %>
      <div class="alert-error"><%= I18n.t(request, "verify.invalid") %></div>
    <% } %>

    <% if (request.getParameter("reset") != null) { %>
      <div class="alert-success"><%= I18n.t(request, "auth.login.reset_ok") %></div>
    <% } %>

    <% if (request.getAttribute("error") != null) { %>
      <div class="alert-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <form method="post" action="<%=request.getContextPath()%>/login" class="stack-md">
      <label><%= I18n.t(request, "auth.login.email") %></label>
      <input class="input" type="email" name="email" required />

      <label><%= I18n.t(request, "auth.login.password") %></label>
      <input class="input" type="password" name="password" required />

      <button type="submit" class="btn btn-primary btn-block"><%= I18n.t(request, "auth.login.submit") %></button>
    </form>

    <p class="meta"><a href="<%=request.getContextPath()%>/forgot-password"><%= I18n.t(request, "auth.login.forgot") %></a></p>
    <p class="meta"><%= I18n.t(request, "auth.login.new_account") %> <a href="<%=request.getContextPath()%>/register"><%= I18n.t(request, "auth.login.create") %></a></p>
  </section>
</div>
</body>
</html>


