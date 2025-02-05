package compiler;

import java.util.*;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import compiler.AST.*;
import compiler.FOOLParser.*;
import compiler.lib.*;
import static compiler.lib.FOOLlib.*;

public class ASTGenerationSTVisitor extends FOOLBaseVisitor<Node> {

	String indent;
    public boolean print;
	
    ASTGenerationSTVisitor() {}    
    ASTGenerationSTVisitor(boolean debug) { print=debug; }
        
    private void printVarAndProdName(ParserRuleContext ctx) {
        String prefix="";        
    	Class<?> ctxClass=ctx.getClass(), parentClass=ctxClass.getSuperclass();
        if (!parentClass.equals(ParserRuleContext.class)) // parentClass is the var context (and not ctxClass itself)
        	prefix=lowerizeFirstChar(extractCtxName(parentClass.getName()))+": production #";
    	System.out.println(indent+prefix+lowerizeFirstChar(extractCtxName(ctxClass.getName())));                               	
    }
        
    @Override
	public Node visit(ParseTree t) {
    	if (t==null) return null;
        String temp=indent;
        indent=(indent==null)?"":indent+"  ";
        Node result = super.visit(t);
        indent=temp;
        return result; 
	}

	@Override
	public Node visitProg(ProgContext c) {
		if (print) printVarAndProdName(c);
		return visit(c.progbody());
	}

	@Override
	public Node visitLetInProg(LetInProgContext c) {
		if (print) printVarAndProdName(c);
		List<DecNode> declist = new ArrayList<>();
		for (DecContext dec : c.dec()) declist.add((DecNode) visit(dec));
		return new ProgLetInNode(declist, visit(c.exp()));
	}

	@Override
	public Node visitNoDecProg(NoDecProgContext c) {
		if (print) printVarAndProdName(c);
		return new ProgNode(visit(c.exp()));
	}

