<%@ page import="java.util.List" %>
<%@ page import="ma.estagadir.forum.util.HtmlUtil" %>
<%@ page import="ma.estagadir.forum.util.I18n" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    List<String> filieres = (List<String>) request.getAttribute("filieres");
    List<String> semestres = (List<String>) request.getAttribute("semestres");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title><%= I18n.t(request, "auth.register.title") %></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/style.css" />
</head>
<body class="app-bg">
<div class="container lg">
  <div class="card glass">
    <p class="eyebrow"><%= I18n.t(request, "auth.register.eyebrow") %></p>
    <h2><%= I18n.t(request, "auth.register.heading") %></h2>

    <% if (request.getAttribute("error") != null) { %>
      <div class="alert-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <form method="post" action="<%=request.getContextPath()%>/register" id="registerForm" class="stack-lg">
      <div class="grid form-grid">
        <div>
          <label><%= I18n.t(request, "auth.register.nom") %></label>
          <input class="input" type="text" name="nom" required />
        </div>
        <div>
          <label><%= I18n.t(request, "auth.register.prenom") %></label>
          <input class="input" type="text" name="prenom" required />
        </div>
        <div>
          <label><%= I18n.t(request, "auth.login.email") %></label>
          <input class="input" type="email" name="email" required />
        </div>
        <div>
          <label><%= I18n.t(request, "auth.register.password") %></label>
          <input class="input" type="password" name="password" required />
        </div>
        <div>
          <label><%= I18n.t(request, "auth.register.filiere") %></label>
          <select name="filiere" id="filiere" required>
            <option value=""><%= I18n.t(request, "auth.register.choose") %></option>
            <% for (String f : filieres) { %>
              <option value="<%= HtmlUtil.escape(f) %>"><%= HtmlUtil.escape(f) %></option>
            <% } %>
          </select>
        </div>
        <div>
          <label><%= I18n.t(request, "auth.register.semestre") %></label>
          <select name="semestre" id="semestre" required>
            <option value=""><%= I18n.t(request, "auth.register.choose") %></option>
            <% for (String s : semestres) { %>
              <option value="<%= HtmlUtil.escape(s) %>"><%= HtmlUtil.escape(s) %></option>
            <% } %>
          </select>
        </div>
      </div>

      <button type="submit" class="btn btn-primary"><%= I18n.t(request, "auth.register.submit") %></button>
    </form>

    <div class="card inset">
      <h3><%= I18n.t(request, "auth.register.preview") %></h3>
      <ul id="modulePreview" class="module-list preview-list">
        <li class="meta"><%= I18n.t(request, "auth.register.select_hint") %></li>
      </ul>
    </div>

    <p class="meta"><%= I18n.t(request, "auth.register.already") %> <a href="<%=request.getContextPath()%>/login"><%= I18n.t(request, "auth.login.heading") %></a></p>
  </div>
</div>

<script>
(function() {
  const filiereSelect = document.getElementById('filiere');
  const semestreSelect = document.getElementById('semestre');
  const modulePreview = document.getElementById('modulePreview');

  const hintText = '<%= I18n.t(request, "auth.register.select_hint") %>';
  const noModuleText = '<%= I18n.t(request, "auth.register.no_module") %>';
  const loadErrorText = '<%= I18n.t(request, "auth.register.load_error") %>';

  async function loadModules() {
    const filiere = filiereSelect.value;
    const semestre = semestreSelect.value;

    if (!filiere || !semestre) {
      modulePreview.innerHTML = '<li class="meta">' + hintText + '</li>';
      return;
    }

    const params = new URLSearchParams({ filiere, semestre });
    const url = '<%=request.getContextPath()%>/modules/options?' + params.toString();

    try {
      const res = await fetch(url);
      const data = await res.json();
      if (!Array.isArray(data) || data.length === 0) {
        modulePreview.innerHTML = '<li class="meta">' + noModuleText + '</li>';
        return;
      }
      modulePreview.innerHTML = data.map(m => '<li>' + m + '</li>').join('');
    } catch (e) {
      modulePreview.innerHTML = '<li class="meta">' + loadErrorText + '</li>';
    }
  }

  filiereSelect.addEventListener('change', loadModules);
  semestreSelect.addEventListener('change', loadModules);
})();
</script>
</body>
</html>


