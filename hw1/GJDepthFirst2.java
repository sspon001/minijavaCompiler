
import java.util.HashMap ;
import java.util.LinkedList ;
import java.util.Set ;

import com.sun.tools.javac.Main;
import syntaxtree.*;
import visitor.* ;

import visitor.GJDepthFirst;


public class GJDepthFirst2<R,A> extends GJDepthFirst<R,A> {
    HashMap<String, symbolTable<R>> MainTable = new HashMap<>() ;
    int expressionCount = 0 ;
    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    public R visit(Goal n, A argu) {
        R _ret=null;
        MainTable = (HashMap<String, symbolTable<R>>) argu ;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return (R) "Program type checked successfully" ;
    }
    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    public R visit(ClassDeclaration n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        String className = (String) n.f1.accept(this, argu);
        symbolTable<R> classTable = MainTable.get(className) ;
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, (A) classTable);
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
        n.f0.accept(this, argu);
        String className = (String) n.f1.accept(this, argu);
        symbolTable<R> classTable = MainTable.get(className) ;
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, (A) classTable);
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
    public R visit(MethodDeclaration n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String methodName = (String) n.f2.accept(this, argu);
        symbolTable<R> classTable = (symbolTable<R>) argu ;
        symbolTable<R> methodTable= classTable.methodMap.get(methodName) ;
        n.f3.accept(this, argu);
        LinkedList<R> paramIDs = (LinkedList<R>) n.f4.accept(this, argu);  // check if param list matches function signature
        methodTable.signatures.put("grungle", paramIDs) ;
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        n.f8.accept(this, (A) methodTable);
        n.f9.accept(this, argu);
        String expType = (String) n.f10.accept(this, argu);  // check if expression type matches function signature
        String expType2 = "" ;
        if(classTable.fieldMap.containsKey(expType)) expType2 = classTable.fieldMap.get(expType) ;
        if(methodTable.fieldMap.containsKey(expType)) expType2 = methodTable.fieldMap.get(expType) ;
        ////System.out.println("*expType : " + expType + " | expType2 : " + expType2) ;
        if(expType2 != "")  expType = expType2 ;
        if(expType != classTable.signatures.get(methodName).getLast()){
            //System.out.println("expression type : " + expType + " | return type : " + classTable.signatures.get(methodName).getLast()) ;
            //System.out.println("Return Expression type doesn't match signature in method " + methodName + " in class " + classTable.name) ;
            System.out.println("Type error") ;
            System.exit(1) ;
        }
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);
        return _ret;
    }
    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public R visit(VarDeclaration n, A argu) {
        R _ret=null;
        String type = (String) n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return (R) type ;
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
        n.f0.accept(this, argu) ;
        String paramID = n.f1.accept(this, argu).toString() ;
        LinkedList<R> ll = (LinkedList<R>) argu ;
        ll.add((R) paramID) ;
        return (R) ll ;
    }
    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    public R visit(FormalParameterRest n, A argu) {
        R _ret = null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }
    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public R visit(AssignmentStatement n, A argu) {
        symbolTable<R> methodTable = (symbolTable<R>) argu ;
        R _ret=null;                                            // check if identifier exists in symbol table
        String id = (String) n.f0.accept(this, argu);  // check if Identifier type is equal to Expression type
        String parent = methodTable.parent ;
        Set<String> declaredMethodVars = methodTable.fieldMap.keySet() ;
        //System.out.println(declaredMethodVars) ;
        Set<String> classVars = MainTable.get(parent).fieldMap.keySet() ;
        Set<String> classNames = MainTable.keySet() ;
        classNames.remove("main") ;
        LinkedList<R> parameterMethodVars = methodTable.signatures.get("grungle");
        if(parameterMethodVars != null){
            if((!declaredMethodVars.contains(id)) && (!classVars.contains(id)) && (!parameterMethodVars.contains(id))){
                //System.out.println(id + " was not declared (assign statement*)");
                System.out.println("Type error");
                System.exit(1);
            }
        }
        else{
            if ((!declaredMethodVars.contains(id)) && (!classVars.contains(id))) {
                //System.out.println(id + " was not declared (assign statement)");
                System.out.println("Type error");
                System.exit(1);
            }
        }
        String idType = (String) MainTable.get(parent).fieldMap.get(id) ;
        String idType2 = (String) methodTable.fieldMap.get(id) ;
        n.f1.accept(this, argu);
        String expType = (String) n.f2.accept(this, (A) methodTable);
        //System.out.println("expType : " + expType) ;
        if(expType != "int" && expType != "boolean" && expType != "int[]") {
            if (parameterMethodVars != null) {
                if ((!declaredMethodVars.contains(expType)) && (!classVars.contains(expType)) && (!parameterMethodVars.contains(expType)) && (!classNames.contains(expType))) {
                    //System.out.println(expType + " was not declared (assign statement*)");
                    System.out.println("Type error");
                    System.exit(1);
                }
            } else {
                if ((!declaredMethodVars.contains(expType)) && (!classVars.contains(expType)) && (!classNames.contains(expType))) {
                    //System.out.println(expType + " was not declared (assign statement)");
                    //System.out.println(methodTable.name) ;
                    System.out.println("Type error");
                    System.exit(1);
                }
            }
        }
        /*if(idType != expType){
            //System.out.println("Assignment Statement Type Error") ;
            //System.out.println("exp type : " + expType + " | " + "id type : " + idType+ " | " + "id type2 : " + idType2) ;
            System.out.println("Type error") ;
            System.exit(1) ;
        }*/
        n.f3.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    public R visit(ArrayAssignmentStatement n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu); // check if Expression is of type int
        n.f6.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    public R visit(IfStatement n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu); // check if Expression is of type bool
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public R visit(WhileStatement n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu); // check if Expression is boolen
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "//System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public R visit(PrintStatement n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);  // check if Expression is of type int
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> AndExpression()
     *       | CompareExpression()
     *       | PlusExpression()
     *       | MinusExpression()
     *       | TimesExpression()
     *       | ArrayLookup()
     *       | ArrayLength()
     *       | MessageSend()
     *       | PrimaryExpression()
     */
    public R visit(Expression n, A argu) {
        R _ret=null;
        String type = (String) n.f0.accept(this, argu);
        ////System.out.println("expression type = " + type) ;
        return (R) type ;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    public R visit(AndExpression n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu); // check if expressions are bools
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return (R) "boolean" ;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public R visit(CompareExpression n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu); // check if expressions are ints
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return (R) "boolean" ;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public R visit(PlusExpression n, A argu) {
        R _ret=null;
        String s = argu.getClass().getName() ;
        if(s == "symbolTable") {
            symbolTable<R> mt = (symbolTable<R>) argu;
            String t1 = (String) n.f0.accept(this, argu); // check if both expressions are ints
            n.f1.accept(this, argu);
            String t2 = (String) n.f2.accept(this, argu);
            if (t1 != "int" && t2 != "int") {
                t1 = mt.fieldMap.get(t1);
                t2 = mt.fieldMap.get(t2);
                if (t1 != "int" && t2 != "int") {
                    System.out.println("t1 : " + t1 + " | t2 : " + t2);
                    System.out.println("Type error");
                    System.exit(1);
                }
            }
        }
        return (R) "int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public R visit(MinusExpression n, A argu) {
        R _ret=null;
        String s = argu.getClass().getName() ;
        if(s == "symbolTable") {
            symbolTable<R> mt = (symbolTable<R>) argu;
            String t1 = (String) n.f0.accept(this, argu); // check if both expressions are ints
            n.f1.accept(this, argu);
            String t2 = (String) n.f2.accept(this, argu);
            if (t1 != "int" && t2 != "int") {
                t1 = mt.fieldMap.get(t1);
                t2 = mt.fieldMap.get(t2);
                if (t1 != "int" && t2 != "int") {
                    System.out.println("t1 : " + t1 + " | t2 : " + t2);
                    System.out.println("Type error");
                    System.exit(1);
                }
            }
        }
        return (R) "int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public R visit(TimesExpression n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);// check if both expressions are ints
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return (R) "int" ;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public R visit(ArrayLookup n, A argu) {
        R _ret=null;
        String expType = (String) n.f0.accept(this, argu);
        A methodTable =  argu ;
        String huh = methodTable.getClass().getName() ;
        if(huh == "symbolTable"){
            symbolTable<R> mt = (symbolTable<R>) methodTable ;
            String methodName = mt.name ;
            String className = mt.parent ;
            if(MainTable.get(className).fieldMap.containsKey(expType)){
                //System.out.println("was in maintable") ;
                if(MainTable.get(className).fieldMap.get(expType) != "int[]"){
                    //System.out.println("Array lookup but primary exp is not type int[] | " + MainTable.get(className).fieldMap.get(expType)) ;
                    System.out.println("Type error") ;
                    System.exit(1) ;
                }
            }
            else if(mt.fieldMap.containsKey(expType)){
                //System.out.println("was in method fields") ;
                if(mt.fieldMap.get(expType) != "int[]"){
                    //System.out.println("Array lookup but primary exp is not type int[] | " + expType) ;
                    System.out.println("Type error") ;
                    System.exit(1) ;
                }
            }
            else if(mt.signatures.get("grungle").contains(expType)){
                //System.out.println("was in method params") ;
                int i = mt.signatures.get("grungle").lastIndexOf(expType) ;
                if(mt.signatures.get("grungle").get(i) != "int[]"){
                    //System.out.println("Array lookup but primary exp is not type int[] | " + expType) ;
                    System.out.println("Type error") ;
                    System.exit(1) ;
                }
            }

        }

        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return (R) "int" ;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public R visit(ArrayLength n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return (R) "int" ;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public R visit(MessageSend n, A argu) {
        R _ret=null;
        A methodTable =  argu ;
        String huh = methodTable.getClass().getName() ;
        String id = (String) n.f2.accept(this, argu);
        String primaryExp = (String) n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f3.accept(this, argu);
        LinkedList<String> ll = (LinkedList<String>) n.f4.accept(this, argu) ;
        if(ll == null) expressionCount = 0 ;
        else expressionCount = ll.size() ;
        n.f5.accept(this, argu);
        String invokingMethodClassName ;
        if(huh == "symbolTable"){
            symbolTable<R> mt = (symbolTable<R>) methodTable ;
            String methodName = mt.name ;
            String className = mt.parent ;
            //System.out.println(primaryExp + "**") ;
            //System.out.println(MainTable.get(primaryExp)) ;
            if(MainTable.containsKey(primaryExp)){ // 'this' keyword will return classname directly
                invokingMethodClassName = primaryExp ;
            }
            else{
                invokingMethodClassName = mt.fieldMap.get(primaryExp) ;
                if(invokingMethodClassName == null) {
                    //System.out.println(mt.signatures.get("grungle"));
                    //System.out.println(primaryExp) ;
                    int i = mt.signatures.get("grungle").lastIndexOf(primaryExp) ;
                    //System.out.println("className : " + className + " | id : " + id + " | i = " + i) ;
                    //System.out.println(MainTable.get(className).signatures.get(methodName));
                    //System.out.println(mt.signatures.get("grungle"));
                    if(i >= 0) invokingMethodClassName = (String) MainTable.get(className).signatures.get(methodName).get(i) ;
                    else invokingMethodClassName = className ;
                }
            }
            //System.out.println("message sendy thing | invoked method name : " + id + " | in method " + methodName + " | invoking method class name : " + invokingMethodClassName) ;
            if(MainTable.get(invokingMethodClassName) == null){
                //System.out.println("*** wow what ") ;
            }
            int sigsize = MainTable.get(invokingMethodClassName).signatures.get(id).size() - 1 ;
            //System.out.println("sigsize : " + sigsize + " | expressionCount : " + expressionCount) ;
            if(sigsize != expressionCount){
                //System.out.println("Method call param count doesn't match method signature") ;
                System.out.println("Type error") ;
                System.exit(1) ;
            }
            return (R) MainTable.get(mt.parent).signatures.get(methodName).getLast() ;
        }

        return _ret;
    }

    /**
     * f0 -> Expression()
     * f1 -> ( ExpressionRest() )*
     */
    public R visit(ExpressionList n, A argu) {
        R _ret=null;
        expressionCount = 1 ;
        LinkedList<String> ll = new LinkedList<>() ;
        String huh = (String) n.f0.accept(this, (A) ll) ;
        //System.out.println(huh + "******") ;
        ll.add(huh) ;
        n.f1.accept(this, (A) ll);
        return (R) ll ;
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public R visit(ExpressionRest n, A argu) {
        R _ret=null;
        LinkedList<String> ll = (LinkedList<String>) argu ;
        n.f0.accept(this, argu);
        ll.add((String) n.f1.accept(this, argu));
        expressionCount++ ;
        return _ret;
    }

    /**
     * f0 -> IntegerLiteral()
     *       | TrueLiteral()
     *       | FalseLiteral()
     *       | Identifier()
     *       | ThisExpression()
     *       | ArrayAllocationExpression()
     *       | AllocationExpression()
     *       | NotExpression()
     *       | BracketExpression()
     */
    public R visit(PrimaryExpression n, A argu) {
        R _ret=null;
        String type = (String) n.f0.accept(this, argu);
        return (R) type ;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public R visit(IntegerLiteral n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        return (R) "int";
    }

    /**
     * f0 -> "true"
     */
    public R visit(TrueLiteral n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        return (R) "boolean";
    }

    /**
     * f0 -> "false"
     */
    public R visit(FalseLiteral n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        return (R) "boolean";
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public R visit(Identifier n, A argu) {
        n.f0.accept(this, argu) ;
        return (R) n.f0.toString() ;
    }

    /**
     * f0 -> "this"
     */
    public R visit(ThisExpression n, A argu) {
        R _ret=null;
        symbolTable<R> methodTable = (symbolTable<R>) argu ;
        String className = methodTable.parent ;
        n.f0.accept(this, argu);
        return (R) className ;
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public R visit(ArrayAllocationExpression n, A argu) {
        R _ret=null;
        symbolTable methodTable = (symbolTable) argu ;
        String methodName = methodTable.name ;
        String className = methodTable.parent ;
        symbolTable classTable = MainTable.get(className) ;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        String expType = (String) n.f3.accept(this, argu);
        String expType2 = "" ;
        LinkedList<R> parameterMethodVars = (LinkedList<R>) methodTable.signatures.get("grungle");
        if(classTable.fieldMap.containsKey(expType)) expType2 = (String) classTable.fieldMap.get(expType) ;
        if(methodTable.fieldMap.containsKey(expType)) expType2 = (String) methodTable.fieldMap.get(expType) ;
        LinkedList<R> parameterMethodTypes = (LinkedList<R>) classTable.signatures.get(methodName) ;
        if(parameterMethodVars.contains(expType)) expType2 = (String) parameterMethodTypes.get(parameterMethodVars.lastIndexOf(expType)) ;
        ////System.out.println("*expType : " + expType + " | expType2 : " + expType2) ;
        if(expType2 != "")  expType = expType2 ;
        if(expType != "int"){
            //System.out.println("Array allocation expression expected type 'int' but got type " + expType) ;
            System.out.println("Type error") ;
            System.exit(1) ;
        }
        n.f4.accept(this, argu);
        return (R) "int[]" ;
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public R visit(AllocationExpression n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        String id = (String) n.f1.accept(this, argu);
        if(!MainTable.containsKey(id)){
            //System.out.println("Undeclared Identifier " + id) ;
            System.out.println("Type error") ;
            System.exit(1) ;
        }
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return (R) id ;
    }

    /**
     * f0 -> "!"
     * f1 -> Expression()
     */
    public R visit(NotExpression n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return (R) "boolean";
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public R visit(BracketExpression n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

}
