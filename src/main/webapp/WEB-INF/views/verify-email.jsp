<%@ page import="ma.estagadir.forum.util.I18n" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title><%= I18n.t(request, "verify.code_title") %></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/style.css?v=20260301-1" />
</head>
<body class="app-bg">
<div class="container page-medium">
  <div class="card">
    <h2><%= I18n.t(request, "verify.code_title") %></h2>
    <p class="meta"><%= I18n.t(request, "verify.code_hint") %></p>

    <% if ("1".equals(request.getParameter("sent"))) { %>
      <div class="alert-success"><%= I18n.t(request, "verify.code_sent") %></div>
    <% } %>
    <% if (request.getParameter("wait") != null) { %>
      <div class="alert-error"><%= I18n.t(request, "verify.code_wait", request.getParameter("wait")) %></div>
    <% } %>
    <% if ("1".equals(request.getParameter("bad"))) { %>
      <div class="alert-error"><%= I18n.t(request, "verify.code_bad") %></div>
    <% } %>
    <% if ("1".equals(request.getParameter("smtp"))) { %>
      <div class="alert-error"><%= I18n.t(request, "err.smtp") %></div>
    <% } %>

    <form method="post" action="<%=request.getContextPath()%>/verify-email" class="stack-md">
      <label><%= I18n.t(request, "forgot.gmail") %></label>
      <input class="input" type="email" name="email" id="emailField" pattern="^[^\s@]+@gmail\.com$"
             value="<%= request.getParameter("email") == null ? "" : request.getParameter("email") %>" required />

      <label><%= I18n.t(request, "verify.code_label") %></label>
      <div class="otp-row">
        <input type="text" inputmode="numeric" maxlength="1" class="otp-box" />
        <input type="text" inputmode="numeric" maxlength="1" class="otp-box" />
        <input type="text" inputmode="numeric" maxlength="1" class="otp-box" />
        <input type="text" inputmode="numeric" maxlength="1" class="otp-box" />
        <input type="text" inputmode="numeric" maxlength="1" class="otp-box" />
        <input type="text" inputmode="numeric" maxlength="1" class="otp-box" />
      </div>
      <input type="hidden" name="code" id="fullCode" required />

      <button type="submit" class="btn btn-primary"><%= I18n.t(request, "verify.code_submit") %></button>
    </form>

    <form method="post" action="<%=request.getContextPath()%>/verify-email" class="stack-md top-gap">
      <input type="hidden" name="action" value="resend" />
      <input type="hidden" name="email" value="<%= request.getParameter("email") == null ? "" : request.getParameter("email") %>" />
      <button type="submit" class="btn btn-ghost"><%= I18n.t(request, "verify.code_resend") %></button>
    </form>

    <p class="meta top-gap"><a href="<%=request.getContextPath()%>/login"><%= I18n.t(request, "app.back") %></a></p>
  </div>
</div>

<script>
  (function () {
    const boxes = Array.from(document.querySelectorAll('.otp-box'));
    const fullCode = document.getElementById('fullCode');

    function updateCode() {
      fullCode.value = boxes.map(b => b.value).join('');
    }

    boxes.forEach((box, index) => {
      box.addEventListener('input', function () {
        this.value = (this.value || '').replace(/\D/g, '').slice(0, 1);
        if (this.value && index < boxes.length - 1) {
          boxes[index + 1].focus();
        }
        updateCode();
      });

      box.addEventListener('keydown', function (e) {
        if (e.key === 'Backspace' && !this.value && index > 0) {
          boxes[index - 1].focus();
        }
      });

      box.addEventListener('paste', function (e) {
        e.preventDefault();
        const pasted = (e.clipboardData.getData('text') || '').replace(/\D/g, '').slice(0, 6);
        if (!pasted) return;
        pasted.split('').forEach((ch, i) => {
          if (boxes[i]) boxes[i].value = ch;
        });
        boxes[Math.min(pasted.length, boxes.length - 1)].focus();
        updateCode();
      });
    });

    updateCode();
  })();
</script>
</body>
</html>
