import syntaxtree.Goal ;
import java.io.InputStream ;

public class Typecheck {
    public static void processStream(InputStream s){
        try{
            MiniJavaParser p = new MiniJavaParser(s) ;
            Goal root = p.Goal() ;
            TypeChecker typeChecker = new TypeChecker(root) ;
            System.out.println((String)typeChecker.check()) ;
        }
        catch (Exception e){
            System.out.println("Type error") ;
            e.printStackTrace() ;
        }
    }
    public static void main(String[] args) {
        processStream(System.in);
    }
}