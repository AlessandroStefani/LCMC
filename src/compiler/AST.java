package compiler;

import java.util.*;

import compiler.lib.*;

public class AST {

    public static class ProgLetInNode extends Node {
        final List<DecNode> declist;
        final Node exp;

        ProgLetInNode(List<DecNode> d, Node e) {
            declist = Collections.unmodifiableList(d);
            exp = e;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class ProgNode extends Node {
        final Node exp;

        ProgNode(Node e) {
            exp = e;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class FunNode extends DecNode {
        final String id;
        final TypeNode retType;
        final List<ParNode> parlist;
        final List<DecNode> declist;
        final Node exp;

        FunNode(String i, TypeNode rt, List<ParNode> pl, List<DecNode> dl, Node e) {
            id = i;
            retType = rt;
            parlist = Collections.unmodifiableList(pl);
            declist = Collections.unmodifiableList(dl);
            exp = e;
        }

        //void setType(TypeNode t) {type = t;}

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class ParNode extends DecNode {
        final String id;

        ParNode(String i, TypeNode t) {
            id = i;
            type = t;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class VarNode extends DecNode {
        final String id;
        final Node exp;

        VarNode(String i, TypeNode t, Node v) {
            id = i;
            type = t;
            exp = v;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class PrintNode extends Node {
        final Node exp;

        PrintNode(Node e) {
            exp = e;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class IfNode extends Node {
        final Node cond;
        final Node th;
        final Node el;

        IfNode(Node c, Node t, Node e) {
            cond = c;
            th = t;
            el = e;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class EqualNode extends Node {
        final Node left;
        final Node right;

        EqualNode(Node l, Node r) {
            left = l;
            right = r;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class LessEqualNode extends Node {
        final Node left;
        final Node right;

        LessEqualNode(Node l, Node r) {
            left = l;
            right = r;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class GreaterEqualNode extends Node {
        final Node left;
        final Node right;

        GreaterEqualNode(Node l, Node r) {
            left = l;
            right = r;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class AndNode extends Node {
        final Node left;
        final Node right;

        AndNode(Node l, Node r) {
            left = l;
            right = r;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class OrNode extends Node {
        final Node left;
        final Node right;

        OrNode(Node l, Node r) {
            left = l;
            right = r;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class NotNode extends Node {
        final Node exp;

        NotNode(Node e) {
            exp = e;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class TimesNode extends Node {
        final Node left;
        final Node right;

        TimesNode(Node l, Node r) {
            left = l;
            right = r;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class DivNode extends Node {
        final Node left;
        final Node right;

        DivNode(Node l, Node r) {
            left = l;
            right = r;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class PlusNode extends Node {
        final Node left;
        final Node right;

        PlusNode(Node l, Node r) {
            left = l;
            right = r;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class MinusNode extends Node {
        final Node left;
        final Node right;

        MinusNode(Node l, Node r) {
            left = l;
            right = r;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class CallNode extends Node {
        final String id;
        final List<Node> arglist;
        STentry entry;
        int nl;

        CallNode(String i, List<Node> p) {
            id = i;
            arglist = Collections.unmodifiableList(p);
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class IdNode extends Node {
        final String id;
        STentry entry;
        int nl;

        IdNode(String i) {
            id = i;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class BoolNode extends Node {
        final Boolean val;

        BoolNode(boolean n) {
            val = n;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class IntNode extends Node {
        final Integer val;

        IntNode(Integer n) {
            val = n;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class ArrowTypeNode extends TypeNode {
        final List<TypeNode> parlist;
        final TypeNode ret;

        ArrowTypeNode(List<TypeNode> p, TypeNode r) {
            parlist = Collections.unmodifiableList(p);
            ret = r;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class BoolTypeNode extends TypeNode {

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class IntTypeNode extends TypeNode {

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    /**
     * valore null
     */
    public static class EmptyNode extends Node {

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    /**
     * tipo null
     */
    public static class EmptyTypeNode extends TypeNode {

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class FieldNode extends DecNode {

        final String id;
		int offset = -1; //se non si eredita, -1. altrimenti settato in base a classtypenode

        FieldNode(String i, TypeNode t) {
            id = i;
            type = t;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }

    }

    /**
     * MethodNode
     */
    public static class MethodNode extends DecNode {
        final String id;
        final TypeNode retType;
        final List<ParNode> parlist;
        final List<DecNode> declist;
        final Node exp;
		String label;//etichetta per indirizzo
		int offset = 0;//se non si eredita, 0. altrimenti settato in base a classtypenode

        MethodNode(String i, TypeNode rt, List<ParNode> pl, List<DecNode> dl, Node e, TypeNode t) {
            id = i;
            retType = rt;
            parlist = Collections.unmodifiableList(pl);
            declist = Collections.unmodifiableList(dl);
            exp = e;
            super.type = t;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }

    }

    /**
     * contiene l'ID della classe come campo
     */
    public static class RefTypeNode extends TypeNode {
        final String id;

        RefTypeNode(String id) {
            this.id = id;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    /**
     *
     */
    public static class ClassNode extends DecNode {
        final String id;
        final List<MethodNode> methods;
        final List<FieldNode> fields;
        final String superId; //nulla se non c'è
        STentry superEntry;

        public ClassNode(String id, List<MethodNode> methods, List<FieldNode> fields, String superId, TypeNode t) {
            this.id = id;
            this.methods = Collections.unmodifiableList(methods);
            this.fields = Collections.unmodifiableList(fields);
            this.superId = superId;
            super.type = t;
        }


        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class ClassTypeNode extends TypeNode {
        final List<TypeNode> allFields;
        final List<ArrowTypeNode> allMethods;

        //se non si eredita
        ClassTypeNode(final List<TypeNode> fields, final List<ArrowTypeNode> methods) {
            this.allFields = new ArrayList<>(fields);
            this.allMethods = new ArrayList<>(methods);
        }

        //se si eredita
        ClassTypeNode(final ClassTypeNode parent) {
            this(parent.allFields, parent.allMethods);
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }


    public static class NewNode extends Node {
        final String className;//nome classe
        final List<Node> argumentList;//argomenti del costruttore
        STentry classEntry;

        NewNode(String id, final List<Node> args) {
            this.className = id;
            this.argumentList = Collections.unmodifiableList(args);
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

    public static class ClassCallNode extends Node {
        final String classId;//classe
        final String methodId;//metodo
        final List<Node> argumentList;//argomenti del metodo
        STentry classEntry;
        STentry methodEntry;
        int nestingLevel;

        ClassCallNode(final String classId, final String methodId, final List<Node> args) {
            this.classId = classId;
            this.methodId = methodId;
            this.argumentList = Collections.unmodifiableList(args);
            this.nestingLevel = 0;
        }

        @Override
        public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
            return visitor.visitNode(this);
        }
    }

}