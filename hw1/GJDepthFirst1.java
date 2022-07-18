import java.util.HashMap ;
import java.util.LinkedList ;
import java.util.Set ;

import syntaxtree.*;
import visitor.GJDepthFirst;

public class GJDepthFirst1<R,A> extends GJDepthFirst<R,A> {
    int methodCount = 0 ;
    HashMap<String, symbolTable<R>> MainTable = new HashMap<>() ;
    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    public R visit(Goal n, A argu) {
        R _ret=null;
        String className = n.f0.accept(this, argu).toString() ;
        //System.out.println(className + " = [") ;
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
       // System.out.println("]") ;
      /*  System.out.println("Class A signatures : " + MainTable.get("A").signatures) ;
        System.out.println("Class A fields : " + MainTable.get("A").fieldMap) ;
        System.out.println("run() fields : " + MainTable.get("A").methodMap.get("run").fieldMap) ;
        System.out.println("run2() fields : " + MainTable.get("A").methodMap.get("run2").fieldMap) ;
        System.out.println("") ;
        System.out.println("Class B signatures : " + MainTable.get("B").signatures) ;
        System.out.println("Class B fields : " + MainTable.get("B").fieldMap) ;
        System.out.println("run3() fields : " + MainTable.get("B").methodMap.get("run3").fieldMap) ;
        System.out.println("run3() parent : " + MainTable.get("B").methodMap.get("run3").parent) ;
        System.out.println("Class B parent : " + MainTable.get("B").parent) ;*/
        Set<String> keys = MainTable.keySet() ;
        /*for(String classNames : keys){
            Set<String> methods = MainTable.get(classNames).methodMap.keySet() ;
            System.out.println("Class " + MainTable.get(classNames).name) ;
            System.out.println("   -> fields") ;
            System.out.println("        " + MainTable.get(classNames).fieldMap) ;
            System.out.println("   -> methods") ;
            int i = 0 ;
            for(String methodNames : methods){
                System.out.println("        m" + i++ + ": " + MainTable.get(classNames).signatures.get(methodNames)) ;
                System.out.println("          " + MainTable.get(classNames).methodMap.get(methodNames).fieldMap) ;
            }
        }*/

        return (R) MainTable;
    }
    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    public R visit(MainClass n, A argu) {
        R _ret=null;
        symbolTable s = symbolTable.newTable() ;
        n.f0.accept(this, (A) s);
        String className = n.f1.accept(this, argu).toString();
        s.name = className ;
        n.f2.accept(this, (A) s);
        n.f3.accept(this, (A) s);
        n.f4.accept(this, (A) s);
        n.f5.accept(this, (A) s);
        n.f6.accept(this, (A) s);
        n.f7.accept(this, (A) s);
        n.f8.accept(this, (A) s);
        n.f9.accept(this, (A) s);
        n.f10.accept(this, (A) s);
        String main_param_name = (String) n.f11.accept(this, (A) s) ;
        n.f12.accept(this, (A) s);
        n.f13.accept(this, (A) s);
        n.f14.accept(this, (A) s);
        n.f15.accept(this, (A) s);
        n.f16.accept(this, (A) s);
        n.f17.accept(this, (A) s);
        LinkedList<R> signature = new LinkedList<>() ;
        signature.add((R) (String) "String[]") ;         // parameter type of main function
        signature.add((R) (String) "void") ;             // return type of main function
        s.signatures.put("main", signature) ;            // add main method signature to symbol table of Main class
        symbolTable ss = symbolTable.newTable() ;                    // create new symbolTable for main method
        ss.fieldMap.put(main_param_name, "String[]") ;   // add the passed in parameter to symbol table
        s.methodMap.put("main", ss) ;                    // add the main method symbol table to main class symbol table
        MainTable.put(s.name, s) ;
        return (R) className ;
    }

