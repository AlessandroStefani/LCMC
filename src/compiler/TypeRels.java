package compiler;

import compiler.AST.*;
import compiler.lib.*;

import java.util.HashMap;
import java.util.Map;

public class TypeRels {

	public static Map<String,String> superType = new HashMap<>();

	// valuta se il tipo "a" e' <= al tipo "b", dove "a" e "b" sono tipi di base: IntTypeNode o BoolTypeNode
	public static boolean isSubtype(TypeNode a, TypeNode b) {
		return
				//fatto in classe
				a.getClass().equals(b.getClass()) ||
						((a instanceof BoolTypeNode) && (b instanceof IntTypeNode)) ||
						//– un tipo EmptyTypeNode sottotipo di un qualsiasi tipo riferimento RefTypeNode (ok)
						//controllo un tipo EmptyTypeNode sottotipo di un qualsiasi
						//tipo riferimento RefTypeNode
						((a instanceof EmptyTypeNode) && (b instanceof RefTypeNode)) ||
						//sono tutti e due RefTypeNode e nella mappa c'è una entry che dice che a è sottotipo di b
						//– un tipo riferimento RefTypeNode sottotipo di un altro in base alla funzione superType
							//• raggiungibilità applicandola multiple volte
						((a instanceof RefTypeNode first) &&
								((b instanceof RefTypeNode second)) &&
								superType.containsKey(first.id) &&
								checkClassSubtypeMultilevel(first, second)) ||
						//– un tipo funzionale ArrowTypeNode sottotipo di un altro (necessario per overriding tra metodi) in base
						//alla:
							//• relazione di co-varianza sul tipo di ritorno
							//• relazione di contro-varianza sul tipo dei parametri
						((a instanceof ArrowTypeNode method1) &&
								(b instanceof ArrowTypeNode method2) &&
									((method1.ret.equals(method2.ret)) || isSubtype(method1.ret, method2.ret)) &&
								checkEqualsMethodsParameters(method1, method2));
	}

	private static boolean checkEqualsMethodsParameters(ArrowTypeNode method1, ArrowTypeNode method2) {
		//numero parametri uguali
		if (method1.parlist.size() != method2.parlist.size()) return false;
		//parametro di 2 sottotipo di 1
		for (int i = 0; i < method1.parlist.size(); i++) {
			if (!(isSubtype(method2.parlist.get(i), method1.parlist.get(i)))) return false;
		}
		return true;
	}

	private static boolean checkClassSubtypeMultilevel(RefTypeNode a, RefTypeNode b) {
		String current = a.id;
		
		while (superType.containsKey(current)) {
			if (superType.get(current).equals(b.id)) {
				return true;
			}
			current = superType.get(current);
		}
		return false;
	}

	//TODO
	private TypeNode lowestCommonAncestor(TypeNode a, TypeNode b) {
		if (a instanceof EmptyTypeNode) return b;
		if (b instanceof EmptyTypeNode) return a;

		if (a instanceof IntTypeNode && b instanceof IntTypeNode) return new IntTypeNode();
		if (a instanceof BoolTypeNode && b instanceof BoolTypeNode) return new BoolTypeNode();

		if ((a instanceof IntTypeNode && b instanceof BoolTypeNode) ||
				(a instanceof BoolTypeNode && b instanceof IntTypeNode)) {
			return new IntTypeNode();
		}

		if (!(a instanceof RefTypeNode) || !(b instanceof RefTypeNode)) {
			return null;
		}

		RefTypeNode refA = (RefTypeNode) a;
		RefTypeNode refB = (RefTypeNode) b;

		if (isSubtype(refA, refB)) {
			return refB;
		}

		if (isSubtype(refB, refA)) {
			return refA;
		}

		String current = refA.id;
		while (superType.containsKey(current)) {
			String ancestor = superType.get(current);
			RefTypeNode ancestorNode = new RefTypeNode(ancestor);
			if (isSubtype(refB, ancestorNode)) {
				return ancestorNode;
			}
			current = ancestor;
		}

		return null;
	}

}
