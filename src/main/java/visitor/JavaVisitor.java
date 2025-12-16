package visitor;

import metaModel.Attribute;
import metaModel.Entity;
import metaModel.Model;
import metaModel.Visitor;

public class JavaVisitor extends Visitor {

    private StringBuilder classFields = new StringBuilder();
    private StringBuilder classMethods = new StringBuilder();
    private String className = "";

    @Override
    public void visitModel(Model e) {
        for (Entity n: e.getEntities()){
            n.accept(this);
        }
    }

    @Override
    public void visitEntity(Entity e) {
        classFields.setLength(0);
        classMethods.setLength(0);
        className = e.getName();


        for (Attribute n: e.getAttributes()){
            n.accept(this);
        }

        classMethods.insert(0, "\n    public " + className + "() { }\n");
    }

    @Override
    public void visitAttribute(Attribute e) {
        String type = e.getType();
        String name = e.getName();


        classFields.append("    ").append(type).append(" ").append(name).append(" ;\n");


        classMethods.append("\n    public ").append(type).append(" get").append(name).append("() {\n")
                .append("        return this.").append(name).append(";\n")
                .append("    }\n");


        classMethods.append("\n    public void set").append(name).append("(")
                .append(type).append(" ").append(name).append(") {\n")
                .append("        this.").append(name).append(" = ").append(name).append(";\n")
                .append("    }\n");
    }


    public String getResult(){
        if (className.isEmpty()) return "";

        StringBuilder result = new StringBuilder();

        result.append("public class ").append(className).append(" {\n");
        result.append("\n");

        result.append(classFields.toString());

        result.append(classMethods.toString());

        result.append("}\n");

        return result.toString();
    }
}