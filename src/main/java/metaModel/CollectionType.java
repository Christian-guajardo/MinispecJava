package metaModel;


public abstract class CollectionType extends Type {
    protected Type elementType;
    protected int sizeMax = -1;
    protected int sizeMin = 0;

    public CollectionType(Type elementType) {
        this.elementType = elementType;
    }

    public Type getElementType() { return elementType; }

    public int getSizeMax() { return sizeMax; }
    public void setSizeMax(int sizeMax) { this.sizeMax = sizeMax; }

    public int getSizeMin() { return sizeMin; }
    public void setSizeMin(int sizeMin) { this.sizeMin = sizeMin; }

    // On ne met pas accept() ici, car chaque sous-classe doit appeler sa propre méthode visitor
}