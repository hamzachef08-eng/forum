package ma.estagadir.forum.util;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class I18n {
    private static final String DEFAULT_LANG = "fr";
    private static final Map<String, Map<String, String>> MESSAGES = new HashMap<>();

    static {
        Map<String, String> fr = new HashMap<>();
        Map<String, String> en = new HashMap<>();

        fr.put("lang.fr", "FR");
        fr.put("lang.en", "EN");
        en.put("lang.fr", "FR");
        en.put("lang.en", "EN");

        fr.put("app.dashboard", "Dashboard");
        fr.put("app.back", "Retour");
        fr.put("app.logout", "Déconnexion");
        en.put("app.dashboard", "Dashboard");
        en.put("app.back", "Back");
        en.put("app.logout", "Logout");

        fr.put("auth.login.title", "Connexion Etudiant - EST Agadir");
        fr.put("auth.login.heading", "Connexion");
        fr.put("auth.login.email", "Email");
        fr.put("auth.login.password", "Mot de passe");
        fr.put("auth.login.submit", "Se connecter");
        fr.put("auth.login.forgot", "Mot de passe oublié ?");
        fr.put("auth.login.create", "Créer un compte étudiant");
        fr.put("auth.login.welcome", "Entrez vos identifiants étudiants");
        fr.put("auth.login.reset_ok", "Mot de passe réinitialisé avec succès. Vous pouvez vous connecter.");
        fr.put("auth.brand.title", "Plateforme d'apprentissage étudiant");
        fr.put("auth.brand.subtitle", "Connectez-vous pour accéder à vos modules de semestre, discussions et espaces de chat collaboratif.");

        en.put("auth.login.title", "Student Login - EST Agadir");
        en.put("auth.login.heading", "Login");
        en.put("auth.login.email", "Email");
        en.put("auth.login.password", "Password");
        en.put("auth.login.submit", "Sign in");
        en.put("auth.login.forgot", "Forgot password?");
        en.put("auth.login.create", "Create student account");
        en.put("auth.login.welcome", "Enter your student credentials");
        en.put("auth.login.reset_ok", "Password reset successful. You can now login.");
        en.put("auth.brand.title", "Student Learning Platform");
        en.put("auth.brand.subtitle", "Connect to access your semester modules, discussions, and collaborative chat spaces.");

        fr.put("auth.register.title", "Création de compte étudiant");
        fr.put("auth.register.heading", "Création de compte étudiant");
        fr.put("auth.register.eyebrow", "Inscription");
        fr.put("auth.register.nom", "Nom");
        fr.put("auth.register.prenom", "Prénom");
        fr.put("auth.register.password", "Mot de passe");
        fr.put("auth.register.filiere", "Filière");
        fr.put("auth.register.semestre", "Semestre");
        fr.put("auth.register.submit", "Créer mon compte");
        fr.put("auth.register.already", "Déjà inscrit ?");
        fr.put("auth.register.preview", "Modules correspondant au choix");
        fr.put("auth.register.choose", "-- Choisir --");
        fr.put("auth.register.select_hint", "Sélectionnez une filière et un semestre pour afficher les modules.");
        fr.put("auth.register.no_module", "Aucun module trouvé pour ce choix.");
        fr.put("auth.register.load_error", "Erreur de chargement des modules.");

        en.put("auth.register.title", "Student Account Creation");
        en.put("auth.register.heading", "Create student account");
        en.put("auth.register.eyebrow", "Register");
        en.put("auth.register.nom", "Last name");
        en.put("auth.register.prenom", "First name");
        en.put("auth.register.password", "Password");
        en.put("auth.register.filiere", "Track");
        en.put("auth.register.semestre", "Semester");
        en.put("auth.register.submit", "Create my account");
        en.put("auth.register.already", "Already registered?");
        en.put("auth.register.preview", "Modules for your selection");
        en.put("auth.register.choose", "-- Select --");
        en.put("auth.register.select_hint", "Select a track and semester to display modules.");
        en.put("auth.register.no_module", "No module found for this selection.");
        en.put("auth.register.load_error", "Module loading error.");

        fr.put("forgot.title", "Mot de passe oublié");
        fr.put("forgot.hint", "Entrez votre Gmail pour recevoir un code de réinitialisation à 6 chiffres.");
        fr.put("forgot.gmail", "Gmail");
        fr.put("forgot.submit", "Envoyer le code");
        fr.put("forgot.back_login", "Retour à la connexion");

        en.put("forgot.title", "Forgot Password");
        en.put("forgot.hint", "Enter your Gmail to receive a 6-digit reset code.");
        en.put("forgot.gmail", "Gmail");
        en.put("forgot.submit", "Send code");
        en.put("forgot.back_login", "Back to login");

        fr.put("reset.title", "Réinitialiser le mot de passe");
        fr.put("reset.hint", "Entrez le code à 6 chiffres reçu sur Gmail puis choisissez un nouveau mot de passe.");
        fr.put("reset.code_sent", "Code de vérification envoyé avec succès.");
        fr.put("reset.wait", "Veuillez attendre {0} secondes avant de renvoyer.");
        fr.put("reset.code_label", "Code à 6 chiffres");
        fr.put("reset.new_password", "Nouveau mot de passe");
        fr.put("reset.confirm_password", "Confirmer le mot de passe");
        fr.put("reset.submit", "Réinitialiser le mot de passe");
        fr.put("reset.resend", "Renvoyer le code");
        fr.put("reset.verifying", "Vérification du code...");
        fr.put("reset.code_ok", "Code vérifié avec succès.");
        fr.put("reset.code_bad", "Code invalide ou expiré.");
        fr.put("reset.verify_fail", "Échec de vérification. Réessayez.");
        fr.put("reset.gmail_rule", "L'email doit se terminer par @gmail.com");

        en.put("reset.title", "Reset Password");
        en.put("reset.hint", "Enter the 6-digit code sent to Gmail then choose a new password.");
        en.put("reset.code_sent", "Verification code sent successfully.");
        en.put("reset.wait", "Please wait {0} seconds before resending.");
        en.put("reset.code_label", "6-digit code");
        en.put("reset.new_password", "New password");
        en.put("reset.confirm_password", "Confirm password");
        en.put("reset.submit", "Reset password");
        en.put("reset.resend", "Resend code");
        en.put("reset.verifying", "Verifying code...");
        en.put("reset.code_ok", "Code verified successfully.");
        en.put("reset.code_bad", "Invalid or expired verification code.");
        en.put("reset.verify_fail", "Verification failed. Try again.");
        en.put("reset.gmail_rule", "Email must end with @gmail.com");

        fr.put("dashboard.title", "Dashboard Étudiant");
        fr.put("dashboard.eyebrow", "Dashboard");
        fr.put("dashboard.welcome", "Bienvenue");
        fr.put("dashboard.filiere", "Filière");
        fr.put("dashboard.semestre", "Semestre");
        fr.put("dashboard.modules", "Vos modules");
        fr.put("dashboard.noModules", "Aucun module trouvé pour votre semestre actuel.");
        fr.put("dashboard.blog", "Blog");
        fr.put("dashboard.blog_panel_title", "Blog et commentaires");
        fr.put("dashboard.blog_panel_text", "Accédez au blog pour publier et suivre les commentaires sur vos articles.");
        fr.put("dashboard.blog_panel_action", "Ouvrir le blog");
        fr.put("dashboard.account", "Mon compte");
        fr.put("dashboard.change_sem", "Changer semestre");
        fr.put("dashboard.admin_reports", "Signalements admin");
        fr.put("dashboard.open_chat", "Ouvrir le chat");
        fr.put("dashboard.open_blog", "Ouvrir le blog");
        fr.put("dashboard.sem_updated", "Votre semestre a été mis à jour.");
        fr.put("dashboard.latest_blog", "Dernier article du blog");
        fr.put("dashboard.latest_blog_empty", "Aucun article publié pour le moment.");
        fr.put("dashboard.latest_blog_open", "Lire l'article");
        fr.put("dashboard.stat.modules", "Modules");
        fr.put("dashboard.stat.my_articles", "Mes articles");
        fr.put("dashboard.stat.community_articles", "Articles communauté");
        fr.put("dashboard.stat.open_reports", "Signalements ouverts");
        fr.put("dashboard.stat.banned_users", "Utilisateurs bannis");

        en.put("dashboard.title", "Student Dashboard");
        en.put("dashboard.eyebrow", "Dashboard");
        en.put("dashboard.welcome", "Welcome");
        en.put("dashboard.filiere", "Track");
        en.put("dashboard.semestre", "Semester");
        en.put("dashboard.modules", "Your modules");
        en.put("dashboard.noModules", "No modules found for your current semester.");
        en.put("dashboard.blog", "Blog");
        en.put("dashboard.blog_panel_title", "Blog and comments");
        en.put("dashboard.blog_panel_text", "Open the blog to publish and follow comments on your articles.");
        en.put("dashboard.blog_panel_action", "Open blog");
        en.put("dashboard.account", "My account");
        en.put("dashboard.change_sem", "Change semester");
        en.put("dashboard.admin_reports", "Admin reports");
        en.put("dashboard.open_chat", "Open chat");
        en.put("dashboard.open_blog", "Open blog");
        en.put("dashboard.sem_updated", "Your semester has been updated.");
        en.put("dashboard.latest_blog", "Latest blog post");
        en.put("dashboard.latest_blog_empty", "No article published yet.");
        en.put("dashboard.latest_blog_open", "Read article");
        en.put("dashboard.stat.modules", "Modules");
        en.put("dashboard.stat.my_articles", "My articles");
        en.put("dashboard.stat.community_articles", "Community articles");
        en.put("dashboard.stat.open_reports", "Open reports");
        en.put("dashboard.stat.banned_users", "Banned users");

        fr.put("chat.title", "Chat - ");
        fr.put("chat.groups", "Groupes");
        fr.put("chat.module_discussion", "Discussion du module");
        fr.put("chat.ask_question", "Poser une question");
        fr.put("chat.questions_answers", "Questions et réponses");
        fr.put("chat.no_questions", "Aucune question pour ce module.");
        fr.put("chat.post", "Publier");
        fr.put("chat.no_messages", "Aucun message pour le moment. Lancez la conversation.");
        fr.put("chat.reply", "Répondre");
        fr.put("chat.report", "Signaler");
        fr.put("chat.send", "Envoyer");
        fr.put("chat.placeholder", "Écrire un message...");
        fr.put("chat.report_ok", "Signalement envoyé à l'admin.");
        fr.put("chat.ban_ok", "Utilisateur banni avec succès.");
        fr.put("chat.report_own", "Impossible de signaler votre propre message.");
        fr.put("chat.report_admin", "Impossible de signaler un message d'admin.");
        fr.put("chat.unknown", "Inconnu");
        fr.put("chat.deleted", "[message supprimé]");
        fr.put("chat.report_reason", "Message inapproprié");
        fr.put("chat.ghosted", "[message masqué - utilisateur banni]");

        en.put("chat.title", "Chat - ");
        en.put("chat.groups", "Groups");
        en.put("chat.module_discussion", "Module discussion");
        en.put("chat.ask_question", "Ask a question");
        en.put("chat.questions_answers", "Questions and answers");
        en.put("chat.no_questions", "No questions for this module yet.");
        en.put("chat.post", "Post");
        en.put("chat.no_messages", "No messages yet. Start the conversation.");
        en.put("chat.reply", "Reply");
        en.put("chat.report", "Report");
        en.put("chat.send", "Send");
        en.put("chat.placeholder", "Write a message...");
        en.put("chat.report_ok", "Report sent to admin.");
        en.put("chat.ban_ok", "User banned successfully.");
        en.put("chat.report_own", "You cannot report your own message.");
        en.put("chat.report_admin", "You cannot report an admin message.");
        en.put("chat.unknown", "Unknown");
        en.put("chat.deleted", "[message deleted]");
        en.put("chat.report_reason", "Inappropriate message");
        en.put("chat.ghosted", "[message hidden - banned user]");

        fr.put("blog.title", "Blog des étudiants");
        fr.put("blog.new", "Nouvel article");
        fr.put("blog.articleTitle", "Titre");
        fr.put("blog.articleContent", "Contenu");
        fr.put("blog.publish", "Publier");
        fr.put("blog.comments", "Commentaires");
        fr.put("blog.addComment", "Ajouter un commentaire");
        fr.put("blog.list", "Articles");
        fr.put("blog.feed.community", "Blogs des autres étudiants");
        fr.put("blog.feed.my", "Mes blogs");
        fr.put("blog.add_button", "Ajouter un blog");
        fr.put("blog.hide_button", "Fermer");
        fr.put("blog.none", "Aucun article pour le moment.");
        fr.put("blog.by", "Par");
        fr.put("blog.delete", "Supprimer");
        fr.put("blog.ban_user", "Bannir utilisateur");
        fr.put("blog.ban_done", "Utilisateur banni avec succès.");
        fr.put("blog.report", "Signaler à l'admin");
        fr.put("blog.report_reason", "Contenu de blog inapproprié");
        fr.put("blog.report_ok", "Signalement du blog envoyé à l'admin.");
        fr.put("blog.report_own", "Impossible de signaler votre propre blog.");
        fr.put("blog.report_admin", "Impossible de signaler un blog d'admin.");
        fr.put("blog.report_exists", "Vous avez déjà signalé ce blog.");
        fr.put("blog.follow_comments", "Voir commentaires");
        fr.put("blog.back", "Retour au blog");
        fr.put("blog.no_comment", "Aucun commentaire.");
        fr.put("blog.self_comment_blocked", "Vous ne pouvez pas commenter votre propre article.");
        fr.put("blog.ghosted_title", "[blog masqué - utilisateur banni]");
        fr.put("blog.ghosted_content", "Ce contenu est masqué tant que l'utilisateur est banni.");

        en.put("blog.title", "Student Blog");
        en.put("blog.new", "New article");
        en.put("blog.articleTitle", "Title");
        en.put("blog.articleContent", "Content");
        en.put("blog.publish", "Publish");
        en.put("blog.comments", "Comments");
        en.put("blog.addComment", "Add comment");
        en.put("blog.list", "Articles");
        en.put("blog.feed.community", "Other students' blogs");
        en.put("blog.feed.my", "My blogs");
        en.put("blog.add_button", "Add blog");
        en.put("blog.hide_button", "Close");
        en.put("blog.none", "No article yet.");
        en.put("blog.by", "By");
        en.put("blog.delete", "Delete");
        en.put("blog.ban_user", "Ban user");
        en.put("blog.ban_done", "User banned successfully.");
        en.put("blog.report", "Report to admin");
        en.put("blog.report_reason", "Inappropriate blog content");
        en.put("blog.report_ok", "Blog report sent to admin.");
        en.put("blog.report_own", "You cannot report your own blog.");
        en.put("blog.report_admin", "You cannot report an admin blog.");
        en.put("blog.report_exists", "You already reported this blog.");
        en.put("blog.follow_comments", "View comments");
        en.put("blog.back", "Back to blog");
        en.put("blog.no_comment", "No comments.");
        en.put("blog.self_comment_blocked", "You cannot comment on your own article.");
        en.put("blog.ghosted_title", "[hidden blog - banned user]");
        en.put("blog.ghosted_content", "This content is hidden while the user is banned.");

        fr.put("account.title", "Mon compte");
        fr.put("account.heading", "Gérer mon compte");
        fr.put("account.save", "Enregistrer");
        fr.put("account.updated", "Informations mises à jour avec succès.");
        fr.put("account.password_section", "Changer mot de passe (optionnel)");
        fr.put("account.current_password", "Mot de passe actuel");

        en.put("account.title", "My account");
        en.put("account.heading", "Manage my account");
        en.put("account.save", "Save");
        en.put("account.updated", "Information updated successfully.");
        en.put("account.password_section", "Change password (optional)");
        en.put("account.current_password", "Current password");

        fr.put("semester.title", "Changer semestre");
        fr.put("semester.heading", "Changer le semestre");
        fr.put("semester.eyebrow", "Profil");
        fr.put("semester.fixed", "Filière fixe");
        fr.put("semester.invalid", "Semestre invalide.");
        fr.put("semester.save", "Enregistrer");

        en.put("semester.title", "Change Semester");
        en.put("semester.heading", "Change semester");
        en.put("semester.eyebrow", "Profile");
        en.put("semester.fixed", "Fixed track");
        en.put("semester.invalid", "Invalid semester.");
        en.put("semester.save", "Save");

        fr.put("admin.reports.title", "Signalements messages");
        fr.put("admin.reports.only", "Seul l'admin peut accéder à cette page.");
        fr.put("admin.reports.done", "Utilisateur banni avec succès.");
        fr.put("admin.reports.none", "Aucun signalement en attente.");
        fr.put("admin.reports.reported", "Signalé");
        fr.put("admin.reports.by", "Par");
        fr.put("admin.reports.message", "Message");
        fr.put("admin.reports.article", "Article");
        fr.put("admin.reports.reason", "Raison");
        fr.put("admin.reports.ban", "Bannir utilisateur");
        fr.put("admin.reports.blog_title", "Signalements blogs");
        fr.put("admin.banned.title", "Étudiants bannis");
        fr.put("admin.banned.only", "Gestion des comptes bannis (admin).");
        fr.put("admin.banned.none", "Aucun étudiant banni.");
        fr.put("admin.banned.unban", "Retirer le ban");
        fr.put("admin.banned.done", "Ban retiré avec succès.");

        en.put("admin.reports.title", "Message reports");
        en.put("admin.reports.only", "Only admin can access this page.");
        en.put("admin.reports.done", "User banned successfully.");
        en.put("admin.reports.none", "No pending reports.");
        en.put("admin.reports.reported", "Reported");
        en.put("admin.reports.by", "By");
        en.put("admin.reports.message", "Message");
        en.put("admin.reports.article", "Article");
        en.put("admin.reports.reason", "Reason");
        en.put("admin.reports.ban", "Ban user");
        en.put("admin.reports.blog_title", "Blog reports");
        en.put("admin.banned.title", "Banned students");
        en.put("admin.banned.only", "Manage banned accounts (admin).");
        en.put("admin.banned.none", "No banned students.");
        en.put("admin.banned.unban", "Remove ban");
        en.put("admin.banned.done", "Ban removed successfully.");

        fr.put("register.verify.sent", "Compte créé. Vérifiez votre email avant de vous connecter.");
        fr.put("verify.success", "Email vérifié avec succès. Vous pouvez vous connecter.");
        fr.put("verify.invalid", "Lien de vérification invalide ou expiré.");
        fr.put("auth.login.new_account", "Nouveau compte ?");

        en.put("register.verify.sent", "Account created. Please verify your email before login.");
        en.put("verify.success", "Email verified successfully. You can now login.");
        en.put("verify.invalid", "Verification link is invalid or expired.");
        en.put("auth.login.new_account", "New account?");

        fr.put("err.required_email_password", "Email et mot de passe sont obligatoires.");
        fr.put("err.invalid_credentials", "Identifiants invalides.");
        fr.put("err.email_not_verified", "Email non vérifié. Consultez votre boîte mail.");
        fr.put("err.account_banned", "Votre compte est banni. Contactez l'administration.");
        fr.put("err.required_fields", "Tous les champs sont obligatoires.");
        fr.put("err.invalid_track_sem", "Choix filière/semestre invalide.");
        fr.put("err.email_exists", "Cet email existe déjà.");
        fr.put("err.verify_not_sent", "Compte créé, mais email de vérification non envoyé. Vérifiez SMTP.");
        fr.put("err.required_email", "Email requis.");
        fr.put("err.gmail_only", "L'email doit se terminer par @gmail.com.");
        fr.put("err.no_account_email", "Aucun compte trouvé avec cet email.");
        fr.put("err.smtp", "Impossible d'envoyer l'email. Vérifiez SMTP.");
        fr.put("err.all_fields_required", "Tous les champs sont obligatoires.");
        fr.put("err.password_mismatch", "Les mots de passe ne correspondent pas.");
        fr.put("err.code_6_digits", "Le code doit contenir exactement 6 chiffres.");
        fr.put("err.invalid_email_or_code", "Email ou code invalide.");
        fr.put("err.invalid_or_expired_code", "Code invalide ou expiré.");
        fr.put("err.profile_required", "Nom, prénom, email et semestre sont obligatoires.");
        fr.put("err.email_used", "Cet email est déjà utilisé.");
        fr.put("err.password_fields_required", "Pour changer le mot de passe, remplissez tous les champs mot de passe.");
        fr.put("err.current_password_wrong", "Mot de passe actuel incorrect.");

        en.put("err.required_email_password", "Email and password are required.");
        en.put("err.invalid_credentials", "Invalid credentials.");
        en.put("err.email_not_verified", "Email is not verified. Check your inbox.");
        en.put("err.account_banned", "Your account is banned. Contact administration.");
        en.put("err.required_fields", "All fields are required.");
        en.put("err.invalid_track_sem", "Invalid track/semester selection.");
        en.put("err.email_exists", "This email already exists.");
        en.put("err.verify_not_sent", "Account created, but verification email was not sent. Check SMTP.");
        en.put("err.required_email", "Email is required.");
        en.put("err.gmail_only", "Email must end with @gmail.com.");
        en.put("err.no_account_email", "No account found with this email.");
        en.put("err.smtp", "Unable to send email. Check SMTP configuration.");
        en.put("err.all_fields_required", "All fields are required.");
        en.put("err.password_mismatch", "Passwords do not match.");
        en.put("err.code_6_digits", "Code must be exactly 6 digits.");
        en.put("err.invalid_email_or_code", "Invalid email or code.");
        en.put("err.invalid_or_expired_code", "Invalid or expired code.");
        en.put("err.profile_required", "Last name, first name, email and semester are required.");
        en.put("err.email_used", "This email is already used.");
        en.put("err.password_fields_required", "To change password, fill all password fields.");
        en.put("err.current_password_wrong", "Current password is incorrect.");

        MESSAGES.put("fr", fr);
        MESSAGES.put("en", en);
    }

    private I18n() {
    }

    public static String t(HttpServletRequest req, String key) {
        String lang = getLang(req);
        Map<String, String> bundle = MESSAGES.getOrDefault(lang, MESSAGES.get(DEFAULT_LANG));
        String value = bundle.get(key);
        if (value != null) {
            return value;
        }
        return MESSAGES.get(DEFAULT_LANG).getOrDefault(key, key);
    }

    public static String t(HttpServletRequest req, String key, String arg0) {
        return t(req, key).replace("{0}", arg0 == null ? "" : arg0);
    }

    public static String getLang(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return DEFAULT_LANG;
        }
        Object raw = session.getAttribute("lang");
        if (raw == null) {
            return DEFAULT_LANG;
        }
        String lang = String.valueOf(raw).trim().toLowerCase();
        if (!"en".equals(lang) && !"fr".equals(lang)) {
            return DEFAULT_LANG;
        }
        return lang;
    }
}
