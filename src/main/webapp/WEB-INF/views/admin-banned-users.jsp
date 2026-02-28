<%@ page import="java.util.List" %>
<%@ page import="ma.estagadir.forum.model.User" %>
<%@ page import="ma.estagadir.forum.util.HtmlUtil" %>
<%@ page import="ma.estagadir.forum.util.I18n" %>
<%@ page import="ma.estagadir.forum.util.DateTimeUtil" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    List<User> bannedUsers = (List<User>) request.getAttribute("bannedUsers");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title><%= I18n.t(request, "admin.banned.title") %></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/style.css" />
</head>
<body class="app-bg">
<div class="container lg">
  <div class="card option-bar">
    <div class="option-bar-left">
      <a class="btn btn-ghost" href="<%=request.getContextPath()%>/dashboard"><%= I18n.t(request, "app.dashboard") %></a>
      <a class="btn btn-ghost" href="<%=request.getContextPath()%>/admin/reports"><%= I18n.t(request, "admin.reports.title") %></a>
    </div>
    <div class="option-bar-right">
      <a class="btn btn-ghost" href="<%=request.getContextPath()%>/lang?lang=fr&back=/admin/banned">FR</a>
      <a class="btn btn-ghost" href="<%=request.getContextPath()%>/lang?lang=en&back=/admin/banned">EN</a>
    </div>
  </div>

  <div class="card">
    <h2><%= I18n.t(request, "admin.banned.title") %></h2>
    <p class="meta"><%= I18n.t(request, "admin.banned.only") %></p>

    <% if (request.getParameter("done") != null) { %>
      <div class="alert-success"><%= I18n.t(request, "admin.banned.done") %></div>
    <% } %>

    <% if (bannedUsers == null || bannedUsers.isEmpty()) { %>
      <p class="meta"><%= I18n.t(request, "admin.banned.none") %></p>
    <% } else { %>
      <div class="stack-md">
      <% for (User u : bannedUsers) { %>
        <div class="card inset report-card">
          <p><strong><%= HtmlUtil.escape(u.getFullName()) %></strong> - <%= HtmlUtil.escape(u.getEmail()) %></p>
          <p class="meta"><%= I18n.t(request, "dashboard.filiere") %>: <strong><%= HtmlUtil.escape(u.getFiliere()) %></strong> | <%= I18n.t(request, "dashboard.semestre") %>: <strong><%= HtmlUtil.escape(u.getSemestre()) %></strong></p>
          <p class="meta"><%= DateTimeUtil.format(u.getCreatedAt()) %></p>
          <form method="post" action="<%=request.getContextPath()%>/admin/banned">
            <input type="hidden" name="userId" value="<%= u.getId() %>" />
            <button type="submit" class="btn btn-primary"><%= I18n.t(request, "admin.banned.unban") %></button>
          </form>
        </div>
      <% } %>
      </div>
    <% } %>
  </div>
</div>
</body>
</html>
