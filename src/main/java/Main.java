

import XMLIO.XMLAnalyser;
import metaModel.Entity;
import metaModel.Model;
import visitor.JavaVisitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {


    private static final String INPUT_XML = "src/main/resources/satelite.xml";
    private static final String OUTPUT_DIR = "src/main/gen";
    private static final String PACKAGE_NAME = "gen";

    public static void main(String[] args) {



        File xmlFile = new File(INPUT_XML);
        if (!xmlFile.exists()) {
            System.err.println("ERREUR CRITIQUE : Le fichier XML est introuvable !");
            System.err.println("Chemin cherché : " + xmlFile.getAbsolutePath());
            return;
        }


        System.out.println("\n Lecture et Analyse du XML...");
        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromFile(xmlFile);

        if (model == null) {
            System.err.println("ERREUR : Impossible de créer le modèle (XML invalide ?).");
            return;
        }
        System.out.println("-> Modèle chargé en mémoire.");


        File outputFolder = new File(OUTPUT_DIR);
        if (!outputFolder.exists()) {
            boolean created = outputFolder.mkdirs();
            if (created) System.out.println("-> Dossier '" + OUTPUT_DIR + "' créé.");
        }


        System.out.println("\n Génération des fichiers Java...");
        int count = 0;

        for (Entity entity : model.getEntities()) {

            JavaVisitor visitor = new JavaVisitor(PACKAGE_NAME);


            entity.accept(visitor);


            String content = visitor.getResult();


            String filename = entity.getName() + ".java";
            File file = new File(outputFolder, filename);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
                System.out.println("   [OK] " + filename + " généré.");
                count++;
            } catch (IOException e) {
                System.err.println("   [ERREUR] Impossible d'écrire " + filename);
                e.printStackTrace();
            }
        }


        System.out.println("SUCCÈS : " + count + " classes générées dans '" + PACKAGE_NAME + "'.");

    }
}