	@Override
	public Node visitTimesDiv(TimesDivContext c) {
		if (print) printVarAndProdName(c);
		Node n;
		if (c.TIMES()!=null) {
			n = new TimesNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.TIMES().getSymbol().getLine());
		} else {
			n = new DivNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.DIV().getSymbol().getLine());
		}
        return n;		
	}

	@Override
	public Node visitPlusMinus(PlusMinusContext c) {
		if (print) printVarAndProdName(c);
		Node n;
		if (c.PLUS()!=null) {
			n = new PlusNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.PLUS().getSymbol().getLine());
		} else {
			n = new MinusNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.MINUS().getSymbol().getLine());
		}
        return n;		
	}

	@Override
	public Node visitComp(CompContext c) {
		if (print) printVarAndProdName(c);
		Node n;
		if (c.EQ()!=null) {
			n = new EqualNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.EQ().getSymbol().getLine());
		} else if (c.LE()!=null) {
			n = new LessEqualNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.LE().getSymbol().getLine());
		} else {
			n = new GreaterEqualNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.GE().getSymbol().getLine());
		}

		return n;
	}

	@Override
	public Node visitAndOr(AndOrContext c) {
		if (print) printVarAndProdName(c);

		if (c.AND()!=null) {
			Node andNode = new AndNode(this.visit(c.exp(0)), this.visit(c.exp(1)));
			andNode.setLine(c.AND().getSymbol().getLine());
			return andNode;
		} else {
			Node orNode = new OrNode(this.visit(c.exp(0)), this.visit(c.exp(1)));
			orNode.setLine(c.OR().getSymbol().getLine());
			return orNode;
		}
	}

	@Override
	public Node visitNot(NotContext c) {
		if (print) printVarAndProdName(c);
		Node n = new NotNode( visit(c.exp()) );
		n.setLine(c.NOT().getSymbol().getLine());
		return n;
	}

	@Override
	public Node visitVardec(VardecContext c) {
		if (print) printVarAndProdName(c);
		Node n = null;
		if (c.ID()!=null) { //non-incomplete ST
			n = new VarNode(c.ID().getText(), (TypeNode) visit(c.type()), visit(c.exp()));
			n.setLine(c.VAR().getSymbol().getLine());
		}
        return n;
	}

	@Override
	public Node visitFundec(FundecContext c) {
		if (print) printVarAndProdName(c);
		List<ParNode> parList = new ArrayList<>();
		for (int i = 1; i < c.ID().size(); i++) { 
			ParNode p = new ParNode(c.ID(i).getText(),(TypeNode) visit(c.type(i)));
			p.setLine(c.ID(i).getSymbol().getLine());
			parList.add(p);
		}
		List<DecNode> decList = new ArrayList<>();
		for (DecContext dec : c.dec()) decList.add((DecNode) visit(dec));
		Node n = null;
		if (c.ID().size()>0) { //non-incomplete ST
			n = new FunNode(c.ID(0).getText(),(TypeNode)visit(c.type(0)),parList,decList,visit(c.exp()));
			n.setLine(c.FUN().getSymbol().getLine());
		}
        return n;
	}

	@Override
	public Node visitIntType(IntTypeContext c) {
		if (print) printVarAndProdName(c);
		return new IntTypeNode();
	}

	@Override
	public Node visitBoolType(BoolTypeContext c) {
		if (print) printVarAndProdName(c);
		return new BoolTypeNode();
	}

	@Override
	public Node visitInteger(IntegerContext c) {
		if (print) printVarAndProdName(c);
		int v = Integer.parseInt(c.NUM().getText());
		return new IntNode(c.MINUS()==null?v:-v);
	}

	@Override
	public Node visitTrue(TrueContext c) {
		if (print) printVarAndProdName(c);
		return new BoolNode(true);
	}

	@Override
	public Node visitFalse(FalseContext c) {
		if (print) printVarAndProdName(c);
		return new BoolNode(false);
	}

	@Override
	public Node visitIf(IfContext c) {
		if (print) printVarAndProdName(c);
		Node ifNode = visit(c.exp(0));
		Node thenNode = visit(c.exp(1));
		Node elseNode = visit(c.exp(2));
		Node n = new IfNode(ifNode, thenNode, elseNode);
		n.setLine(c.IF().getSymbol().getLine());			
        return n;		
	}

	@Override
	public Node visitPrint(PrintContext c) {
		if (print) printVarAndProdName(c);
		return new PrintNode(visit(c.exp()));
	}

	@Override
	public Node visitPars(ParsContext c) {
		if (print) printVarAndProdName(c);
		return visit(c.exp());
	}

	@Override
	public Node visitId(IdContext c) {
		if (print) printVarAndProdName(c);
		Node n = new IdNode(c.ID().getText());
		n.setLine(c.ID().getSymbol().getLine());
		return n;
	}

	@Override
	public Node visitCall(CallContext c) {
		if (print) printVarAndProdName(c);		
		List<Node> arglist = new ArrayList<>();
		for (ExpContext arg : c.exp()) arglist.add(visit(arg));
		Node n = new CallNode(c.ID().getText(), arglist);
		n.setLine(c.ID().getSymbol().getLine());
		return n;
	}

	@Override
	public Node visitNull(final NullContext ctx) {
		if (this.print) this.printVarAndProdName(ctx);
		return new EmptyNode();
	}


	///
	///
	///

	/**
	 * CLASS
	 * @param ctx the parse tree
	 * @return
	 */
	@Override
	public Node visitCldec(CldecContext ctx) {
		if (print) printVarAndProdName(ctx);
		//nome
		String name = ctx.ID(0).getText();
		//superclasse, se non c'è null
		String superClass = Objects.isNull(ctx.EXTENDS()) ? null:  ctx.ID(1).getText();

		List<FieldNode> fields = new ArrayList<>();
		List<MethodNode> methods = new ArrayList<>();

		if (superClass!=null) {

		} else {
			for (var x : ctx.methdec()) methods.add((MethodNode) visitMethdec(x));
			for (int i = 1; i < ctx.ID().size(); i++){
				String field = ctx.ID(i).getText();
				TypeNode type = (TypeNode) visit(ctx.type(i));
				FieldNode f = new FieldNode(field, type);
				f.setLine(ctx.ID(i).getSymbol().getLine());
				fields.add(f);
			}
		}

		Node n = new ClassNode(name, methods, fields, superClass);
		n.setLine(ctx.CLASS().getSymbol().getLine());

		return n;
	}

	/**
	 * Methods
	 * @param ctx the parse tree
	 * @return a MethodNode
	 */
	@Override
	public Node visitMethdec(MethdecContext ctx) {
		if (print) printVarAndProdName(ctx);

		List<ParNode> parList = new ArrayList<>();
		List<DecNode> decList = new ArrayList<>();

		for (int i = 1; i < ctx.ID().size(); i++) {
			ParNode param = new ParNode(ctx.ID(i).getText(), (TypeNode) visit(ctx.type(i)));
			param.setLine(ctx.ID(i).getSymbol().getLine());
			parList.add(param);
		}

		for (DecContext dec : ctx.dec()) decList.add((DecNode) visit(dec));

		Node n = null;
		if (!ctx.ID().isEmpty()) {
			n = new MethodNode(
					ctx.ID(0).getText(),
					(TypeNode) visit(ctx.type(0)),
					parList,
					decList,
					visit(ctx.exp())
			);

			n.setLine(ctx.FUN().getSymbol().getLine());
		}
		return n;

	}

	@Override
	public Node visitNew(NewContext ctx) {
		if (print) printVarAndProdName(ctx);
		if (ctx.ID() == null) {
			return null;
		}

		final String cId = ctx.ID().getText();
		final List<Node> args = new ArrayList<>();

		for (ExpContext arg : ctx.exp()) args.add(visit(arg));

		final Node n = new NewNode(cId, args);

		n.setLine(ctx.NEW().getSymbol().getLine());
		return n;
	}

	@Override
	public Node visitDotCall(DotCallContext ctx) {
		if (print) printVarAndProdName(ctx);

		if (ctx.ID() == null || ctx.ID().isEmpty() || ctx.ID().size()!=2) {
			return null;
		}

		final String cId = ctx.ID(0).getText();//classe
		final String mId = ctx.ID(1).getText();//metodo

		final List<Node> params = new ArrayList<>();

		for (ExpContext param : ctx.exp()) params.add(visit(param));

		Node n = new ClassCallNode(cId, mId, params);
		n.setLine(ctx.DOT().getSymbol().getLine());
		return n;
	}

	@Override
	public Node visitIdType(IdTypeContext ctx) {
		if (this.print) this.printVarAndProdName(ctx);
		final String id = ctx.ID().getText();
		final RefTypeNode node = new RefTypeNode(id);
		node.setLine(ctx.ID().getSymbol().getLine());
		return node;
	}
}
