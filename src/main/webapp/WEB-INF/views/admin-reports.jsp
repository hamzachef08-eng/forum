<%@ page import="java.util.List" %>
<%@ page import="ma.estagadir.forum.model.BlogReport" %>
<%@ page import="ma.estagadir.forum.model.MessageReport" %>
<%@ page import="ma.estagadir.forum.util.HtmlUtil" %>
<%@ page import="ma.estagadir.forum.util.I18n" %>
<%@ page import="ma.estagadir.forum.util.DateTimeUtil" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    List<MessageReport> reports = (List<MessageReport>) request.getAttribute("reports");
    List<BlogReport> blogReports = (List<BlogReport>) request.getAttribute("blogReports");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title><%= I18n.t(request, "admin.reports.title") %></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/style.css" />
<style>
  .reports-list .report-card {
    display: grid;
    grid-template-columns: 1fr auto;
    gap: 14px;
    align-items: end;
  }
  .report-main p { margin-bottom: 8px; }
  .report-side {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 10px;
    min-width: 190px;
  }
  .report-side .meta {
    text-align: right;
  }
  @media (max-width: 900px) {
    .reports-list .report-card {
      grid-template-columns: 1fr;
    }
    .report-side {
      align-items: flex-start;
      min-width: 0;
    }
    .report-side .meta {
      text-align: left;
    }
  }
</style>
</head>
<body class="app-bg">
<div class="container lg">
  <div class="card">
    <div class="option-bar">
      <div class="option-bar-left">
        <a class="btn btn-ghost" href="<%=request.getContextPath()%>/dashboard"><%= I18n.t(request, "app.dashboard") %></a>
        <a class="btn btn-ghost" href="<%=request.getContextPath()%>/admin/banned"><%= I18n.t(request, "admin.banned.title") %></a>
      </div>
      <div class="option-bar-right">
        <a class="btn btn-ghost" href="<%=request.getContextPath()%>/lang?lang=fr&back=/admin/reports">FR</a>
        <a class="btn btn-ghost" href="<%=request.getContextPath()%>/lang?lang=en&back=/admin/reports">EN</a>
      </div>
    </div>
  </div>

  <div class="card dash-hero">
    <div>
      <p class="eyebrow">Admin</p>
      <h2><%= I18n.t(request, "admin.reports.title") %></h2>
      <p class="meta"><%= I18n.t(request, "admin.reports.only") %></p>
    </div>
  </div>

  <div class="card">

    <% if (request.getParameter("done") != null) { %>
      <div class="alert-success"><%= I18n.t(request, "admin.reports.done") %></div>
    <% } %>

    <% if ((reports == null || reports.isEmpty()) && (blogReports == null || blogReports.isEmpty())) { %>
      <p class="meta"><%= I18n.t(request, "admin.reports.none") %></p>
    <% } else { %>

      <% if (reports != null && !reports.isEmpty()) { %>
      <h3><%= I18n.t(request, "admin.reports.title") %></h3>
      <div class="stack-md reports-list">
      <% for (MessageReport r : reports) { %>
        <div class="card inset report-card">
          <div class="report-main">
            <p><strong><%= I18n.t(request, "admin.reports.reported") %>:</strong> <%= HtmlUtil.escape(r.getReportedUserName()) %> | <strong><%= I18n.t(request, "admin.reports.by") %>:</strong> <%= HtmlUtil.escape(r.getReporterUserName()) %></p>
            <p><strong><%= I18n.t(request, "admin.reports.message") %>:</strong> <%= HtmlUtil.escape(r.getMessageContent()) %></p>
            <p><strong><%= I18n.t(request, "admin.reports.reason") %>:</strong> <%= HtmlUtil.escape(r.getReason()) %></p>
          </div>
          <div class="report-side">
            <p class="meta"><%= DateTimeUtil.format(r.getCreatedAt()) %></p>
            <form method="post" action="<%=request.getContextPath()%>/admin/reports">
              <input type="hidden" name="source" value="message" />
              <input type="hidden" name="reportId" value="<%= r.getId() %>" />
              <input type="hidden" name="reportedUserId" value="<%= r.getReportedUserId() %>" />
              <button type="submit" class="btn btn-primary"><%= I18n.t(request, "admin.reports.ban") %></button>
            </form>
          </div>
        </div>
      <% } %>
      </div>
      <% } %>

      <% if (blogReports != null && !blogReports.isEmpty()) { %>
      <h3 class="section-title"><%= I18n.t(request, "admin.reports.blog_title") %></h3>
      <div class="stack-md reports-list">
      <% for (BlogReport r : blogReports) { %>
        <div class="card inset report-card">
          <div class="report-main">
            <p><strong><%= I18n.t(request, "admin.reports.reported") %>:</strong> <%= HtmlUtil.escape(r.getReportedUserName()) %> | <strong><%= I18n.t(request, "admin.reports.by") %>:</strong> <%= HtmlUtil.escape(r.getReporterUserName()) %></p>
            <p><strong><%= I18n.t(request, "admin.reports.article") %>:</strong> <%= HtmlUtil.escape(r.getArticleTitle()) %></p>
            <p><strong><%= I18n.t(request, "admin.reports.message") %>:</strong> <%= HtmlUtil.escape(r.getArticleContent()) %></p>
            <p><strong><%= I18n.t(request, "admin.reports.reason") %>:</strong> <%= HtmlUtil.escape(r.getReason()) %></p>
          </div>
          <div class="report-side">
            <p class="meta"><%= DateTimeUtil.format(r.getCreatedAt()) %></p>
            <form method="post" action="<%=request.getContextPath()%>/admin/reports">
              <input type="hidden" name="source" value="blog" />
              <input type="hidden" name="reportId" value="<%= r.getId() %>" />
              <input type="hidden" name="reportedUserId" value="<%= r.getReportedUserId() %>" />
              <button type="submit" class="btn btn-primary"><%= I18n.t(request, "admin.reports.ban") %></button>
            </form>
          </div>
        </div>
      <% } %>
      </div>
      <% } %>

    <% } %>

    <p class="meta page-link"><a href="<%=request.getContextPath()%>/dashboard"><%= I18n.t(request, "app.dashboard") %></a></p>
  </div>
</div>
</body>
</html>
