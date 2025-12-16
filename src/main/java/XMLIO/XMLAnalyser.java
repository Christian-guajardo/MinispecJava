package XMLIO;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;


import metaModel.*;
import metaModel.Entity;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class XMLAnalyser {

	// Les clés des 2 Map sont les id 
	
	// Map des instances de la syntaxe abstraite (metamodel)
	protected Map<String, MinispecElement> minispecIndex;
	// Map des elements XML
	protected Map<String, Element> xmlElementIndex;

	public XMLAnalyser() {
		this.minispecIndex = new HashMap<String, MinispecElement>();
		this.xmlElementIndex = new HashMap<String, Element>();
	}

	protected Model modelFromElement(Element e) {
		return new Model();
	}
	
	protected Entity entityFromElement(Element e) {
		String name = e.getAttribute("name");
		Entity entity = new Entity();
		entity.setName(name);
		Model model = (Model) minispecElementFromXmlElement(this.xmlElementIndex.get(e.getAttribute("model")));
		model.addEntity(entity);
		return entity;
	}

	protected MinispecElement minispecElementFromXmlElement(Element e) {
		String id = e.getAttribute("id");
		MinispecElement result = this.minispecIndex.get(id);
		if (result != null) return result;
		String tag = e.getTagName();
		if (tag.equals("Model")) {
			result = modelFromElement(e);
		} else if (tag.equals("Entity")) {
			result = entityFromElement(e);
		} else if (tag.equals("Attribute")) {
			result = attributeFromElement(e);
		} else {
			throw new IllegalArgumentException("Tag XML inconnu : " + tag);
		}
		this.minispecIndex.put(id, result);
		return result;
	}
	protected Attribute attributeFromElement(Element e) {
		String name = e.getAttribute("name");
		String type = e.getAttribute("type");
		Attribute attribute = new Attribute();
		attribute.setName(name);
		attribute.setType(type);

		Element parentXml = this.xmlElementIndex.get(e.getAttribute("entity"));
		Entity entity = (Entity) minispecElementFromXmlElement(parentXml);

		entity.getAttributes().add(attribute);

		return attribute;
	}

	// alimentation du map des elements XML
	protected void firstRound(Element el) {
		NodeList nodes = el.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);
			if (n instanceof Element) {
				Element child = (Element) n;
				String id = child.getAttribute("id");
				this.xmlElementIndex.put(id, child);
			}
		}
	}

	// alimentation du map des instances de la syntaxe abstraite (metamodel)
	protected void secondRound(Element el) {
		NodeList nodes = el.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);
			if (n instanceof Element) {
				minispecElementFromXmlElement((Element)n);
			}
		}
	}

	public Model getModelFromDocument(Document document) {
		Element e = document.getDocumentElement();
		
		firstRound(e);
		
		secondRound(e);
		
		Model model = (Model) this.minispecIndex.get(e.getAttribute("model"));
				
		return model;
	}
	
	public Model getModelFromInputStream(InputStream stream) {
		try {
			// création d'une fabrique de documents
			DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();

			// création d'un constructeur de documents
			DocumentBuilder constructeur = fabrique.newDocumentBuilder();
			Document document = constructeur.parse(stream);
			return getModelFromDocument(document);

		} catch (ParserConfigurationException pce) {
			System.out.println("Erreur de configuration du parseur DOM");
			System.out.println("lors de l'appel à fabrique.newDocumentBuilder();");
		} catch (SAXException se) {
			System.out.println("Erreur lors du parsing du document");
			System.out.println("lors de l'appel à construteur.parse(xml)");
		} catch (IOException ioe) {
			System.out.println("Erreur d'entrée/sortie");
			System.out.println("lors de l'appel à construteur.parse(xml)");
		}
		return null;
	}
	
	public Model getModelFromString(String contents) {		
		InputStream stream = new ByteArrayInputStream(contents.getBytes());
		return getModelFromInputStream(stream);
	}
	
	public Model getModelFromFile(File file) {		
		InputStream stream = null;
		try {
			stream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getModelFromInputStream(stream);
	}

	public Model getModelFromFilenamed(String filename) {
			File file = new File(filename);
			return getModelFromFile(file);
	}
}
