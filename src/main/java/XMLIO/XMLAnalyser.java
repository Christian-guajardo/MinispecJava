package XMLIO;

import javax.xml.parsers.*;

import metaModel.Entity;
import org.w3c.dom.*;
import metaModel.*;

import java.io.*;
import java.util.*;

public class XMLAnalyser {


	protected Map<String, MinispecElement> minispecIndex;

	protected Map<String, Element> xmlElementIndex;


	protected Map<String, Entity> nameToEntityIndex;

	protected List<ReferenceType> pendingReferences;

	public XMLAnalyser() {
		this.minispecIndex = new HashMap<>();
		this.xmlElementIndex = new HashMap<>();
		this.nameToEntityIndex = new HashMap<>();
		this.pendingReferences = new ArrayList<>();
	}

	public Model getModelFromDocument(Document document) {

		this.minispecIndex.clear();
		this.xmlElementIndex.clear();
		this.nameToEntityIndex.clear();
		this.pendingReferences.clear();

		Element root = document.getDocumentElement();


		indexXmlIds(root);


		String modelId = root.getAttribute("model");
		if (modelId.isEmpty()) modelId = findModelIdInChildren(root);


		Model model = (Model) getOrCreateMinispecElement(modelId);

		instantiateAllElements();
		resolveReferences();

		return model;
	}


	protected void indexXmlIds(Element root) {
		NodeList nodes = root.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);
			if (n instanceof Element) {
				Element el = (Element) n;
				String id = el.getAttribute("id");
				if (id != null && !id.isEmpty()) {
					this.xmlElementIndex.put(id, el);
				}
			}
		}
	}


	private void instantiateAllElements() {
		for (String id : xmlElementIndex.keySet()) {
			getOrCreateMinispecElement(id);
		}
	}


	protected MinispecElement getOrCreateMinispecElement(String id) {
		if (id == null || id.isEmpty()) return null;

		if (this.minispecIndex.containsKey(id)) {
			return this.minispecIndex.get(id);
		}


		Element e = this.xmlElementIndex.get(id);
		if (e == null) return null;


		MinispecElement result = null;
		String tag = e.getTagName();

		switch (tag) {
			case "Model":     result = modelFromElement(e); break;
			case "Entity":    result = entityFromElement(e); break;
			case "Attribute": result = attributeFromElement(e); break;
			case "List":      result = listFromElement(e); break;
			case "Reference": result = referenceFromElement(e); break;
			case "Array":     result = arrayFromElement(e); break;
			// SetType, etc.
			default: return null;
		}


		if (result != null) {
			this.minispecIndex.put(id, result);
		}

		return result;
	}



	protected Model modelFromElement(Element e) {
		return new Model();
	}

	protected Entity entityFromElement(Element e) {
		Entity entity = new Entity();
		entity.setName(e.getAttribute("name"));
		this.nameToEntityIndex.put(entity.getName(), entity);


		String modelId = e.getAttribute("model");
		MinispecElement m = getOrCreateMinispecElement(modelId);
		if (m instanceof Model) {
			((Model) m).getEntities().add(entity);
		}
		return entity;
	}

	protected Attribute attributeFromElement(Element e) {
		Attribute attr = new Attribute();
		attr.setName(e.getAttribute("name"));


		String typeRaw = e.getAttribute("type");
		attr.setType(resolveTypeOrRef(typeRaw));


		String entityId = e.getAttribute("entity");
		MinispecElement parent = getOrCreateMinispecElement(entityId);

		if (parent instanceof Entity) {
			((Entity)parent).getAttributes().add(attr);
		}

		return attr;
	}

	protected ListType listFromElement(Element e) {
		String typeRef = e.getAttribute("typeElement");
		return new ListType(resolveTypeOrRef(typeRef));
	}

	protected ArrayType arrayFromElement(Element e) {
		String typeRef = e.getAttribute("typeElement");
		ArrayType arr = new ArrayType(resolveTypeOrRef(typeRef));
		if(e.hasAttribute("size")) {
			try { arr.setSizeMax(Integer.parseInt(e.getAttribute("size"))); } catch(Exception ex){}
		}
		return arr;
	}

	protected ReferenceType referenceFromElement(Element e) {
		String targetName = e.getAttribute("name");
		ReferenceType ref = new ReferenceType(targetName);

		this.pendingReferences.add(ref);
		return ref;
	}



	private Type resolveTypeOrRef(String raw) {
		if (raw.startsWith("#")) {
			return (Type) getOrCreateMinispecElement(raw);
		} else {

			return new SimpleType(raw);
		}
	}

	private void resolveReferences() {
		System.out.println("-> Vérification des " + pendingReferences.size() + " références...");
		for (ReferenceType ref : pendingReferences) {
			Entity target = nameToEntityIndex.get(ref.getName());
			if (target != null) {
				ref.resolve(target);
			} else {
				System.err.println(" [ERREUR] Info: Référence inconnue : " + ref.getName());
			}
		}
	}

	private String findModelIdInChildren(Element root) {
		NodeList nodes = root.getChildNodes();
		for(int i=0; i<nodes.getLength(); i++) {
			if(nodes.item(i) instanceof Element) {
				Element e = (Element) nodes.item(i);
				if("Model".equals(e.getTagName())) return e.getAttribute("id");
			}
		}
		return "";
	}

	public Model getModelFromFile(File file) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			return getModelFromDocument(dbf.newDocumentBuilder().parse(file));
		} catch (Exception e) { e.printStackTrace(); }
		return null;
	}
}