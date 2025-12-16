package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import XMLIO.XMLAnalyser;
import metaModel.Model;
import prettyPrinter.PrettyPrinter;
import visitor.JavaVisitor;

public class Main {


    private static final String XML_PATH = "satellite.xml";
    private static final String JAVA_OUTPUT_PATH = "Satellite.java";

    public static void main(String[] args) throws ParserConfigurationException {


        XMLAnalyser analyser = new XMLAnalyser();
        File xmlFile = new File(XML_PATH);

        System.out.println("\nAnalyse XML et construction modèle");
        Model model = analyser.getModelFromFile(xmlFile);


        System.out.println("\nExécution du PrettyPrinter");
        PrettyPrinter pp = new PrettyPrinter();
        model.accept(pp);
        String minispecOutput = pp.result();

        System.out.println("\nSortie Minispec\n");
        System.out.println(minispecOutput);


        System.out.println("\nJavaVisitor et génération de code ");
        JavaVisitor jv = new JavaVisitor();
        model.accept(jv);
        String javaCode = jv.getResult();

        System.out.println("\n[Code Java généré pour " + model.getEntities().get(0).getName() + "]\n");


        try (FileWriter writer = new FileWriter(JAVA_OUTPUT_PATH)) {
            writer.write(javaCode);
            System.out.println("\nSuccès: Le code Java a été écrit dans " + JAVA_OUTPUT_PATH);
        } catch (IOException e) {
            System.err.println("\nErreur lors de l'écriture du fichier Java: " + e.getMessage());
        }
    }
}