    /**
     * f0 -> ClassDeclaration()
     *       | ClassExtendsDeclaration()
     */
    public R visit(TypeDeclaration n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    public R visit(ClassDeclaration n, A argu){
        R _ret=null;
        symbolTable s = symbolTable.newTable() ;              // symbolTable for this class
        argu = (A) s ;                            // pass this table to all nodes in this class
        methodCount = 0 ;
        n.f0.accept(this, argu);
        String className = n.f1.accept(this, argu).toString() ;
        if(MainTable.get(className) != null){
            //System.out.println("Duplicate class name") ;
            System.out.println("Type error") ;
            System.exit(0) ;
        }
        s.name = className ;
        MainTable.put(className, s) ;           // add this class' table to the MainTable
        //System.out.println("    " + className + " ->") ;
        //System.out.println("        superclass: _") ;
        n.f2.accept(this, argu);
        //System.out.println("        fields ->") ;
        n.f3.accept(this, argu) ;
        //System.out.println("        methods ->") ;
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        return _ret;
    }
    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    public R visit(ClassExtendsDeclaration n, A argu) {
        R _ret=null;
        symbolTable s = symbolTable.newTable() ;
        argu = (A) s ;
        methodCount = 0 ;
        n.f0.accept(this, argu);
        String className = n.f1.accept(this, argu).toString();
        if(MainTable.get(className) != null){
            //System.out.println("Duplicate class name") ;
            System.out.println("Type error") ;
            System.exit(0) ;
        }
        s.name = className ;
        MainTable.put(className, s) ;
        n.f2.accept(this, argu);
        String superClassName = n.f3.accept(this, argu).toString();
        s.parent = superClassName ;
       // System.out.println("    " + className + " ->") ;
        //System.out.println("        superclass: " + superClassName) ;
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
       // System.out.println("        methods ->") ;
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        return _ret;
    }
    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    public boolean equal(LinkedList<R> a, LinkedList<R> b){  // linked list equality helper function
        if(a.size() == b.size()) {
            for (int i = 0; i < a.size(); i++)
                if (!((String) a.get(i) == (String) b.get(i))) return false;
            return true;                                     // if every element is the same then return true
        }
        return false ;                                       // else false
    }
    public R visit(MethodDeclaration n, A argu) {
        R _ret=null;
        symbolTable s = symbolTable.newTable() ;
        Set<String> keys = MainTable.keySet() ;
        for(String key : keys){
            if(MainTable.get(key) == (symbolTable) argu){       // find class with matching table to determine parent
                s.parent = key ;
                break ;
            }
        }
        n.f0.accept(this, (A) s);
        String returnType = n.f1.accept(this,(A) s).toString();
        String methodName = n.f2.accept(this, (A) s).toString() ;
        s.name = methodName ;
        n.f3.accept(this, (A) s);
        R formalParameters = (R) n.f4.accept(this, (A) s);
        if(formalParameters == null){                           // if no parameters, create empty linked list
            formalParameters = (R) new LinkedList<>() ;
        }
        ((LinkedList<R>) formalParameters).add((R) returnType) ;
        if(((symbolTable) argu).signatures.get(methodName) != null){
            if(equal(((LinkedList<R>) formalParameters), (((symbolTable<R>) argu).signatures.get(methodName)))){
                //System.out.println("Parameters don't match method signature") ;
                System.out.println("Type error") ;
                System.exit(0) ;
            }
        }
        n.f5.accept(this, (A) s);
        n.f6.accept(this,(A) s);
        n.f7.accept(this, (A) s);
        n.f8.accept(this, (A) s);
        n.f9.accept(this, (A) s);
        n.f10.accept(this, (A) s);
        n.f11.accept(this,(A) s);
        n.f12.accept(this, (A) s);                                                           // argu = table of the current class
        ((symbolTable) argu).signatures.put(methodName, (LinkedList<R>) formalParameters) ;     // add signature of this method to current class table
        ((symbolTable) argu).methodMap.put(methodName, s) ;                                     // add this method's symbol table to methodMap of class symbol table
       // System.out.println("            m" + methodCount++ + " : " +  ((symbolTable) argu).signatures.get(methodName)) ;
        return _ret;
    }
    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public R visit(VarDeclaration n, A argu) {
        R _ret=null;
        symbolTable s = (symbolTable) argu ;                    // use the symbol table that was passed in as 'argu'
        String varType = (String) n.f0.accept(this, argu);
        String id = (String) n.f1.accept(this, argu);
        if(s.fieldMap.get(id) != null){
            //System.out.println("Duplicate variable name") ;
            System.out.println("Type error") ;
            System.exit(0) ;
        }
        s.fieldMap.put(id, varType) ;
        String varDec = ((String)("            " + id + " : " + varType));
        n.f2.accept(this, argu);
        //System.out.println(varDec) ;
        return _ret ;
    }
    /**
     * f0 -> FormalParameter()
     * f1 -> ( FormalParameterRest() )*
     */
    public R visit(FormalParameterList n, A argu) {
        R _ret = null ;
        LinkedList<R> paramList = new LinkedList<>() ;
        n.f0.accept(this, (A) paramList) ;
        n.f1.accept(this, (A) paramList) ;
        return (R) paramList ;

    }
    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    public R visit(FormalParameter n, A argu) {
        R _ret=null;
        String paramType = n.f0.accept(this, argu).toString() ;
        String paramID = n.f1.accept(this, argu).toString() ;
        LinkedList<R> ll = (LinkedList<R>) argu ;
        ll.add((R) paramType) ;
        return (R) ll ;
    }
    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    public R visit(FormalParameterRest n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }
    /**
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    public R visit(Type n, A argu) {
        return (R) n.f0.accept(this, argu).toString() ;
    }
    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public R visit(ArrayType n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return (R) "int[]" ;
    }
    /**
     * f0 -> "boolean"
     */
    public R visit(BooleanType n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        return (R) "boolean" ;
    }
    /**
     * f0 -> "int"
     */
    public R visit(IntegerType n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        return (R) "int" ;
    }
    /**
     * f0 -> <IDENTIFIER>
     */
    public R visit(Identifier n, A argu) {
        n.f0.accept(this, argu) ;
        return (R) n.f0.toString() ;
    }
}


