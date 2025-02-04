package compiler;

import compiler.AST.*;
import compiler.lib.*;

public class TypeRels {

	// valuta se il tipo "a" e' <= al tipo "b", dove "a" e "b" sono tipi di base: IntTypeNode o BoolTypeNode
	public static boolean isSubtype(TypeNode a, TypeNode b) {
		return
				//fatto in classe
				a.getClass().equals(b.getClass()) ||
						((a instanceof BoolTypeNode) && (b instanceof IntTypeNode)) ||
						//controllo un tipo EmptyTypeNode sottotipo di un qualsiasi
						//tipo riferimento RefTypeNode
						((a instanceof EmptyTypeNode) && (b instanceof RefTypeNode));
	}

}
