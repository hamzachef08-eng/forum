# EST Agadir Learning Forum (Jakarta EE)

## Logical Architecture Diagram

```text
Client Browser
   |
   v
Servlet Controllers (Auth, Dashboard, Module Chat, Blog, Article, Verify Email, Logout)
   |
   v
DAO Layer (JDBC)
   |
   v
MySQL Database (users, modules, module_messages, articles, article_comments, password_reset_codes, email_verification_tokens)
   |
   v
JSP Views (login, register, dashboard, module discussion)
```

### MVC Mapping
- Model: `User`, `Module`, `Question`, `Answer`
- View: JSP files in `WEB-INF/views`
- Controller: Servlets in `ma.estagadir.forum.web`

## Main Pages
- `/login`: user login
- `/register`: account creation (Student/Teacher)
- `/dashboard`: module groups list (Algorithms, Databases, Web Development, AI, Networks)
- `/modules?id={moduleId}`: module-specific chat/discussion
- `/blog`: article listing + article creation
- `/article?id={articleId}`: article comments page
- `/verify-email?u={userId}&token={token}`: registration email validation

## Main Servlets and Responsibilities
- `AuthServlet`: login validation and session creation
- `RegisterServlet`: new user registration
- `LogoutServlet`: session invalidation
- `DashboardServlet`: load all modules
- `ModuleServlet`: load one module with questions and answers
- `QuestionServlet`: create a question in selected module
- `AnswerServlet`: create answer for a question
- `AuthFilter`: protects authenticated endpoints
- `AppContextListener`: initializes JDBC config from `web.xml`

## Example URL Request Flow
1. `GET /login`
2. `POST /login` -> if valid: set session + redirect `/dashboard`
3. `GET /dashboard` -> show module cards
4. `GET /modules?id=1` -> show module discussions
5. `POST /questions/create` -> redirect to `/modules?id=1`
6. `POST /answers/create` -> redirect to `/modules?id=1`
7. `GET /logout` -> session destroyed -> `/login`

## Session Management
Session keys:
- `currentUserId`
- `currentUserName`
- `currentUserRole`

`AuthFilter` ensures unauthenticated users cannot access dashboard/module/chat actions.

## Database Schema
See [sql/init.sql](../sql/init.sql).

## Eclipse/Tomcat Setup
1. Use Tomcat 10+ (Jakarta namespace).
2. Add MySQL Connector/J jar to `WEB-INF/lib`.
3. Run `sql/init.sql` in MySQL.
4. Update DB credentials in `src/main/webapp/WEB-INF/web.xml` context params.
5. Deploy and start the application.
