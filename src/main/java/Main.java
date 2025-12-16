package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

// Imports nécessaires (Vérifiez vos packages)
import XMLIO.XMLAnalyser;
import metaModel.Entity;
import metaModel.Model;
import visitor.JavaVisitor;

public class Main {

    // Configuration des fichiers
    private static final String INPUT_XML = "src/main/resources/satelite.xml";
    private static final String OUTPUT_JAVA_DIR = "src/main/java/javaClass";

    public static void main(String[] args) {

        System.out.println("\nLecture du fichier XML : " + INPUT_XML);

        File xmlFile = new File(INPUT_XML);
        if (!xmlFile.exists()) {
            System.err.println("ERREUR : Le fichier XML '" + INPUT_XML + "' est introuvable.");
            return;
        }

        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromFile(xmlFile);

        if (model == null || model.getEntities().isEmpty()) {
            System.err.println("ERREUR : Le modèle est vide ou l'analyse XML a échoué.");
            return;
        }
        System.out.println("-> Modèle chargé en mémoire : " + model.getEntities().size() + " entité(s).");
        System.out.println("\nGénération du code Java...");


        File outputDir = new File(OUTPUT_JAVA_DIR);
        if (!outputDir.exists()) {
            boolean created = outputDir.mkdirs();
            if (created) System.out.println("-> Dossier de sortie créé : " + OUTPUT_JAVA_DIR);
        }

        int count = 0;

        for (Entity entity : model.getEntities()) {
            JavaVisitor visitor = new JavaVisitor();
            entity.accept(visitor);
            String javaCode = visitor.getResult();
            String fileName = entity.getName() + ".java";
            File javaFile = new File(outputDir, fileName);

            try (FileWriter writer = new FileWriter(javaFile)) {
                writer.write(javaCode);
                System.out.println("   [+] Fichier généré : " + javaFile.getAbsolutePath());
                count++;
            } catch (IOException e) {
                System.err.println("   [-] Erreur d'écriture pour " + fileName + " : " + e.getMessage());
            }
        }

        System.out.println("\n=================================================");
        System.out.println(" TERMINÉ : " + count + " classe(s) Java générée(s).");
        System.out.println("=================================================");
    }
}