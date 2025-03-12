package compiler;

import java.util.*;
import compiler.AST.*;
import compiler.exc.*;
import compiler.lib.*;

public class SymbolTableASTVisitor extends BaseASTVisitor<Void,VoidException> {
	
	private List<Map<String, STentry>> symTable = new ArrayList<>();
	private int nestingLevel=0; // current nesting level
	private int decOffset=-2; // counter for offset of local declarations at current nesting level 
	int stErrors=0;

	/**
	 * La ClassTable è usata per salvare le virtual table delle classi.
	 */
	private final Map<String, Map<String, STentry>> classTable = new HashMap<>(); //dove HashMap<String, STentry> è la VirtualTable della classe con id String

	SymbolTableASTVisitor() {}
	SymbolTableASTVisitor(boolean debug) {super(debug);} // enables print for debugging

	private STentry stLookup(String id) {
		int j = nestingLevel;
		STentry entry = null;
		while (j >= 0 && entry == null) 
			entry = symTable.get(j--).get(id);	
		return entry;
	}

	@Override
	public Void visitNode(ProgLetInNode n) {
		if (print) printNode(n);
		Map<String, STentry> hm = new HashMap<>();
		symTable.add(hm);
	    for (Node dec : n.declist) visit(dec);
		//se n.exp non c'è qui si rompe
		visit(n.exp);
		symTable.remove(0);
		return null;
	}

	@Override
	public Void visitNode(ProgNode n) {
		if (print) printNode(n);
		visit(n.exp);
		return null;
	}
	
	@Override
	public Void visitNode(FunNode n) {
		if (print) printNode(n);

		Map<String, STentry> hm = symTable.get(nestingLevel);
		List<TypeNode> parTypes = new ArrayList<>();  
		for (ParNode par : n.parlist) parTypes.add(par.getType()); 
		STentry entry = new STentry(nestingLevel, new ArrowTypeNode(parTypes,n.retType),decOffset--);
		//inserimento di ID nella symtable
		if (hm.put(n.id, entry) != null) {
			System.out.println("Fun id " + n.id + " at line "+ n.getLine() +" already declared");
			stErrors++;
		} 
		//creare una nuova hashmap per la symTable
		nestingLevel++;
		Map<String, STentry> hmn = new HashMap<>();
		symTable.add(hmn);
		int prevNLDecOffset=decOffset; // stores counter for offset of declarations at previous nesting level 
		decOffset=-2;
		
		int parOffset=1;
		for (ParNode par : n.parlist)
			if (hmn.put(par.id, new STentry(nestingLevel,par.getType(),parOffset++)) != null) {
				System.out.println("Par id " + par.id + " at line "+ n.getLine() +" already declared");
				stErrors++;
			}
		for (Node dec : n.declist) visit(dec);
		visit(n.exp);
		//rimuovere la hashmap corrente poiche' esco dallo scope               
		symTable.remove(nestingLevel--);
		decOffset=prevNLDecOffset; // restores counter for offset of declarations at previous nesting level

		return null;
	}
	
	@Override
	public Void visitNode(VarNode n) {
		if (print) printNode(n);
		visit(n.exp);
		Map<String, STentry> hm = symTable.get(nestingLevel);
		STentry entry = new STentry(nestingLevel,n.getType(),decOffset--);
		//inserimento di ID nella symtable
		if (hm.put(n.id, entry) != null) {
			System.out.println("Var id " + n.id + " at line "+ n.getLine() +" already declared");
			stErrors++;
		}
		return null;
	}

	@Override
	public Void visitNode(PrintNode n) {
		if (print) printNode(n);
		visit(n.exp);
		return null;
	}

	@Override
	public Void visitNode(IfNode n) {
		if (print) printNode(n);
		visit(n.cond);
		visit(n.th);
		visit(n.el);
		return null;
	}
	
