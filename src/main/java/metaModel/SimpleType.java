package metaModel;

public class SimpleType extends Type {
    private String name;

    public SimpleType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(Visitor v) {
        v.visitSimpleType(this);
    }
}