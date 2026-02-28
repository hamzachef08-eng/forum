# EST Agadir Forum - Jakarta EE Project

This project is a complete university e-learning discussion platform with:
- Authentication (login/register/logout)
- Roles (STUDENT, TEACHER)
- Dashboard of module groups
- Module-based Q&A chat (questions + multiple answers)
- Member blog: article publishing + comments
- Email verification during registration
- FR/EN language toggle
- Session management
- Jakarta Servlet + JSP + JDBC (MVC)

## Quick Start
1. Create database/tables and seed modules:
   - Fresh install: execute `sql/setup_forum_user.sql` (or `sql/init.sql`).
   - Existing install: execute `sql/migration_add_blog_and_verification.sql`.
2. Configure DB parameters in `src/main/webapp/WEB-INF/web.xml`.
3. Add MySQL JDBC driver (`mysql-connector-j`) to `src/main/webapp/WEB-INF/lib`.
4. Deploy on Tomcat 10+.
5. Open `/forum` and register users.

Detailed design and flow: `docs/design.md`.

## Free Hosting Recommendation
For free hosting of this Jakarta EE app, use `Render` or `Railway` for quick deployments with managed MySQL alternatives.
If you need classic Java EE/Tomcat hosting with database persistence for student projects, `HelioHost` is also a common free option.
