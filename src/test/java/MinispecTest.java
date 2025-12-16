

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import XMLIO.XMLAnalyser;
import metaModel.Model;
import metaModel.Entity;
import visitor.JavaVisitor;

public class MinispecTest {

    private static Model testModel;
    private static final String XML_CONTENT =
            "<Root model=\"#1\">\n"
                    + "    <Model id=\"#1\"/>\n"
                    + "    <Entity id=\"#2\" name=\"Satellite\" model=\"#1\"/>\n"
                    + "    <Attribute id=\"#3\" name=\"nom\" type=\"String\" entity=\"#2\"/>\n"
                    + "    <Attribute id=\"#4\" name=\"id\" type=\"Integer\" entity=\"#2\"/>\n"
                    + "</Root>";


    @TempDir
    static File tempDir;
    private static File xmlFile;


    @BeforeAll
    static void setUp() throws IOException {

        xmlFile = new File(tempDir, "satellite.xml");
        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write(XML_CONTENT);
        }


        XMLAnalyser analyser = new XMLAnalyser();
        testModel = analyser.getModelFromFile(xmlFile);
        assertNotNull(testModel, "L'analyseur XML n'a pas réussi à charger le modèle.");
    }

    @Test
    void testModelStructureIntegrity() {

        assertNotNull(testModel, "Le modèle en mémoire ne doit pas être nul.");
        assertEquals(1, testModel.getEntities().size(), "Le modèle doit contenir exactement 1 entité.");

        Entity satellite = testModel.getEntities().get(0);
        assertEquals("Satellite", satellite.getName(), "Le nom de l'entité doit être 'Satellite'.");




        assertEquals(2, satellite.getAttributes().size(), "L'entité 'Satellite' doit contenir exactement 2 attributs.");
        assertTrue(satellite.getAttributes().stream().anyMatch(a -> "nom".equals(a.getName()) && "String".equals(a.getType())),
                "L'attribut 'nom' (String) est manquant ou incorrect.");
        assertTrue(satellite.getAttributes().stream().anyMatch(a -> "id".equals(a.getName()) && "Integer".equals(a.getType())),
                "L'attribut 'id' (Integer) est manquant ou incorrect.");
    }

    @Test
    void testJavaVisitorFileGeneration() throws IOException {
        String javaFileName = "Satellite.java";
        File outputFile = new File(tempDir, javaFileName);


        JavaVisitor jv = new JavaVisitor();
        testModel.accept(jv);
        String javaCode = jv.getResult();


        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(javaCode);
        }

        assertTrue(outputFile.exists(), "Le fichier de code Java '" + javaFileName + "' doit être créé.");

    }


}