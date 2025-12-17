package metaModel;

public class SetType extends CollectionType {
    public SetType(Type elementType) {
        super(elementType);
    }

    @Override
    public void accept(Visitor v) {
        v.visitSetType(this);
    }
}