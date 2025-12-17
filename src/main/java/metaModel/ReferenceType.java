package metaModel;



public class ReferenceType extends Type {
    private String targetName;
    private Entity resolvedEntity; // Null = Unresolved

    public ReferenceType(String targetName) {
        this.targetName = targetName;
        this.resolvedEntity = null; // Par défaut, c'est Unresolved
    }

    // Cette méthode sera appelée lors du "2ème passage" de résolution
    public void resolve(Entity e) {
        this.resolvedEntity = e;
    }

    public boolean isResolved() {
        return resolvedEntity != null;
    }

    public String getName() {
        return targetName; // Retourne le nom de la cible (ex: "Satellite")
    }

    public Entity getEntity() {
        return resolvedEntity;
    }

    @Override
    public void accept(Visitor v) {
        v.visitReferenceType(this);
    }
}