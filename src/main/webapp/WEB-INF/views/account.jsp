<%@ page import="java.util.List" %>
<%@ page import="ma.estagadir.forum.model.User" %>
<%@ page import="ma.estagadir.forum.util.HtmlUtil" %>
<%@ page import="ma.estagadir.forum.util.I18n" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    User user = (User) request.getAttribute("user");
    List<String> filieres = (List<String>) request.getAttribute("filieres");
    List<String> semestres = (List<String>) request.getAttribute("semestres");
    boolean isAdmin = "ADMIN".equals(String.valueOf(session.getAttribute("currentUserRole")));
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title><%= I18n.t(request, "account.title") %></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/style.css" />
</head>
<body class="app-bg">
<div class="container page-wide">
  <div class="card option-bar">
    <div class="option-bar-left">
      <a class="btn btn-ghost" href="<%=request.getContextPath()%>/dashboard"><%= I18n.t(request, "app.dashboard") %></a>
    </div>
    <div class="option-bar-right">
      <a class="btn btn-ghost" href="<%=request.getContextPath()%>/lang?lang=fr&back=/account">FR</a>
      <a class="btn btn-ghost" href="<%=request.getContextPath()%>/lang?lang=en&back=/account">EN</a>
      <a class="btn btn-logout" href="<%=request.getContextPath()%>/logout"><%= I18n.t(request, "app.logout") %></a>
    </div>
  </div>

  <div class="card">
    <p class="eyebrow"><%= I18n.t(request, "account.title") %></p>
    <h2><%= I18n.t(request, "account.heading") %></h2>
    <p class="meta"><%= I18n.t(request, "dashboard.filiere") %>: <strong><%= HtmlUtil.escape(user.getFiliere()) %></strong> | <%= I18n.t(request, "dashboard.semestre") %>: <strong><%= HtmlUtil.escape(user.getSemestre()) %></strong></p>

    <% if (request.getParameter("updated") != null) { %>
      <div class="alert-success"><%= I18n.t(request, "account.updated") %></div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
      <div class="alert-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <form method="post" action="<%=request.getContextPath()%>/account" class="stack-md">
      <div class="grid form-grid">
        <div>
          <label><%= I18n.t(request, "auth.register.nom") %></label>
          <input class="input" type="text" name="nom" value="<%= HtmlUtil.escape(user.getNom()) %>" required />
        </div>
        <div>
          <label><%= I18n.t(request, "auth.register.prenom") %></label>
          <input class="input" type="text" name="prenom" value="<%= HtmlUtil.escape(user.getPrenom()) %>" required />
        </div>
      </div>

      <label><%= I18n.t(request, "auth.login.email") %></label>
      <input class="input" type="email" name="email" value="<%= HtmlUtil.escape(user.getEmail()) %>" required />

      <% if (isAdmin) { %>
        <label><%= I18n.t(request, "auth.register.filiere") %></label>
        <select name="filiere" required>
          <% if (filieres != null) {
               for (String f : filieres) { %>
            <option value="<%= HtmlUtil.escape(f) %>" <%= f.equals(user.getFiliere()) ? "selected" : "" %>><%= HtmlUtil.escape(f) %></option>
          <%   }
             } %>
        </select>
      <% } %>

      <label><%= I18n.t(request, "auth.register.semestre") %></label>
      <select name="semestre" required>
        <% if (semestres != null) {
             for (String s : semestres) { %>
          <option value="<%= HtmlUtil.escape(s) %>" <%= s.equals(user.getSemestre()) ? "selected" : "" %>><%= HtmlUtil.escape(s) %></option>
        <%   }
           } %>
      </select>

      <h3 class="section-title"><%= I18n.t(request, "account.password_section") %></h3>
      <label><%= I18n.t(request, "account.current_password") %></label>
      <input class="input" type="password" name="currentPassword" />

      <div class="grid form-grid">
        <div>
          <label><%= I18n.t(request, "reset.new_password") %></label>
          <input class="input" type="password" name="newPassword" />
        </div>
        <div>
          <label><%= I18n.t(request, "reset.confirm_password") %></label>
          <input class="input" type="password" name="confirmPassword" />
        </div>
      </div>

      <button type="submit" class="btn btn-primary"><%= I18n.t(request, "account.save") %></button>
    </form>

    <p class="meta page-link"><a href="<%=request.getContextPath()%>/dashboard"><%= I18n.t(request, "app.dashboard") %></a></p>
  </div>
</div>
</body>
</html>


