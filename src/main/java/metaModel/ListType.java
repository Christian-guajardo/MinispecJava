package metaModel;

public class ListType extends CollectionType {
    public ListType(Type elementType) {
        super(elementType);
    }

    @Override
    public void accept(Visitor v) {
        v.visitListType(this);
    }
}