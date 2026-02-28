# Deploy On Render With PostgreSQL

## 1) Push latest code

Your repo should include:

- `Dockerfile`
- `sql/init_postgres.sql`

## 2) Create PostgreSQL on Render

1. Render dashboard -> **New** -> **PostgreSQL**
2. Name: `forum-db`
3. Plan: free (or starter if free unavailable)
4. Create database

After creation, copy:

- Host
- Port
- Database
- User
- Password
- External Database URL (optional)

## 3) Initialize schema/data

Run your SQL file `sql/init_postgres.sql` against the Render PostgreSQL.

Use `psql` locally:

```bash
psql "postgresql://USER:PASSWORD@HOST:PORT/DB?sslmode=require" -f sql/init_postgres.sql
```

## 4) Create Web Service on Render

1. Render dashboard -> **New** -> **Web Service**
2. Connect repo: `hamzachef08-eng/forum`
3. Environment: **Docker**
4. Instance type: free (or starter if free unavailable)
5. Create Web Service

## 5) Set environment variables in Render (Web Service)

Set these exactly:

- `DB_DRIVER=org.postgresql.Driver`
- `DB_URL=jdbc:postgresql://HOST:PORT/DB?sslmode=require`
- `DB_USER=USER`
- `DB_PASSWORD=PASSWORD`

For Gmail verification/reset emails:

- `MAIL_SMTP_HOST=smtp.gmail.com`
- `MAIL_SMTP_PORT=587`
- `MAIL_SMTP_USERNAME=<your_gmail>`
- `MAIL_SMTP_PASSWORD=<your_gmail_app_password>`
- `MAIL_FROM=<your_gmail>`

## 6) Deploy

Click **Manual Deploy** -> **Deploy latest commit**.

## 7) Open app

Render URL:

`https://<your-service>.onrender.com/forum`

## Notes

- First request may be slow on free tier (cold start).
- Keep secrets only in Render env vars, not in `web.xml`.
