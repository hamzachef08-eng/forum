package ma.estagadir.forum.util;

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
    private MailUtil() {
    }

    public static void sendResetCode(ServletContext ctx, String toEmail, String code) throws MessagingException {
        Session session = buildSession(ctx);
        String from = ctx.getInitParameter("mail.from");
        sendTextMail(session, from, toEmail, "EST Agadir - Password Reset Code",
                "Your verification code is: " + code + "\nThis code expires in 10 minutes.\nResend allowed every 60 seconds.");
    }

    public static void sendVerificationEmail(ServletContext ctx, String toEmail, String verifyLink) throws MessagingException {
        Session session = buildSession(ctx);
        String from = ctx.getInitParameter("mail.from");
        String text = "Bienvenue sur EST Agadir Forum.\n\n"
                + "Cliquez pour verifier votre compte:\n" + verifyLink + "\n\n"
                + "Ce lien expire dans 24 heures.";
        sendTextMail(session, from, toEmail, "EST Agadir - Verification de compte", text);
    }

    private static Session buildSession(ServletContext ctx) throws MessagingException {
        String host = ctx.getInitParameter("mail.smtp.host");
        String port = ctx.getInitParameter("mail.smtp.port");
        String username = ctx.getInitParameter("mail.smtp.username");
        String password = ctx.getInitParameter("mail.smtp.password");

        if (isBlank(host) || isBlank(port) || isBlank(username) || isBlank(password) || isBlank(ctx.getInitParameter("mail.from"))) {
            throw new MessagingException("Mail SMTP is not configured in web.xml context params.");
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        return Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
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

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
