package visitor;

import metaModel.*;
import java.util.Set;
import java.util.TreeSet;

public class JavaVisitor extends Visitor {

    private StringBuilder classFields = new StringBuilder();
    private StringBuilder classMethods = new StringBuilder();
    private String className = "";


    private String packageName;

    private Set<String> imports = new TreeSet<>();

    private String currentTypeString = "";
    private String currentInitString = "";


    private String currentInnerTypeString = "";

    public JavaVisitor() {
        this("", "");
    }

    public JavaVisitor(String packageName) {
        this.packageName = packageName;
    }


    private JavaVisitor(String packageName, String dummy) {
        this.packageName = packageName;
    }

    @Override
    public void visitModel(Model e) {
        for (Entity n : e.getEntities()) {
            n.accept(this);
        }
    }

    @Override
    public void visitEntity(Entity e) {
        classFields.setLength(0);
        classMethods.setLength(0);
        imports.clear();
        className = e.getName();

        for (Attribute n : e.getAttributes()) {
            n.accept(this);
        }

        classMethods.insert(0, "\n    public " + className + "() { }\n");
    }

    @Override
    public void visitAttribute(Attribute e) {

        e.getType().accept(this);

        String type = this.currentTypeString;
        String init = this.currentInitString;
        String name = e.getName();
        String capName = capitalize(name);


        classFields.append("    public ").append(type).append(" ").append(name).append(init).append(";\n");


        classMethods.append("\n    public ").append(type).append(" get").append(capName).append("() {\n")
                .append("        return this.").append(name).append(";\n")
                .append("    }\n");


        classMethods.append("\n    public void set").append(capName).append("(")
                .append(type).append(" ").append(name).append(") {\n")
                .append("        this.").append(name).append(" = ").append(name).append(";\n")
                .append("    }\n");


        if (e.getType() instanceof ListType || e.getType() instanceof SetType) {
            String innerType = this.currentInnerTypeString;


            classMethods.append("\n    public void add").append(capName).append("Element(")
                    .append(innerType).append(" element) {\n")
                    .append("        this.").append(name).append(".add(element);\n")
                    .append("    }\n");


            classMethods.append("\n    public void remove").append(capName).append("Element(")
                    .append(innerType).append(" element) {\n")
                    .append("        this.").append(name).append(".remove(element);\n")
                    .append("    }\n");
        }
    }

    @Override
    public void visitReferenceType(ReferenceType t) {
        if (t.isResolved()) {
            String targetName = t.getName();
            this.currentTypeString = targetName;

            if (!this.packageName.isEmpty()) {
                imports.add(this.packageName + "." + targetName);
            }
        } else {

            this.currentTypeString = t.getName();
        }
        this.currentInitString = "";
    }

    @Override
    public void visitSimpleType(SimpleType t) {
        this.currentTypeString = t.getName();
        this.currentInitString = "";
    }

    @Override
    public void visitListType(ListType t) {
        t.getElementType().accept(this);
        String innerType = this.currentTypeString;


        this.currentInnerTypeString = innerType;

        imports.add("java.util.List");
        imports.add("java.util.ArrayList");

        this.currentTypeString = "List<" + innerType + ">";
        this.currentInitString = " = new ArrayList<>()";
    }

    @Override
    public void visitSetType(SetType t) {
        t.getElementType().accept(this);
        String innerType = this.currentTypeString;

        this.currentInnerTypeString = innerType;

        imports.add("java.util.Set");
        imports.add("java.util.HashSet");

        this.currentTypeString = "Set<" + innerType + ">";
        this.currentInitString = " = new HashSet<>()";
    }

    @Override
    public void visitArrayType(ArrayType t) {
        t.getElementType().accept(this);
        String innerType = this.currentTypeString;

        this.currentTypeString = innerType + "[]";
        int size = (t.getSizeMax() > 0) ? t.getSizeMax() : Math.max(t.getSizeMin(), 0);
        this.currentInitString = " = new " + innerType + "[" + size + "]";
    }

    public String getResult() {
        if (className.isEmpty()) return "";

        StringBuilder result = new StringBuilder();

        if (!packageName.isEmpty()) {
            result.append("package ").append(packageName).append(";\n\n");
        }

        String selfImport = packageName + "." + className;

        for (String imp : imports) {
            if (!imp.equals(selfImport)) {
                result.append("import ").append(imp).append(";\n");
            }
        }

        if (!imports.isEmpty()) {
            result.append("\n");
        }

        result.append("public class ").append(className).append(" {\n");
        result.append("\n");

        result.append(classFields.toString());
        result.append(classMethods.toString());

        result.append("}\n");

        return result.toString();
    }


    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}