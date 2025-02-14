package compiler;

import compiler.AST.*;
import compiler.lib.*;
import compiler.exc.*;

import java.util.ArrayList;
import java.util.List;

import static compiler.lib.FOOLlib.*;

public class CodeGenerationASTVisitor extends BaseASTVisitor<String, VoidException> {

    private final List<List<String>> dispatchTables = new ArrayList<>();

    CodeGenerationASTVisitor() {
    }

    CodeGenerationASTVisitor(boolean debug) {
        super(false, debug);
    } //enables print for debugging

    @Override
    public String visitNode(ProgLetInNode n) {
        if (print) printNode(n);
        String declCode = null;
        for (Node dec : n.declist) declCode = nlJoin(declCode, visit(dec));
        return nlJoin(
                "push 0",
                declCode, // generate code for declarations (allocation)
                visit(n.exp),
                "halt",
                getCode()
        );
    }

    @Override
    public String visitNode(ProgNode n) {
        if (print) printNode(n);
        return nlJoin(
                visit(n.exp),
                "halt"
        );
    }

    @Override
    public String visitNode(FunNode n) {
        if (print) printNode(n, n.id);
        String declCode = null, popDecl = null, popParl = null;
        for (Node dec : n.declist) {
            declCode = nlJoin(declCode, visit(dec));
            popDecl = nlJoin(popDecl, "pop");
        }
        for (int i = 0; i < n.parlist.size(); i++) popParl = nlJoin(popParl, "pop");
        String funl = freshFunLabel();
        putCode(
                nlJoin(
                        funl + ":",
                        "cfp", // set $fp to $sp value
                        "lra", // load $ra value
                        declCode, // generate code for local declarations (they use the new $fp!!!)
                        visit(n.exp), // generate code for function body expression
                        "stm", // set $tm to popped value (function result)
                        popDecl, // remove local declarations from stack
                        "sra", // set $ra to popped value
                        "pop", // remove Access Link from stack
                        popParl, // remove parameters from stack
                        "sfp", // set $fp to popped value (Control Link)
                        "ltm", // load $tm value (function result)
                        "lra", // load $ra value
                        "js"  // jump to to popped address
                )
        );
        return "push " + funl;
    }

    @Override
    public String visitNode(VarNode n) {
        if (print) printNode(n, n.id);
        return visit(n.exp);
    }

    @Override
    public String visitNode(PrintNode n) {
        if (print) printNode(n);
        return nlJoin(
                visit(n.exp),
                "print"
        );
    }

    @Override
    public String visitNode(IfNode n) {
        if (print) printNode(n);
        String l1 = freshLabel();
        String l2 = freshLabel();
        return nlJoin(
                visit(n.cond),
                "push 1",
                "beq " + l1,
                visit(n.el),
                "b " + l2,
                l1 + ":",
                visit(n.th),
                l2 + ":"
        );
    }

    @Override
    public String visitNode(EqualNode n) {
        if (print) printNode(n);
        String l1 = freshLabel();
        String l2 = freshLabel();
        return nlJoin(
                visit(n.left),
                visit(n.right),
                "beq " + l1,
                "push 0",
                "b " + l2,
                l1 + ":",
                "push 1",
                l2 + ":"
        );
    }

    @Override
    public String visitNode(LessEqualNode n) { //TODO
        if (print) printNode(n);
        String l1 = freshLabel();
        String l2 = freshLabel();
        return nlJoin(
                visit(n.left),
                visit(n.right),
                "bleq " + l1, //?
                "push 0",
                "b " + l2,
                l1 + ":",
                "push 1",
                l2 + ":"
        );
    }

    @Override
    public String visitNode(GreaterEqualNode n) { //TODO
        if (print) printNode(n);
        String l1 = freshLabel();
        String l2 = freshLabel();
        return nlJoin(
                visit(n.left),
                visit(n.right),
                "bleq " + l1, //?
                "push 1",
                "b " + l2,
                l1 + ":",
                "push 0",
                l2 + ":"
        );
    }

    @Override
    public String visitNode(AndNode n) {
        if (print) printNode(n);
        String falseLabel = freshLabel();
        String trueLabel = freshLabel();
        return nlJoin(
                visit(n.left),    //visito left
                "push " + 0,            //compara con 0
                "beq " + falseLabel, // è falso?
                visit(n.right),        //visito right
                "push " + 0,            //compara con 0
                "beq " + falseLabel, //è falso? salta al falselabel
                "push " + 1,            //tutto ok
                "b " + trueLabel,
                falseLabel + ":",
                "push " + 0,            //falso
                trueLabel + ":"
        );
    }

