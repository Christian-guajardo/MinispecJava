package XMLIO;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import metaModel.*;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLSerializer extends Visitor {
	List<Element> elements;
	Element root = null;
	String modelId;
	Integer counter;
	Document doc;
	
	public Document result() {
		return this.doc;
	}
	
	public XMLSerializer() throws ParserConfigurationException {
		this.elements = new ArrayList<>();
		this.counter = 0;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		this.doc = builder.newDocument();
		root = this.doc.createElement("Root");
		this.doc.appendChild(root);
	}
	
	private void addIdToElement(Element e) {
		this.counter++;
		Attr attr = this.doc.createAttribute("id");
		attr.setValue("#" + this.counter.toString());
		e.setAttributeNode(attr);
	}
	
	private void maybeUpdateRootFrom(Element e) {
		String rootId = this.root.getAttribute("model");
		if (rootId.isEmpty()) {
			Attr attr = this.doc.createAttribute("model");
			attr.setValue("#" + this.counter.toString());
			this.root.setAttributeNode(attr);
			modelId = attr.getValue();
		}
	}

	@Override
	public void visitEntity(Entity e) {
		Element elem = this.doc.createElement("Entity");
		this.addIdToElement(elem);
		Attr attr = doc.createAttribute("model");
		attr.setValue(modelId);
		elem.setAttributeNode(attr);

		this.root.appendChild(elem);
		attr = doc.createAttribute("name");
		attr.setValue(e.getName().toString());
		elem.setAttributeNode(attr);
		this.root.appendChild(elem);
		elements.add(elem);
		for (Attribute n : e.getAttributes()) {
			n.accept(this);
		}
	}
	
	@Override
	public void visitModel(Model e) {
		Element elem = this.doc.createElement("Model");
		this.addIdToElement(elem);
		this.maybeUpdateRootFrom(elem);
		this.root.appendChild(elem);
		elements.add(elem);
		for (Entity n : e.getEntities()) {
			n.accept(this);
		}
	}
	@Override
	public void visitAttribute(Attribute e) {
		Element elem = this.doc.createElement("Attribute");
		this.addIdToElement(elem);

		Attr attrName = doc.createAttribute("name");
		attrName.setValue(e.getName());
		elem.setAttributeNode(attrName);

		Attr attrType = doc.createAttribute("type");
		//attrType.setValue(e.getType());
		elem.setAttributeNode(attrType);

		if (!elements.isEmpty()) {
			Element parentEntity = elements.get(elements.size() - 1);
			Attr attrEntity = doc.createAttribute("entity");
			attrEntity.setValue(parentEntity.getAttribute("id"));
			elem.setAttributeNode(attrEntity);
		}

		this.root.appendChild(elem);
		elements.add(elem);
	}

	@Override
	public void visitSimpleType(SimpleType t) {

	}



	@Override
	public void visitListType(ListType t) {

	}

	@Override
	public void visitArrayType(ArrayType t) {

	}

	@Override
	public void visitSetType(SetType t) {

	}
}
