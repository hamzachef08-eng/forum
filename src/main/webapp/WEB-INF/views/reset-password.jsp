<%@ page import="ma.estagadir.forum.util.I18n" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%!
  private String js(String value) {
    if (value == null) return "";
    return value.replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "\\n");
  }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title><%= I18n.t(request, "reset.title") %></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/style.css?v=20260301-2" />
</head>
<body class="app-bg">
<div class="container page-wide">
  <div class="reset-layout">
    <aside class="card reset-side">
      <p class="eyebrow">SECURE</p>
      <h3><%= I18n.t(request, "reset.title") %></h3>
      <p class="meta"><%= I18n.t(request, "reset.hint") %></p>
      <ol class="reset-steps">
        <li><strong>1.</strong> <%= I18n.t(request, "forgot.gmail") %></li>
        <li><strong>2.</strong> <%= I18n.t(request, "reset.code_label") %></li>
        <li><strong>3.</strong> <%= I18n.t(request, "reset.new_password") %></li>
      </ol>
      <p class="meta"><a href="<%=request.getContextPath()%>/login"><%= I18n.t(request, "forgot.back_login") %></a></p>
    </aside>

    <section class="card reset-main">
      <h2><%= I18n.t(request, "reset.title") %></h2>
      <p class="meta"><%= I18n.t(request, "reset.hint") %></p>

      <% if (request.getParameter("sent") != null) { %>
        <div class="alert-success"><%= I18n.t(request, "reset.code_sent") %></div>
      <% } %>
      <% if (request.getParameter("wait") != null) { %>
        <div class="alert-error"><%= I18n.t(request, "reset.wait", request.getParameter("wait")) %></div>
      <% } %>
      <% if (request.getAttribute("error") != null) { %>
        <div class="alert-error"><%= request.getAttribute("error") %></div>
      <% } %>

      <form method="post" action="<%=request.getContextPath()%>/reset-password" class="stack-md" id="resetForm">
        <label><%= I18n.t(request, "forgot.gmail") %></label>
        <input class="input" type="email" name="email" id="emailField" pattern="^[^\s@]+@gmail\.com$" value="<%= request.getParameter("email") == null ? "" : request.getParameter("email") %>" required />

        <label><%= I18n.t(request, "reset.code_label") %></label>
        <input class="input" type="text" name="code" id="codeField" inputmode="numeric" pattern="[0-9]{6}" maxlength="6" autocomplete="one-time-code" required />
        <p class="meta reset-status" id="verifyStatus"></p>

        <div id="passwordBlock" class="password-slide">
          <label><%= I18n.t(request, "reset.new_password") %></label>
          <input class="input" type="password" name="newPassword" id="newPassword" />

          <label><%= I18n.t(request, "reset.confirm_password") %></label>
          <input class="input" type="password" name="confirmPassword" id="confirmPassword" />

          <button type="submit" class="btn btn-primary btn-block"><%= I18n.t(request, "reset.submit") %></button>
        </div>
      </form>

      <form method="post" action="<%=request.getContextPath()%>/forgot-password" class="stack-md top-gap">
        <input type="hidden" name="email" value="<%= request.getParameter("email") == null ? "" : request.getParameter("email") %>" />
        <button type="submit" class="btn btn-ghost"><%= I18n.t(request, "reset.resend") %></button>
      </form>
    </section>
  </div>
</div>

<script>
  (function () {
    const resetForm = document.getElementById('resetForm');
    const codeField = document.getElementById('codeField');
    const emailField = document.getElementById('emailField');
    const verifyStatus = document.getElementById('verifyStatus');
    const passwordBlock = document.getElementById('passwordBlock');
    const newPassword = document.getElementById('newPassword');
    const confirmPassword = document.getElementById('confirmPassword');

    const txtVerifying = '<%= js(I18n.t(request, "reset.verifying")) %>';
    const txtOk = '<%= js(I18n.t(request, "reset.code_ok")) %>';
    const txtBad = '<%= js(I18n.t(request, "reset.code_bad")) %>';
    const txtFail = '<%= js(I18n.t(request, "reset.verify_fail")) %>';
    const txtGmailRule = '<%= js(I18n.t(request, "reset.gmail_rule")) %>';

    let verifyTimer = null;
    let codeVerified = false;

    function setPasswordVisible(visible) {
      if (visible) {
        passwordBlock.classList.add('show');
        newPassword.required = true;
        confirmPassword.required = true;
      } else {
        passwordBlock.classList.remove('show');
        newPassword.required = false;
        confirmPassword.required = false;
      }
    }

    function setVerifiedState(verified, message) {
      codeVerified = verified;
      setPasswordVisible(verified);
      verifyStatus.textContent = message || '';
    }

    async function verifyCodeWithServer(email, code) {
      const params = new URLSearchParams({ email, code });
      const url = '<%=request.getContextPath()%>/reset-password/verify-code?' + params.toString();
      const res = await fetch(url, { method: 'GET' });
      if (!res.ok) return false;
      const data = await res.json();
      return !!data.valid;
    }

    function queueVerify() {
      const code = (codeField.value || '').replace(/\D/g, '').slice(0, 6);
      codeField.value = code;
      const email = (emailField.value || '').trim().toLowerCase();
      setVerifiedState(false, '');

      if (!/^[^\s@]+@gmail\.com$/.test(email)) {
        setVerifiedState(false, txtGmailRule);
        return;
      }

      if (!/^\d{6}$/.test(code)) {
        return;
      }

      verifyStatus.textContent = txtVerifying;
      clearTimeout(verifyTimer);
      verifyTimer = setTimeout(async function () {
        try {
          const valid = await verifyCodeWithServer(email, code);
          if (valid) {
            setVerifiedState(true, txtOk);
          } else {
            setVerifiedState(false, txtBad);
          }
        } catch (e) {
          setVerifiedState(false, txtFail);
        }
      }, 200);
    }

    codeField.addEventListener('input', queueVerify);
    emailField.addEventListener('input', queueVerify);
    resetForm.addEventListener('submit', function (e) {
      const code = (codeField.value || '').replace(/\D/g, '').slice(0, 6);
      codeField.value = code;
      if (!/^\d{6}$/.test(code) || !codeVerified) {
        setVerifiedState(false, txtBad);
        codeField.focus();
        e.preventDefault();
      }
    });
    codeField.focus();
    queueVerify();
  })();
</script>
</body>
</html>


