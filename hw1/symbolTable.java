import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class symbolTable<R> {
    String name;                                   // name of the class or method that this symbol table represents
    String parent;                                 // parent = superClass of class or class that method was defined in
    HashMap<String, String> fieldMap;              // fieldMap[variableName] = variableType
    HashMap<String, symbolTable<R>> methodMap;        // methodMap[methodName] = symbol table for that method
    HashMap<String, LinkedList<R>> signatures;     // signatures[methodName] = signature of that function

    public static symbolTable newTable() {                      // newTable helper function
        symbolTable s = new symbolTable();
        s.name = null;
        s.parent = null;
        s.fieldMap = new HashMap<>();
        s.methodMap = new HashMap<>();
        s.signatures = new HashMap<>();
        return s;
    }
}