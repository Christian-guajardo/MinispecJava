package prettyPrinter;

import metaModel.Attribute;
import metaModel.Entity;
import metaModel.Model;
import metaModel.Visitor;

public class PrettyPrinter extends Visitor {
	private String result = "";

	public String result() {
		return result;
	}

	@Override
	public void visitModel(Model e) {
		result = "model ;\n\n";
		for (Entity n : e.getEntities()) {
			n.accept(this);
		}
		result = result + "end model;\n";
	}

	@Override
	public void visitEntity(Entity e) {
		result = result + "entity " + e.getName() + " ;\n";

		for (Attribute n : e.getAttributes()) {
			n.accept(this);
		}
		result = result + "end_entity ;\n";
	}

	@Override
	public void visitAttribute(Attribute e) {
		result = result + "  " + e.getName() + " : " + e.getType() + " ;\n";
	}
}