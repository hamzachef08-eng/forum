<%@ page import="ma.estagadir.forum.util.I18n" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title><%= I18n.t(request, "forgot.title") %></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/style.css" />
</head>
<body class="app-bg">
<div class="container page-narrow">
  <div class="card">
    <h2><%= I18n.t(request, "forgot.title") %></h2>
    <p class="meta"><%= I18n.t(request, "forgot.hint") %></p>

    <% if (request.getAttribute("error") != null) { %>
      <div class="alert-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <form method="post" action="<%=request.getContextPath()%>/forgot-password" class="stack-md">
      <label><%= I18n.t(request, "forgot.gmail") %></label>
      <input class="input" type="email" name="email" pattern="^[^\s@]+@gmail\.com$" placeholder="example@gmail.com" required />
      <button type="submit" class="btn btn-primary"><%= I18n.t(request, "forgot.submit") %></button>
    </form>

    <p class="meta page-link"><a href="<%=request.getContextPath()%>/login"><%= I18n.t(request, "forgot.back_login") %></a></p>
  </div>
</div>
</body>
</html>


