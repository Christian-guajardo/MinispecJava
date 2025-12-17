package metaModel;
import java.util.ArrayList;
import java.util.List;


public class Model implements MinispecElement {

	private String name;
	private List<Entity> entities = new ArrayList<>();

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public List<Entity> getEntities() { return entities; }

	@Override
	public void accept(Visitor visitor) {
		visitor.visitModel(this);
	}
}