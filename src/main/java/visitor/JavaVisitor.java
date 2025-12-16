package visitor;

import metaModel.Attribute;
import metaModel.Entity;
import metaModel.Model;
import metaModel.Visitor;

public class JavaVisitor extends Visitor {
    String resultBuffer;
    String methodeBuffer;
    @Override
    public void visitModel(Model e) {
        for (Entity n: e.getEntities()){
            n.accept(this);
        }
    }

    @Override
    public void visitEntity(Entity e) {
        resultBuffer += "public class "+e.getName()+"{\n";
        for (Attribute n: e.getAttributes()){
            n.accept(this);
        }

    }

    @Override
    public void visitAttribute(Attribute e) {
        resultBuffer += e.getType() +" "+e.getName();
        resultBuffer +=  "    public " + e.getName() + " get" + e.getName() + "() {\n" +
                         "        return " + e.getName() + ";\n" +
                         "    }\n\n" +
                         "    public void set" + e.getName() + "(" + e.getName() + " " + e.getName() + ") {\n" +
                         "        this." + e.getName() + " = " + e.getName() + ";\n" +
                         "    }\n\n";
    }

    public  String getResult(){
        return resultBuffer + methodeBuffer + "\n}";
    }
}
