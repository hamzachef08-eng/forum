<%@ page import="java.util.List" %>
<%@ page import="ma.estagadir.forum.model.Module" %>
<%@ page import="ma.estagadir.forum.model.ModuleMessage" %>
<%@ page import="ma.estagadir.forum.util.HtmlUtil" %>
<%@ page import="ma.estagadir.forum.util.I18n" %>
<%@ page import="ma.estagadir.forum.util.DateTimeUtil" %>
<%@ page import="java.time.LocalDate" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    Module module = (Module) request.getAttribute("module");
    List<ModuleMessage> messages = (List<ModuleMessage>) request.getAttribute("messages");
    List<Module> sidebarModules = (List<Module>) request.getAttribute("sidebarModules");
    Long currentUserId = (Long) session.getAttribute("currentUserId");
    boolean adminViewer = "ADMIN".equalsIgnoreCase(String.valueOf(session.getAttribute("currentUserRole")));
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title><%= I18n.t(request, "chat.title") + HtmlUtil.escape(module.getNomModule()) %></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/style.css?v=20260228-2" />
</head>
<body>
<div class="wa-wrap">
  <div class="wa-shell wa-shell-sidebar">
    <aside class="wa-sidebar">
      <div class="wa-sidebar-head">
        <h3><%= I18n.t(request, "chat.groups") %></h3>
        <a href="<%=request.getContextPath()%>/dashboard"><%= I18n.t(request, "app.back") %></a>
      </div>
      <div class="wa-group-list">
        <% if (sidebarModules != null) {
             for (Module m : sidebarModules) { %>
          <a class="wa-group-item <%= m.getId() == module.getId() ? "active" : "" %>"
             href="<%=request.getContextPath()%>/module/chat?id=<%=m.getId()%>">
            <span><%= HtmlUtil.escape(m.getNomModule()) %></span>
          </a>
        <%   }
           } %>
      </div>
    </aside>

    <section class="wa-main">
      <div class="wa-header">
        <div>
          <h2><%= HtmlUtil.escape(module.getNomModule()) %></h2>
          <p><%= I18n.t(request, "chat.module_discussion") %></p>
        </div>
      </div>

      <% if ("1".equals(request.getParameter("report"))) { %>
        <div class="alert-success" style="margin: 8px 12px 0;"><%= I18n.t(request, "chat.report_ok") %></div>
      <% } %>
      <% if ("own".equals(request.getParameter("report"))) { %>
        <div class="alert-error" style="margin: 8px 12px 0;"><%= I18n.t(request, "chat.report_own") %></div>
      <% } %>
      <% if ("admin".equals(request.getParameter("report"))) { %>
        <div class="alert-error" style="margin: 8px 12px 0;"><%= I18n.t(request, "chat.report_admin") %></div>
      <% } %>
      <% if ("1".equals(request.getParameter("ban"))) { %>
        <div class="alert-success" style="margin: 8px 12px 0;"><%= I18n.t(request, "chat.ban_ok") %></div>
      <% } %>

      <div class="wa-chat" id="waChat">
        <% if (messages == null || messages.isEmpty()) { %>
          <div class="wa-empty"><%= I18n.t(request, "chat.no_messages") %></div>
        <% } else {
             LocalDate lastRenderedDate = null;
             for (ModuleMessage msg : messages) {
               LocalDate currentDate = msg.getCreatedAt() == null ? null : msg.getCreatedAt().toLocalDate();
               String shownContent = msg.isUserBanned() ? I18n.t(request, "chat.ghosted") : msg.getContent();
               String shownParentContent = msg.isParentUserBanned()
                       ? I18n.t(request, "chat.ghosted")
                       : (msg.getParentContent() == null ? I18n.t(request, "chat.deleted") : msg.getParentContent());
               if (currentDate != null && (lastRenderedDate == null || !currentDate.equals(lastRenderedDate))) {
        %>
          <div class="wa-day-separator">
            <span class="wa-day-pill"><%= DateTimeUtil.formatDate(msg.getCreatedAt()) %></span>
          </div>
        <%
                 lastRenderedDate = currentDate;
               }
               boolean mine = currentUserId != null && msg.getUserId() == currentUserId.longValue();
        %>
          <div class="wa-row <%= mine ? "mine" : "theirs" %>">
            <div class="wa-bubble" id="msg-<%= msg.getId() %>">
              <% if (msg.getParentMessageId() != null) { %>
                <div class="wa-reply-preview">
                  <div class="wa-reply-author">
                    <%= "ADMIN".equalsIgnoreCase(String.valueOf(msg.getParentUserRole()))
                          ? "ADMIN"
                          : HtmlUtil.escape(msg.getParentUserName() == null ? I18n.t(request, "chat.unknown") : msg.getParentUserName()) %>
                  </div>
                  <div class="wa-reply-text <%= msg.isParentUserBanned() ? "ghosted-text" : "" %>"><%= HtmlUtil.escape(shownParentContent) %></div>
                </div>
              <% } %>
              <div class="wa-author">
                <%= "ADMIN".equalsIgnoreCase(String.valueOf(msg.getUserRole())) ? "ADMIN" : HtmlUtil.escape(msg.getUserName()) %>
              </div>
              <div class="wa-text <%= msg.isUserBanned() ? "ghosted-text" : "" %>"><%= HtmlUtil.escape(shownContent) %></div>
              <div class="wa-time"><%= DateTimeUtil.formatTime(msg.getCreatedAt()) %></div>
              <div class="wa-actions">
                <button type="button"
                        class="wa-reply-btn"
                        data-msg-id="<%= msg.getId() %>"
                        data-msg-user="<%= "ADMIN".equalsIgnoreCase(String.valueOf(msg.getUserRole())) ? "ADMIN" : HtmlUtil.escape(msg.getUserName()) %>"
                        data-msg-content="<%= HtmlUtil.escape(shownContent) %>"><%= I18n.t(request, "chat.reply") %></button>

                <% if (!mine) { %>
                  <% boolean messageFromAdmin = "ADMIN".equalsIgnoreCase(String.valueOf(msg.getUserRole())); %>
                  <% if (adminViewer && !messageFromAdmin && !msg.isUserBanned()) { %>
                    <form method="post" action="<%=request.getContextPath()%>/module/report" style="display:inline-block;">
                      <input type="hidden" name="moduleId" value="<%= module.getId() %>" />
                      <input type="hidden" name="messageId" value="<%= msg.getId() %>" />
                      <input type="hidden" name="action" value="ban" />
                      <button type="submit" class="wa-report-btn"><%= I18n.t(request, "admin.reports.ban") %></button>
                    </form>
                  <% } else if (!adminViewer && !messageFromAdmin) { %>
                    <form method="post" action="<%=request.getContextPath()%>/module/report" style="display:inline-block;">
                      <input type="hidden" name="moduleId" value="<%= module.getId() %>" />
                      <input type="hidden" name="messageId" value="<%= msg.getId() %>" />
                      <input type="hidden" name="reason" value="<%= I18n.t(request, "chat.report_reason") %>" />
                      <button type="submit" class="wa-report-btn"><%= I18n.t(request, "chat.report") %></button>
                    </form>
                  <% } %>
                <% } %>
              </div>
            </div>
          </div>
        <%   }
           } %>
      </div>

      <form class="wa-composer" method="post" action="<%=request.getContextPath()%>/module/message" id="composerForm">
        <input type="hidden" name="moduleId" value="<%= module.getId() %>" />
        <input type="hidden" name="replyToMessageId" id="replyToMessageId" value="" />

        <div class="wa-composer-main">
          <div class="wa-composer-reply" id="composerReply" style="display:none;">
            <div>
              <div class="wa-reply-author" id="replyUser"></div>
              <div class="wa-reply-text" id="replyContent"></div>
            </div>
            <button type="button" class="wa-reply-cancel" id="cancelReply">x</button>
          </div>
          <textarea name="content" rows="1" placeholder="<%= I18n.t(request, "chat.placeholder") %>" required></textarea>
        </div>
        <button type="submit" class="btn btn-primary"><%= I18n.t(request, "chat.send") %></button>
      </form>
    </section>
  </div>
</div>

<script>
  (function() {
    const box = document.getElementById('waChat');
    const replyInput = document.getElementById('replyToMessageId');
    const replyBox = document.getElementById('composerReply');
    const replyUser = document.getElementById('replyUser');
    const replyContent = document.getElementById('replyContent');
    const cancelReply = document.getElementById('cancelReply');

    if (box) {
      box.scrollTop = box.scrollHeight;
    }

    document.querySelectorAll('.wa-reply-btn').forEach(btn => {
      btn.addEventListener('click', function() {
        replyInput.value = this.dataset.msgId;
        replyUser.textContent = this.dataset.msgUser || '';
        replyContent.textContent = this.dataset.msgContent || '';
        replyBox.style.display = 'flex';
      });
    });

    if (cancelReply) {
      cancelReply.addEventListener('click', function() {
        replyInput.value = '';
        replyUser.textContent = '';
        replyContent.textContent = '';
        replyBox.style.display = 'none';
      });
    }
  })();
</script>
</body>
</html>


