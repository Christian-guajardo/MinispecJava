package metaModel;

public class ArrayType extends CollectionType {
    public ArrayType(Type elementType) {
        super(elementType);
    }

    @Override
    public void accept(Visitor v) {
        v.visitArrayType(this);
    }
}