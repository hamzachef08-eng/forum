DROP TABLE IF EXISTS answers;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS message_reports;
DROP TABLE IF EXISTS password_reset_codes;
DROP TABLE IF EXISTS article_comments;
DROP TABLE IF EXISTS articles;
DROP TABLE IF EXISTS email_verification_tokens;
DROP TABLE IF EXISTS module_messages;
DROP TABLE IF EXISTS modules;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id BIGSERIAL,
  nom VARCHAR(100) NOT NULL,
  prenom VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL,
  mot_de_passe VARCHAR(255) NOT NULL,
  filiere VARCHAR(180) NOT NULL,
  semestre VARCHAR(2) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'STUDENT' CHECK (role IN ('STUDENT','ADMIN')),
  is_banned SMALLINT NOT NULL DEFAULT 0,
  email_verified SMALLINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT uq_users_email UNIQUE (email)
) ;

CREATE TABLE modules (
  id BIGSERIAL,
  filiere VARCHAR(180) NOT NULL,
  semestre VARCHAR(2) NOT NULL,
  nom_module VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
) ;

CREATE TABLE module_messages (
  id BIGSERIAL,
  module_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  parent_message_id BIGINT NULL,
  content TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_mm_module FOREIGN KEY (module_id) REFERENCES modules(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_mm_user FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_mm_parent FOREIGN KEY (parent_message_id) REFERENCES module_messages(id)
    ON DELETE SET NULL ON UPDATE CASCADE
) ;

CREATE TABLE password_reset_codes (
  id BIGSERIAL,
  user_id BIGINT NOT NULL,
  code_hash VARCHAR(255) NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  last_sent_at TIMESTAMP NOT NULL,
  used_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_prc_user FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ;

CREATE TABLE email_verification_tokens (
  id BIGSERIAL,
  user_id BIGINT NOT NULL,
  token_hash VARCHAR(255) NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  used_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_evt_user FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ;

CREATE TABLE articles (
  id BIGSERIAL,
  author_id BIGINT NOT NULL,
  title VARCHAR(180) NOT NULL,
  content TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_articles_author FOREIGN KEY (author_id) REFERENCES users(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ;

CREATE TABLE article_comments (
  id BIGSERIAL,
  article_id BIGINT NOT NULL,
  author_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_ac_article FOREIGN KEY (article_id) REFERENCES articles(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_ac_author FOREIGN KEY (author_id) REFERENCES users(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ;

CREATE TABLE message_reports (
  id BIGSERIAL,
  message_id BIGINT NOT NULL,
  reported_user_id BIGINT NOT NULL,
  reporter_user_id BIGINT NOT NULL,
  reason VARCHAR(255) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN','BANNED','DISMISSED')),
  reviewed_by BIGINT NULL,
  reviewed_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_rep_message FOREIGN KEY (message_id) REFERENCES module_messages(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_rep_reported FOREIGN KEY (reported_user_id) REFERENCES users(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_rep_reporter FOREIGN KEY (reporter_user_id) REFERENCES users(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_rep_admin FOREIGN KEY (reviewed_by) REFERENCES users(id)
    ON DELETE SET NULL ON UPDATE CASCADE
) ;

INSERT INTO modules (filiere, semestre, nom_module) VALUES
-- CDL S1
('Conception et Développement Logiciel (CDL)', 'S1', 'Algo & Programmation C'),
('Conception et Développement Logiciel (CDL)', 'S1', 'Mathématique 1'),
('Conception et Développement Logiciel (CDL)', 'S1', 'Architecture des Ordinateurs'),
('Conception et Développement Logiciel (CDL)', 'S1', 'Système exploitation'),
('Conception et Développement Logiciel (CDL)', 'S1', 'Programmation Python'),
('Conception et Développement Logiciel (CDL)', 'S1', 'Méthodologie de travail'),
('Conception et Développement Logiciel (CDL)', 'S1', 'Langues & Communication'),
-- CDL S2
('Conception et Développement Logiciel (CDL)', 'S2', 'Culture digitale'),
('Conception et Développement Logiciel (CDL)', 'S2', 'Développement Web (HTML/CSS/JS)'),
('Conception et Développement Logiciel (CDL)', 'S2', 'Base de données relationnelles'),
('Conception et Développement Logiciel (CDL)', 'S2', 'Réseaux'),
('Conception et Développement Logiciel (CDL)', 'S2', 'PHP/LARAVEL'),
('Conception et Développement Logiciel (CDL)', 'S2', 'Structure de données en C'),
('Conception et Développement Logiciel (CDL)', 'S2', 'Langues 2'),
-- CDL S3
('Conception et Développement Logiciel (CDL)', 'S3', 'POO (Java)'),
('Conception et Développement Logiciel (CDL)', 'S3', 'Ingénierie Logicielle (Agile)'),
('Conception et Développement Logiciel (CDL)', 'S3', 'Bases de données NoSQL'),
('Conception et Développement Logiciel (CDL)', 'S3', 'Modélisation UML'),
('Conception et Développement Logiciel (CDL)', 'S3', 'Gestion de Projets'),
('Conception et Développement Logiciel (CDL)', 'S3', 'Mathématique 1'),
('Conception et Développement Logiciel (CDL)', 'S3', 'JAVA avancée'),
-- CDL S4
('Conception et Développement Logiciel (CDL)', 'S4', 'Dev Mobile (Android/iOS)'),
('Conception et Développement Logiciel (CDL)', 'S4', 'Complexité computationnelle'),
('Conception et Développement Logiciel (CDL)', 'S4', 'JEE'),
('Conception et Développement Logiciel (CDL)', 'S4', 'Introduction à l''IA'),

-- IDD S1
('Informatique et Développement Digital (IDD)', 'S1', 'Algo & Programmation C'),
('Informatique et Développement Digital (IDD)', 'S1', 'Développement Web (HTML/CSS/JS)'),
('Informatique et Développement Digital (IDD)', 'S1', 'Architecture des Ordinateurs'),
('Informatique et Développement Digital (IDD)', 'S1', 'Système d''Information & BD SQL'),
('Informatique et Développement Digital (IDD)', 'S1', 'Introduction à l''IA'),
('Informatique et Développement Digital (IDD)', 'S1', 'Réseaux'),
('Informatique et Développement Digital (IDD)', 'S1', 'Langues & Communication'),
-- IDD S2
('Informatique et Développement Digital (IDD)', 'S2', 'Programmation Python'),
('Informatique et Développement Digital (IDD)', 'S2', 'Modélisation UML'),
('Informatique et Développement Digital (IDD)', 'S2', 'Comptabilité Générale'),
('Informatique et Développement Digital (IDD)', 'S2', 'Conception Logicielle'),
('Informatique et Développement Digital (IDD)', 'S2', 'Maths Appliquées'),
('Informatique et Développement Digital (IDD)', 'S2', 'Culture Digitale'),
('Informatique et Développement Digital (IDD)', 'S2', 'Langues II'),
-- IDD S3
('Informatique et Développement Digital (IDD)', 'S3', 'POO (Java)'),
('Informatique et Développement Digital (IDD)', 'S3', 'Ingénierie Logicielle (Agile)'),
('Informatique et Développement Digital (IDD)', 'S3', 'Bases de données NoSQL'),
('Informatique et Développement Digital (IDD)', 'S3', 'Système Linux'),
('Informatique et Développement Digital (IDD)', 'S3', 'Machine Learning'),
('Informatique et Développement Digital (IDD)', 'S3', 'IoT & Applications'),
('Informatique et Développement Digital (IDD)', 'S3', 'Révolution Digitale'),
-- IDD S4
('Informatique et Développement Digital (IDD)', 'S4', 'Dev Mobile (Android/iOS)'),
('Informatique et Développement Digital (IDD)', 'S4', 'Sécurité Informatique'),
('Informatique et Développement Digital (IDD)', 'S4', 'Gestion de Projets'),
('Informatique et Développement Digital (IDD)', 'S4', 'Entrepreneuriat'),

-- TM S1
('Techniques de Management (TM)', 'S1', 'Langues & TEC'),
('Techniques de Management (TM)', 'S1', 'Environnement Économique (Micro-économie/Droit)'),
('Techniques de Management (TM)', 'S1', 'Comptabilité Générale 1 & Management'),
('Techniques de Management (TM)', 'S1', 'Mathématiques Appliquées & Statistiques I'),
-- TM S2
('Techniques de Management (TM)', 'S2', 'Statistiques II & Maths Financières'),
('Techniques de Management (TM)', 'S2', 'Macro-économie & Droit des affaires'),
('Techniques de Management (TM)', 'S2', 'Comptabilité Générale 2'),
('Techniques de Management (TM)', 'S2', 'Marketing Fondamental'),
-- TM S3
('Techniques de Management (TM)', 'S3', 'Comptabilité de Société & Analytique'),
('Techniques de Management (TM)', 'S3', 'Informatique de Gestion (Excel avancé/Access)'),
('Techniques de Management (TM)', 'S3', 'Gestion Financière & Fiscalité'),
('Techniques de Management (TM)', 'S3', 'Marketing Opérationnel & GRH'),
-- TM S4
('Techniques de Management (TM)', 'S4', 'Audit & Contrôle de Gestion'),
('Techniques de Management (TM)', 'S4', 'Entrepreneuriat & Jeu d''entreprise'),
('Techniques de Management (TM)', 'S4', 'Stage de Fin d''Études'),

-- EESA S1
('Électronique, Électrotechnique et Systèmes Automatisés (EESA)', 'S1', 'Langues & Communication'),
('Électronique, Électrotechnique et Systèmes Automatisés (EESA)', 'S1', 'Physique (Électricité/Thermique)'),
('Électronique, Électrotechnique et Systèmes Automatisés (EESA)', 'S1', 'Maths & Informatique 1'),
('Électronique, Électrotechnique et Systèmes Automatisés (EESA)', 'S1', 'Électronique 1 (Analogique & Numérique de base)'),
-- EESA S2
('Électronique, Électrotechnique et Systèmes Automatisés (EESA)', 'S2', 'Maths & Informatique 2 (Prog C)'),
('Électronique, Électrotechnique et Systèmes Automatisés (EESA)', 'S2', 'Électrotechnique (Transformateurs/Machines)'),
('Électronique, Électrotechnique et Systèmes Automatisés (EESA)', 'S2', 'Électronique 2 (Systèmes)'),
('Électronique, Électrotechnique et Systèmes Automatisés (EESA)', 'S2', 'Dessin technique & CAO'),
-- EESA S3
('Électronique, Électrotechnique et Systèmes Automatisés (EESA)', 'S3', 'Informatique Industrielle (Microcontrôleurs)'),
('Électronique, Électrotechnique et Systèmes Automatisés (EESA)', 'S3', 'Électronique de Puissance'),
('Électronique, Électrotechnique et Systèmes Automatisés (EESA)', 'S3', 'Automatique & Instrumentation'),
('Électronique, Électrotechnique et Systèmes Automatisés (EESA)', 'S3', 'Systèmes Automatisés (API) & Réseaux Locaux Industriels'),
-- EESA S4
('Électronique, Électrotechnique et Systèmes Automatisés (EESA)', 'S4', 'Culture d''entreprise'),
('Électronique, Électrotechnique et Systèmes Automatisés (EESA)', 'S4', 'Installation & Réseaux Électriques'),
('Électronique, Électrotechnique et Systèmes Automatisés (EESA)', 'S4', 'Maintenance Industrielle'),
('Électronique, Électrotechnique et Systèmes Automatisés (EESA)', 'S4', 'Stage de Fin d''Études'),

-- TCC S1
('Techniques de Communication et de Commercialisation (TCC)', 'S1', 'Langues & Méthodologie'),
('Techniques de Communication et de Commercialisation (TCC)', 'S1', 'Économie Générale & Droit'),
('Techniques de Communication et de Commercialisation (TCC)', 'S1', 'Techniques Quantitatives (Stats/Compta)'),
('Techniques de Communication et de Commercialisation (TCC)', 'S1', 'Marketing Fondamental'),
-- TCC S2
('Techniques de Communication et de Commercialisation (TCC)', 'S2', 'Communication Commerciale'),
('Techniques de Communication et de Commercialisation (TCC)', 'S2', 'Marketing Direct & Comportement du consommateur'),
('Techniques de Communication et de Commercialisation (TCC)', 'S2', 'Outils de Gestion (Informatique)'),
('Techniques de Communication et de Commercialisation (TCC)', 'S2', 'Négociation Achat/Vente'),
-- TCC S3
('Techniques de Communication et de Commercialisation (TCC)', 'S3', 'Commerce International (Logistique/Douane)'),
('Techniques de Communication et de Commercialisation (TCC)', 'S3', 'Communication Événementielle'),
('Techniques de Communication et de Commercialisation (TCC)', 'S3', 'Stratégie Marketing'),
('Techniques de Communication et de Commercialisation (TCC)', 'S3', 'Webmarketing & E-commerce'),
-- TCC S4
('Techniques de Communication et de Commercialisation (TCC)', 'S4', 'Anglais des affaires & Espagnol'),
('Techniques de Communication et de Commercialisation (TCC)', 'S4', 'Entrepreneuriat & Psychosociologie'),
('Techniques de Communication et de Commercialisation (TCC)', 'S4', 'Stage de Fin d''Études'),

-- GBI S1
('Génie Bio-Industriel (GBI)', 'S1', 'Physique (Mécanique des fluides/Thermique)'),
('Génie Bio-Industriel (GBI)', 'S1', 'Biologie générale & Biostatistiques'),
('Génie Bio-Industriel (GBI)', 'S1', 'Chimie Générale & Organique I'),
('Génie Bio-Industriel (GBI)', 'S1', 'Langues & Bio-informatique'),
-- GBI S2
('Génie Bio-Industriel (GBI)', 'S2', 'Biochimie & Enzymologie'),
('Génie Bio-Industriel (GBI)', 'S2', 'Chimie des solutions'),
('Génie Bio-Industriel (GBI)', 'S2', 'Microbiologie Générale'),
('Génie Bio-Industriel (GBI)', 'S2', 'Biologie Cellulaire'),
-- GBI S3
('Génie Bio-Industriel (GBI)', 'S3', 'Technologie Alimentaire & Sciences des aliments'),
('Génie Bio-Industriel (GBI)', 'S3', 'Techniques d''Analyse & Métrologie'),
('Génie Bio-Industriel (GBI)', 'S3', 'Opérations Unitaires (Transferts de masse/chaleur)'),
('Génie Bio-Industriel (GBI)', 'S3', 'Qualité (QHSE) & Sécurité alimentaire'),
-- GBI S4
('Génie Bio-Industriel (GBI)', 'S4', 'Biotechnologies'),
('Génie Bio-Industriel (GBI)', 'S4', 'Gestion de Production & Maintenance'),
('Génie Bio-Industriel (GBI)', 'S4', 'Stage de Fin d''Études');

-- student password: student123
INSERT INTO users (nom, prenom, email, mot_de_passe, filiere, semestre, role, is_banned, email_verified)
VALUES
('Étudiant', 'Test', 'student@estagadir.ma', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Conception et Développement Logiciel (CDL)', 'S1', 'STUDENT', 0, 1)
ON CONFLICT (email) DO UPDATE SET
nom = EXCLUDED.nom,
prenom = EXCLUDED.prenom,
mot_de_passe = EXCLUDED.mot_de_passe,
filiere = EXCLUDED.filiere,
semestre = EXCLUDED.semestre,
role = EXCLUDED.role,
is_banned = EXCLUDED.is_banned,
email_verified = EXCLUDED.email_verified;

-- admin password: admin123
INSERT INTO users (nom, prenom, email, mot_de_passe, filiere, semestre, role, is_banned, email_verified)
VALUES
('Admin', 'EST', 'admin@estagadir.ma', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Conception et Développement Logiciel (CDL)', 'S1', 'ADMIN', 0, 1)
ON CONFLICT (email) DO UPDATE SET
nom = EXCLUDED.nom,
prenom = EXCLUDED.prenom,
mot_de_passe = EXCLUDED.mot_de_passe,
role = EXCLUDED.role,
is_banned = EXCLUDED.is_banned,
email_verified = EXCLUDED.email_verified;