	@Override
	public Void visitNode(EqualNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(LessEqualNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(GreaterEqualNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(AndNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(OrNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(NotNode n) {
		if (print) printNode(n);
		visit(n.exp);
		return null;
	}
	
	@Override
	public Void visitNode(TimesNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(DivNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}
	
	@Override
	public Void visitNode(PlusNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(MinusNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(CallNode n) {
		if (print) printNode(n);
		STentry entry = stLookup(n.id);
		if (entry == null) {
			System.out.println("Fun id " + n.id + " at line "+ n.getLine() + " not declared");
			stErrors++;
		} else {
			n.entry = entry;
			n.nl = nestingLevel;
		}
		for (Node arg : n.arglist) visit(arg);
		return null;
	}

	@Override
	public Void visitNode(IdNode n) {
		if (print) printNode(n);
		STentry entry = stLookup(n.id);
		if (entry == null) {
			System.out.println("Var or Par id " + n.id + " at line "+ n.getLine() + " not declared");
			stErrors++;
		} else {
			n.entry = entry;
			n.nl = nestingLevel;
		}
		return null;
	}

	@Override
	public Void visitNode(BoolNode n) {
		if (print) printNode(n, n.val.toString());
		return null;
	}

	@Override
	public Void visitNode(IntNode n) {
		if (print) printNode(n, n.val.toString());
		return null;
	}

	/**
	 *
	 * @param n EmptyTypeNode
	 * @return
	 * @throws VoidException
	 */
	@Override
	public Void visitNode(EmptyTypeNode n) throws VoidException {
		if (print) printNode(n);
		return null;
	}

	/**
	 *
	 * @param n EmptyNode
	 * @return
	 * @throws VoidException
	 */
	@Override
	public Void visitNode(EmptyNode n) throws VoidException {
		if (print) printNode(n);
		return null;
	}

	/**
	 *
	 * @param node FieldNode
	 * @return
	 * @throws VoidException
	 */
	@Override
	public Void visitNode(FieldNode node) throws VoidException {
		if (print) printNode(node);
		return null;
	}


	//TODO
	/**
	 *
	 * @param n
	 * @return
	 * @throws VoidException
	 */
	@Override
	public Void visitNode(ClassNode n) throws VoidException { //senza ereditarietà
		if (print) printNode(n);

		Map<String, STentry> hm = symTable.get(nestingLevel);
		HashSet<String> thisClassAllIds = new HashSet<>();

		//se è presente una superclasse reperisco la sua classtable
		//ed estrapolo le informazioni (tipi parametri e tipo ritorno metodi) da aggiungere a quelle
		//della classe corrente
		Map<String,STentry> superClassTable = new HashMap<>();
		List<TypeNode> superTypeFields = new ArrayList<>();
		List<ArrowTypeNode> superMethodsArrowTypes = new ArrayList<>();
		if (n.superId!=null) {
			n.superEntry = stLookup(n.superId);
			ClassTypeNode superClassTypeNode = (ClassTypeNode) n.superEntry.type;
			superTypeFields.addAll(superClassTypeNode.allFields);
			superMethodsArrowTypes.addAll(superClassTypeNode.allMethods);

			superClassTable = classTable.get(n.superId);
		}

		//classtype è aggiornato in seguito (seguo le slide del prof)
		STentry entry = new STentry(nestingLevel, new ClassTypeNode(superTypeFields,superMethodsArrowTypes),decOffset--);

		//creazione virtual table
		nestingLevel++;
        //copio la virtualTable della superclasse (se non esiste rimane vuota)
        Map<String, STentry> vt = new HashMap<>(superClassTable);
		symTable.add(vt);
		int prevNLDecOffset=decOffset; // stores counter for offset of declarations at previous nesting level
		decOffset=superMethodsArrowTypes.isEmpty()?0:superMethodsArrowTypes.size();

		int fieldOffset= superTypeFields.isEmpty() ?-1:(-superTypeFields.size()-1);

		for (FieldNode field : n.fields) {
			field.offset = fieldOffset--;
			//controllo che il campo non sia già presente all'interno di questa classe
			if(!thisClassAllIds.add(field.id)) {
				System.out.println("Field id " + field.id + " at line " + n.getLine() + " already declared");
				stErrors++;
			}
			//inserisco il campo in VirtualTable curando il fatto che se presente nella superclasse devo fare override
			//mantenedo il precedente offset
			if(vt.putIfAbsent(field.id,new STentry(nestingLevel, field.getType(),field.offset)) != null) {
				fieldOffset++;
				field.offset = vt.get(field.id).offset;
				vt.put(field.id,new STentry(nestingLevel, field.getType(),field.offset));
				((ClassTypeNode) entry.type).allFields.set(-field.offset-1, field.getType());
			} else {
				//Aggiorno ClassTypeNode della entry.
				((ClassTypeNode) entry.type).allFields.add(field.getType());
			}
		}

		for (MethodNode meth : n.methods) {
			meth.offset = decOffset++;
			//controllo che il metodo non sia già presente all'interno di questa classe
			if(!thisClassAllIds.add(meth.id)) {
				System.out.println("Method id " + meth.id + " at line " + n.getLine() + " already declared");
				stErrors++;
			}

			//inserisco i metodi in virtual table curando il fatto che se presente nella superclasse devo fare override
			//mantenedo il precedente offset
			List<TypeNode> methodParsTypes = new ArrayList<>();
			ArrowTypeNode methodType = new ArrowTypeNode(methodParsTypes,meth.retType);
			for (ParNode par : meth.parlist) methodParsTypes.add(par.getType());
			if(vt.putIfAbsent(meth.id, new STentry(nestingLevel, methodType, meth.offset))!=null){
				decOffset--;
				meth.offset = vt.get(meth.id).offset;
				vt.put(meth.id,new STentry(nestingLevel, methodType,meth.offset));
				((ClassTypeNode) entry.type).allMethods.set(meth.offset, methodType);
			} else {
				//Aggiorno allMethods di ClassTypeNode.
				((ClassTypeNode) entry.type).allMethods.add(methodType);
			}

			visit(meth);
		}


		//rimuovere la hashmap corrente poiche' esco dallo scope
		symTable.remove(nestingLevel--);
		decOffset=prevNLDecOffset; // restores counter for offset of declarations at previous nesting level

		//inserimento della classe nella symtable
		if (hm.put(n.id, entry) != null) {
			System.out.println("Class id " + n.id + " at line "+ n.getLine() +" already declared");
			stErrors++;
		}
		
		//inserimento ID nella classTable
		classTable.put(n.id, vt);
		return null;
	}

	/**
	 *
	 * @param n
	 * @return
	 * @throws VoidException
	 */
	@Override
	public Void visitNode(MethodNode n) throws VoidException { //dovrebbe essere molto simile a visit funNode
		if (print) printNode(n);

//		Map<String, STentry> hm = symTable.get(nestingLevel);
//		List<TypeNode> parTypes = new ArrayList<>();
//		for (ParNode par : n.parlist) parTypes.add(par.getType());
//		STentry entry = new STentry(nestingLevel, new ArrowTypeNode(parTypes,n.retType),decOffset); //decoffset è già incrementato prima della visit
//		//inserimento di ID nella symtable
//		if (hm.put(n.id, entry) != null) {
//			System.out.println("Method id " + n.id + " at line "+ n.getLine() +" already declared MethodNode");
//			stErrors++;
//		}

		//creare una nuova hashmap per la symTable
		nestingLevel++;
		Map<String, STentry> hmn = new HashMap<>();
		symTable.add(hmn);
		int prevNLDecOffset=decOffset; // stores counter for offset of declarations at previous nesting level
		decOffset=-2;

		int parOffset=1;
		for (ParNode par : n.parlist)
			if (hmn.put(par.id, new STentry(nestingLevel,par.getType(),parOffset++)) != null) {
				System.out.println("Par id " + par.id + " at line "+ n.getLine() +" already declared");
				stErrors++;
			}
		for (Node dec : n.declist) visit(dec);
		visit(n.exp);
		//rimuovere la hashmap corrente poiche' esco dallo scope
		symTable.remove(nestingLevel--);
		decOffset=prevNLDecOffset; // restores counter for offset of declarations at previous nesting level

		return null;
	}

	@Override
	public Void visitNode(NewNode n) {
		if (print) printNode(n);
		STentry entry = stLookup(n.className);
		if (entry == null) {
			System.out.println("Class id " + n.className + " at line "+ n.getLine() + " not declared");
			stErrors++;
		} if (!classTable.containsKey(n.className)) {
			System.out.println("Id " + n.className + " at line "+ n.getLine() + " not a class");
			stErrors++;
		}else {
			n.classEntry = entry;
		}
		for (Node arg : n.argumentList) visit(arg);
		return null;

	}

	@Override
	public Void visitNode(ClassCallNode n) {
		if (print) printNode(n);
		STentry entry = stLookup(n.classId);



		if (entry == null) {
			System.out.println("Object id " + n.classId + " at line "+ n.getLine() + " not declared");
			stErrors++;
		} else {
			n.classEntry = entry;
			n.nestingLevel = nestingLevel;
			//fino a qui cerco ID1 come in IdNode e CallNode (discesa livelli)

			//da qui cerdo ID2
			//se ID1 è RefTypeNode
			if (entry.type instanceof RefTypeNode ref){
				//cerco ID2 nella virtualTable
				Map<String, STentry> hm = classTable.get(ref.id);
				STentry methodEntry = hm.get(n.methodId);
				//se non lo trovo errore
				if (methodEntry == null) {
					System.out.println("Object id " + n.classId + " at line " + n.getLine() + " has no method " + n.methodId);
					this.stErrors++;
				} else {//se lo trovo lo assegno
					n.methodEntry = methodEntry;
				}
			} else {//se ID1 non è reftype errore
				System.out.println("Object id " + n.classId + " at line " + n.getLine() + " is not a RefTypeNode");
				this.stErrors++;
			}

		}

		for (Node arg : n.argumentList) visit(arg);
		return null;
	}

	/**
	 *
	 * @param n
	 * @return
	 * @throws VoidException
	 */
	@Override
	public Void visitNode(RefTypeNode n) throws VoidException {
		if (print) printNode(n);
		return null;
	}
}
