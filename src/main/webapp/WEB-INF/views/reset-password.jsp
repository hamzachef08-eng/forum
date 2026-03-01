<%@ page import="ma.estagadir.forum.util.I18n" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
        <div class="otp-row otp-row-reset">
          <input type="text" inputmode="numeric" maxlength="1" class="otp-box" autocomplete="one-time-code" autofocus />
          <input type="text" inputmode="numeric" maxlength="1" class="otp-box" />
          <input type="text" inputmode="numeric" maxlength="1" class="otp-box" />
          <input type="text" inputmode="numeric" maxlength="1" class="otp-box" />
          <input type="text" inputmode="numeric" maxlength="1" class="otp-box" />
          <input type="text" inputmode="numeric" maxlength="1" class="otp-box" />
        </div>
        <input type="hidden" name="code" id="fullCode" required />
        <p class="meta reset-status" id="verifyStatus"></p>

        <div id="passwordBlock" class="password-slide show">
          <label><%= I18n.t(request, "reset.new_password") %></label>
          <input class="input" type="password" name="newPassword" id="newPassword" required />

          <label><%= I18n.t(request, "reset.confirm_password") %></label>
          <input class="input" type="password" name="confirmPassword" id="confirmPassword" required />

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
    const boxes = Array.from(document.querySelectorAll('.otp-box'));
    const resetForm = document.getElementById('resetForm');
    const fullCode = document.getElementById('fullCode');
    const otpRow = document.querySelector('.otp-row-reset');
    const emailField = document.getElementById('emailField');
    const verifyStatus = document.getElementById('verifyStatus');

    const txtVerifying = '<%= I18n.t(request, "reset.verifying") %>';
    const txtOk = '<%= I18n.t(request, "reset.code_ok") %>';
    const txtBad = '<%= I18n.t(request, "reset.code_bad") %>';
    const txtFail = '<%= I18n.t(request, "reset.verify_fail") %>';
    const txtGmailRule = '<%= I18n.t(request, "reset.gmail_rule") %>';

    let verifyTimer = null;

    async function verifyCodeWithServer(email, code) {
      const params = new URLSearchParams({ email, code });
      const url = '<%=request.getContextPath()%>/reset-password/verify-code?' + params.toString();
      const res = await fetch(url, { method: 'GET' });
      if (!res.ok) return false;
      const data = await res.json();
      return !!data.valid;
    }

    function queueVerify() {
      const code = boxes.map(b => b.value).join('');
      fullCode.value = code;
      const email = (emailField.value || '').trim().toLowerCase();
      verifyStatus.textContent = '';

      if (!/^[^\s@]+@gmail\.com$/.test(email)) {
        verifyStatus.textContent = txtGmailRule;
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
            verifyStatus.textContent = txtOk;
          } else {
            verifyStatus.textContent = txtBad;
          }
        } catch (e) {
          verifyStatus.textContent = txtFail;
        }
      }, 200);
    }

    function focusFirstEmpty() {
      const empty = boxes.find(b => !b.value);
      (empty || boxes[boxes.length - 1]).focus();
    }

    boxes.forEach((box, index) => {
      box.addEventListener('input', function () {
        this.value = (this.value || '').replace(/\D/g, '').slice(0, 1);
        if (this.value && index < boxes.length - 1) {
          boxes[index + 1].focus();
        }
        queueVerify();
      });

      box.addEventListener('keydown', function (e) {
        if (/^\d$/.test(e.key)) {
          this.value = e.key;
          if (index < boxes.length - 1) {
            boxes[index + 1].focus();
          }
          queueVerify();
          e.preventDefault();
          return;
        }
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
        queueVerify();
      });
    });

    otpRow.addEventListener('click', function () {
      focusFirstEmpty();
    });

    emailField.addEventListener('input', queueVerify);
    resetForm.addEventListener('submit', function (e) {
      const code = boxes.map(b => b.value).join('');
      fullCode.value = code;
      if (!/^\d{6}$/.test(code)) {
        verifyStatus.textContent = txtBad;
        focusFirstEmpty();
        e.preventDefault();
      }
    });
    focusFirstEmpty();
    queueVerify();
  })();
</script>
</body>
</html>


