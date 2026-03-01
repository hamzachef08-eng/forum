package ma.estagadir.forum.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletContext;

public final class MailUtil {
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    private MailUtil() {
    }

    public static void sendResetCode(ServletContext ctx, String toEmail, String code) throws MessagingException {
        sendTransactionalMail(ctx, toEmail, "EST Agadir - Password Reset Code",
                "Your verification code is: " + code + "\nThis code expires in 10 minutes.\nResend allowed every 60 seconds.");
    }

    public static void sendVerificationEmail(ServletContext ctx, String toEmail, String verifyLink) throws MessagingException {
        String text = "Bienvenue sur EST Agadir Forum.\n\n"
                + "Cliquez pour verifier votre compte:\n" + verifyLink + "\n\n"
                + "Ce lien expire dans 24 heures.";
        sendTransactionalMail(ctx, toEmail, "EST Agadir - Verification de compte", text);
    }

    public static void sendVerificationCode(ServletContext ctx, String toEmail, String code) throws MessagingException {
        String text = "Bienvenue sur EST Agadir Forum.\n\n"
                + "Votre code de verification est: " + code + "\n"
                + "Ce code expire dans 10 minutes.\n"
                + "Vous pouvez renvoyer le code apres 60 secondes.";
        sendTransactionalMail(ctx, toEmail, "EST Agadir - Code de verification", text);
    }

    private static void sendTransactionalMail(ServletContext ctx, String toEmail, String subject, String text)
            throws MessagingException {
        String brevoApiKey = firstNonBlank(
                getConfig(ctx, "BREVO_API_KEY", "mail.brevo.api.key"),
                getConfig(ctx, "MAIL_BREVO_API_KEY", "mail.brevo.api.key"));
        String from = resolveFromAddress(ctx);
        if (!isBlank(brevoApiKey)) {
            sendViaBrevoApi(from, toEmail, subject, text, brevoApiKey);
            return;
        }

        Session session = buildSession(ctx);
        sendTextMail(session, from, toEmail, subject, text);
    }

    private static Session buildSession(ServletContext ctx) throws MessagingException {
        String host = getConfig(ctx, "MAIL_SMTP_HOST", "mail.smtp.host");
        String port = getConfig(ctx, "MAIL_SMTP_PORT", "mail.smtp.port");
        String username = getConfig(ctx, "MAIL_SMTP_USERNAME", "mail.smtp.username");
        String password = getConfig(ctx, "MAIL_SMTP_PASSWORD", "mail.smtp.password");

        if (isBlank(host) || isBlank(port) || isBlank(username) || isBlank(password)) {
            throw new MessagingException("Mail SMTP is not configured in web.xml context params.");
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.ssl.trust", host);
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        final String smtpUser = username.trim();
        final String smtpPassword = password.replace(" ", "");
        return Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUser, smtpPassword);
            }
        });
    }

    private static void sendTextMail(Session session, String from, String toEmail, String subject, String text)
            throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setText(text);

        Transport.send(message);
    }

    private static void sendViaBrevoApi(String from, String toEmail, String subject, String text, String apiKey)
            throws MessagingException {
        if (isBlank(from) || !from.contains("@")) {
            throw new MessagingException("MAIL_FROM is invalid for Brevo API.");
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL(BREVO_API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("api-key", apiKey.trim());
            connection.setRequestProperty("content-type", "application/json");

            String payload = "{"
                    + "\"sender\":{\"email\":\"" + jsonEscape(from.trim()) + "\"},"
                    + "\"to\":[{\"email\":\"" + jsonEscape(toEmail.trim()) + "\"}],"
                    + "\"subject\":\"" + jsonEscape(subject) + "\","
                    + "\"textContent\":\"" + jsonEscape(text) + "\""
                    + "}";

            try (OutputStream out = connection.getOutputStream()) {
                out.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            int status = connection.getResponseCode();
            if (status < 200 || status >= 300) {
                throw new MessagingException("Brevo API send failed with HTTP status " + status);
            }
        } catch (IOException e) {
            throw new MessagingException("Unable to send mail via Brevo API", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String jsonEscape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String firstNonBlank(String preferred, String fallback) {
        if (!isBlank(preferred)) {
            return preferred;
        }
        return fallback;
    }

    private static String resolveFromAddress(ServletContext ctx) {
        String from = getConfig(ctx, "MAIL_FROM", "mail.from");
        if (!isBlank(from) && from.contains("@")) {
            return from.trim();
        }
        String username = getConfig(ctx, "MAIL_SMTP_USERNAME", "mail.smtp.username");
        return isBlank(username) ? from : username.trim();
    }

    private static String getConfig(ServletContext ctx, String envName, String paramName) {
        String envValue = System.getenv(envName);
        if (!isBlank(envValue)) {
            return envValue.trim();
        }
        String init = ctx.getInitParameter(paramName);
        return init == null ? null : init.trim();
    }
}
