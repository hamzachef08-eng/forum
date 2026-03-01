package ma.estagadir.forum.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class AcademicCatalog {
    private static final List<String> FILIERES = Collections.unmodifiableList(Arrays.asList(
            "Conception et Développement Logiciel (CDL)",
            "Informatique et Développement Digital (IDD)",
            "Techniques de Management (TM)",
            "Électronique, Électrotechnique et Systèmes Automatisés (EESA)",
            "Génie Bio-Industriel (GBI)",
            "Techniques de Communication et de Commercialisation (TCC)"));

    private static final List<String> SEMESTRES = Collections.unmodifiableList(Arrays.asList("S1", "S2", "S3", "S4"));

    private AcademicCatalog() {
    }

    public static List<String> filieres() {
        return FILIERES;
    }

    public static List<String> semestres() {
        return SEMESTRES;
    }

    public static boolean isValidFiliere(String filiere) {
        return filiere != null && FILIERES.contains(filiere);
    }

    public static boolean isValidSemestre(String semestre) {
        return semestre != null && SEMESTRES.contains(semestre);
    }
}