    @Override
    public String visitNode(OrNode n) {
        if (print) printNode(n);
        String falseLabel = freshLabel();
        String trueLabel = freshLabel();
        return nlJoin(
                visit(n.left),    //visito right
                "push " + 1,            //true?
                "beq " + trueLabel,    //se si, salto e finisco senno continuo
                visit(n.right),        //stessa roba di sopra
                "push " + 1,
                "beq " + trueLabel,    //tutti falsi
                "push " + 0,            // carico falso
                "b " + falseLabel,    //e salto al output falso
                trueLabel + ":",    //branch true
                "push " + 1,            //carico 1
                falseLabel + ":"
        );
    }

    @Override
    public String visitNode(NotNode n) {
        if (print) printNode(n);
        String falseLabel = freshLabel();
        String trueLabel = freshLabel();
        return nlJoin(
                visit(n.exp),
                "push " + 1,
                "beq " + falseLabel, //se è true(1) salto a false dove faccio diventare 0
                "push " + 1,        //sennò so che è 0, quindi deve uscire 1, carico 1
                "b " + trueLabel,    //e salto alla fine
                falseLabel + ":",
                "push " + 0,
                trueLabel + ":"
        );
    }

    @Override
    public String visitNode(TimesNode n) {
        if (print) printNode(n);
        return nlJoin(
                visit(n.left),
                visit(n.right),
                "mult"
        );
    }

    @Override
    public String visitNode(DivNode n) {
        if (print) printNode(n);
        return nlJoin(
                visit(n.left),
                visit(n.right),
                "div"
        );
    }

    @Override
    public String visitNode(PlusNode n) {
        if (print) printNode(n);
        return nlJoin(
                visit(n.left),
                visit(n.right),
                "add"
        );
    }

    @Override
    public String visitNode(MinusNode n) {
        if (print) printNode(n);
        return nlJoin(
                visit(n.left),
                visit(n.right),
                "sub"
        );
    }

    @Override
    public String visitNode(CallNode n) {
        if (print) printNode(n, n.id);
        String argCode = null, getAR = null;
        for (int i = n.arglist.size() - 1; i >= 0; i--) argCode = nlJoin(argCode, visit(n.arglist.get(i)));
        for (int i = 0; i < n.nl - n.entry.nl; i++) getAR = nlJoin(getAR, "lw");

        if (n.entry.offset >= 0) {//TODO
            return nlJoin(
                    "lfp", // load Control Link (pointer to frame of function "id" caller)
                    argCode, // generate code for argument expressions in reversed order
                    "lfp", getAR, // retrieve address of frame containing "id" declaration
                    // by following the static chain (of Access Links)
                    "stm", // set $tm to popped value (with the aim of duplicating top of stack)
                    "ltm", // load Access Link (pointer to frame of function "id" declaration)
                    "ltm", // duplicate top of stack
                    "push " + n.entry.offset, "add", // compute address of "id" declaration
                    "lw", // load address of "id" function
                    "js"  // jump to popped address (saving address of subsequent instruction in $ra)
            );

        }
        return nlJoin(
                "lfp", // load Control Link (pointer to frame of function "id" caller)
                argCode, // generate code for argument expressions in reversed order
                "lfp", getAR, // retrieve address of frame containing "id" declaration
                // by following the static chain (of Access Links)
                "stm", // set $tm to popped value (with the aim of duplicating top of stack)
                "ltm", // load Access Link (pointer to frame of function "id" declaration)
                "ltm", // duplicate top of stack
                "push " + n.entry.offset, "add", // compute address of "id" declaration
                "lw", // load address of "id" function
                "js"  // jump to popped address (saving address of subsequent instruction in $ra)
        );
    }

    @Override
    public String visitNode(IdNode n) {
        if (print) printNode(n, n.id);
        String getAR = null;
        for (int i = 0; i < n.nl - n.entry.nl; i++) getAR = nlJoin(getAR, "lw");
        return nlJoin(
                "lfp", getAR, // retrieve address of frame containing "id" declaration
                // by following the static chain (of Access Links)
                "push " + n.entry.offset, "add", // compute address of "id" declaration
                "lw" // load value of "id" variable
        );
    }

    @Override
    public String visitNode(BoolNode n) {
        if (print) printNode(n, n.val.toString());
        return "push " + (n.val ? 1 : 0);
    }

    @Override
    public String visitNode(IntNode n) {
        if (print) printNode(n, n.val.toString());
        return "push " + n.val;
    }

    @Override
    public String visitNode(EmptyNode n) throws VoidException {
        if (print) printNode(n);
        return "push " + -1;
    }

