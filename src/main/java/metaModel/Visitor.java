package metaModel;

public abstract class Visitor {
	public abstract void visitModel(Model e);
	public abstract void visitEntity(Entity e);
	public abstract void visitAttribute(Attribute e);


	public abstract void visitSimpleType(SimpleType t); // Renommé



	public abstract void visitListType(ListType t);
	public abstract void visitArrayType(ArrayType t);
	public abstract void visitSetType(SetType t);

	public void visitReferenceType(ReferenceType referenceType) {
	}
}