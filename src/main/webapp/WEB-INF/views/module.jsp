<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="ma.estagadir.forum.model.Module" %>
<%@ page import="ma.estagadir.forum.model.Question" %>
<%@ page import="ma.estagadir.forum.model.Answer" %>
<%@ page import="ma.estagadir.forum.util.HtmlUtil" %>
<%@ page import="ma.estagadir.forum.util.I18n" %>
<%@ page import="ma.estagadir.forum.util.DateTimeUtil" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    Module module = (Module) request.getAttribute("module");
    List<Question> questions = (List<Question>) request.getAttribute("questions");
    Map<Long, List<Answer>> answersByQuestion = (Map<Long, List<Answer>>) request.getAttribute("answersByQuestion");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title><%= I18n.t(request, "chat.module_discussion") %> - <%= HtmlUtil.escape(module.getTitle()) %></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/style.css" />
</head>
<body class="app-bg">
<div class="container">
  <div class="card header">
    <div>
      <h2><%= HtmlUtil.escape(module.getTitle()) %></h2>
      <p class="meta"><%= HtmlUtil.escape(module.getCode()) %> - <%= HtmlUtil.escape(module.getDescription()) %></p>
    </div>
    <div>
      <a href="<%=request.getContextPath()%>/dashboard"><%= I18n.t(request, "app.dashboard") %></a> |
      <a href="<%=request.getContextPath()%>/logout"><%= I18n.t(request, "app.logout") %></a>
    </div>
  </div>

  <div class="card">
    <h3><%= I18n.t(request, "chat.ask_question") %></h3>
    <form method="post" action="<%=request.getContextPath()%>/questions/create" class="stack-md">
      <input type="hidden" name="moduleId" value="<%= module.getId() %>" />
      <label><%= I18n.t(request, "blog.articleTitle") %></label>
      <input class="input" type="text" name="title" maxlength="200" required />
      <label><%= I18n.t(request, "blog.articleContent") %></label>
      <textarea name="content" rows="4" required></textarea>
      <button type="submit"><%= I18n.t(request, "chat.post") %></button>
    </form>
  </div>

  <div class="card">
    <h3><%= I18n.t(request, "chat.questions_answers") %></h3>

    <% if (questions == null || questions.isEmpty()) { %>
      <p class="meta"><%= I18n.t(request, "chat.no_questions") %></p>
    <% } else {
         for (Question question : questions) {
            List<Answer> answers = answersByQuestion == null ? null : answersByQuestion.get(question.getId());
    %>
      <div class="card" style="margin-bottom:12px;">
        <h4><%= HtmlUtil.escape(question.getTitle()) %></h4>
        <p class="meta">
          <%= I18n.t(request, "blog.by") %> <strong><%= HtmlUtil.escape(question.getAuthorName()) %></strong>
          - <%= DateTimeUtil.format(question.getCreatedAt()) %>
        </p>
        <p><%= HtmlUtil.escape(question.getContent()) %></p>

        <% if (answers != null) {
             for (Answer answer : answers) { %>
          <div class="answer">
            <p class="meta"><strong><%= HtmlUtil.escape(answer.getAuthorName()) %></strong> - <%= DateTimeUtil.format(answer.getCreatedAt()) %></p>
            <p><%= HtmlUtil.escape(answer.getContent()) %></p>
          </div>
        <%   }
           } %>

        <form method="post" action="<%=request.getContextPath()%>/answers/create" class="stack-md">
          <input type="hidden" name="moduleId" value="<%= module.getId() %>" />
          <input type="hidden" name="questionId" value="<%= question.getId() %>" />
          <label><%= I18n.t(request, "chat.reply") %></label>
          <textarea name="content" rows="3" required></textarea>
          <button type="submit"><%= I18n.t(request, "chat.send") %></button>
        </form>
      </div>
    <%   }
       }
    %>
  </div>
</div>
</body>
</html>


