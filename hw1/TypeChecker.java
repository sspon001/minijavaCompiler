import syntaxtree.Goal ;

public class TypeChecker{
    GJDepthFirst1 firstPass ;
    GJDepthFirst2 secondPass ;
    Goal goal ;
    public TypeChecker(Goal goal){
        firstPass = new GJDepthFirst1() ;
        secondPass = new GJDepthFirst2() ;
        this.goal = goal ;
    }
    public String check() {
        Object symbolTable = goal.accept(firstPass, null) ;
        return goal.accept(secondPass, symbolTable).toString() ;
    }
}