    @Override
    public String visitNode(MethodNode n) throws VoidException { //come funNode
        if (print) printNode(n);
        String label = freshFunLabel();
        n.label = label;

        String declCode = null, popDecl = null, popParl = null;
        for (Node dec : n.declist) {
            declCode = nlJoin(declCode, visit(dec));
            popDecl = nlJoin(popDecl, "pop");
        }
        for (int i = 0; i < n.parlist.size(); i++) popParl = nlJoin(popParl, "pop");


        putCode(
                nlJoin(
                        label + ":",
                        "cfp", // set $fp to $sp value
                        "lra", // load $ra value
                        declCode, // generate code for local declarations (they use the new $fp!!!)
                        visit(n.exp), // generate code for function body expression
                        "stm", // set $tm to popped value (function result)
                        popDecl, // remove local declarations from stack
                        "sra", // set $ra to popped value
                        "pop", // remove Access Link from stack
                        popParl, // remove parameters from stack
                        "sfp", // set $fp to popped value (Control Link)
                        "ltm", // load $tm value (function result)
                        "lra", // load $ra value
                        "js"  // jump to to popped address
                )
        );

        return null;
    }

    @Override
    public String visitNode(ClassNode n) throws VoidException {
        if (print) printNode(n, n.superId!=null? n.id + " Extends " + n.superId : n.id);

        List<String> dispatchTable = new ArrayList<>();
        if (n.superId != null) {
            //eredito, in teoria dovrebbe essere cosi questo
            final List<String> superDispatchTable = this.dispatchTables.get(-n.superEntry.offset - 2);
            dispatchTable.addAll(superDispatchTable);
        } else {
            this.dispatchTables.add(dispatchTable);
        }

        for (var method : n.methods) {//bisogna gestire l'eredita dei metodi, todo, in teoria si usa la .set(?)
            visit(method);
            //???
            String methodLabel = method.label; //me pare giusto
            int methodOffset = method.offset; //me pare giusto
            //dispatchTable.set(methodOffset, methodLabel);
            //dispatchTable.add(methodLabel);

            //direi così
            if (methodOffset>=dispatchTable.size()){
                dispatchTable.add(methodOffset, methodLabel);
            } else {
                //quando eredita il metodo va qui i guess
                //avevo ipotizzato, ma probabilmente è risolvibile durante la gen della SymbolTable: una classe C1 con il metodo A estende una classe C2 con i metodi B(offset 0) e A (offset 1):
                //  il metodo A della classe C1 potrebbe già avere offset 1, se è possibile fare un controllo sul nome dei metodi della superclass C2 e, trovando in entrambe i metodi A,
                //  possiamo già inserire che C1 ha il metodo B in offset 0 (ereditato) e A in offset 1 (override), se C1 avesse anche un metodo M, questo avrebbe offset 2 se non sbaglio.
                //  Il punto è che la questione è già risolta in SymbolTableASTVisitor, ma a questo punto sono un po' confuso sull superDispatchTable... boh scoprirò
                dispatchTable.set(methodOffset, methodLabel); //qui non si dovrebbe entrare mai se non sbaglio, ma per sicurezza c'è
            }
            //???
        }

        String code = null;

        for (String label : dispatchTable){
            code = nlJoin(
                    code,       //memorizza l'etichetta del metodo nel'heap
                    "push " + label,   //pusha l'etichetta del metodo
                    "lhp ",            //carico heap pointer
                    "sw ",             //memorizzo etichetta nel hp

                    "push " + 1,       //pusho 1 (per incrementare hp)
                    "lhp ",            //pusho heap pointer (per incrementare hp)
                    "add ",            //incremento hp

                    "shp "             //store hp
            );
        }

        return nlJoin(
                "lhp ",
                code
        );
    }

    @Override
    public String visitNode(ClassCallNode n) throws VoidException {
        if (print) printNode(n, n.classId + "." + n.methodId);
        String argCode = null, getAR = null;
        for (int i = n.argumentList.size() - 1; i >= 0; i--) argCode = nlJoin(argCode, visit(n.argumentList.get(i)));
        for (int i = 0; i < n.nestingLevel - n.methodEntry.nl; i++) getAR = nlJoin(getAR, "lw");
        return nlJoin(  //TODO
                "lfp", // load Control Link (pointer to frame of function "id" caller)
                argCode, // generate code for argument expressions in reversed order
                "lfp", getAR, // retrieve address of frame containing "id" declaration
                // by following the static chain (of Access Links)
                "stm", // set $tm to popped value (with the aim of duplicating top of stack)
                "ltm", // load Access Link (pointer to frame of function "id" declaration)
                "ltm", // duplicate top of stack
                "push " + n.entry.offset, "add", // compute address of "id" declaration
                "lw", // load address of "id" function
                "js"  // jump to popped address (saving address of subsequent instruction in $ra)
        );
    }

    @Override
    public String visitNode(NewNode n) throws VoidException { //TODO
        if (print) printNode(n, n.className);

        String argCode = null, getAR = null;
        for (int i = n.argumentList.size() - 1; i >= 0; i--) argCode = nlJoin(argCode, visit(n.argumentList.get(i)));
        for (int i = 0; i < n. - n.classEntry.nl; i++) getAR = nlJoin(getAR, "lw");

        return nlJoin(

        );
    }


}