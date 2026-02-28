<%@ page import="java.util.List" %>
<%@ page import="ma.estagadir.forum.model.User" %>
<%@ page import="ma.estagadir.forum.util.HtmlUtil" %>
<%@ page import="ma.estagadir.forum.util.I18n" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    User student = (User) request.getAttribute("student");
    List<String> semestres = (List<String>) request.getAttribute("semestres");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title><%= I18n.t(request, "semester.title") %></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/style.css" />
</head>
<body class="app-bg">
<div class="container page-medium">
  <div class="card">
    <p class="eyebrow"><%= I18n.t(request, "semester.eyebrow") %></p>
    <h2><%= I18n.t(request, "semester.heading") %></h2>
    <p class="meta"><%= I18n.t(request, "semester.fixed") %>: <strong><%= HtmlUtil.escape(student.getFiliere()) %></strong></p>

    <% if (request.getParameter("error") != null) { %>
      <div class="alert-error"><%= I18n.t(request, "semester.invalid") %></div>
    <% } %>

    <form method="post" action="<%=request.getContextPath()%>/student/semester" class="stack-md">
      <label><%= I18n.t(request, "auth.register.semestre") %></label>
      <select name="semestre" required>
        <% for (String s : semestres) { %>
          <option value="<%= HtmlUtil.escape(s) %>" <%= s.equals(student.getSemestre()) ? "selected" : "" %>><%= HtmlUtil.escape(s) %></option>
        <% } %>
      </select>
      <button type="submit" class="btn btn-primary"><%= I18n.t(request, "semester.save") %></button>
    </form>

    <p class="meta page-link"><a href="<%=request.getContextPath()%>/dashboard"><%= I18n.t(request, "app.dashboard") %></a></p>
  </div>
</div>
</body>
</html>


