package sdl.ist.osaka_u.newmasu.AST;

import java.io.PrintWriter;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import sdl.ist.osaka_u.newmasu.accessor.MethodInfoAccessor;
import sdl.ist.osaka_u.newmasu.util.NodeFinder;

public class MethodVisitor extends ASTVisitor {

	PrintWriter pw;

	public MethodVisitor(PrintWriter pw) {
		this.pw = pw;
	}

	@Override
	public boolean visit(MethodDeclaration node) {

		pw.println("*****************************************************");
		pw.println("-------" + node.resolveBinding().getKey() + "-------");

		// for (Modifier m : MethodInfoAccessor.getModifiers(node)){
		// pw.print(m);
		// pw.print(" ");
		// }
		//
		//
		// pw.print(node.getName());

		pw.println("local variable");
		for (ASTNode ast : MethodInfoAccessor.getLocalVariables(node)) {
			pw.print("\t");
			pw.println(NodeFinder.getDeclaringNode(ast));
		}

		pw.println();
		pw.println("declaring class");
		TypeDeclaration td = MethodInfoAccessor.getDeclaringClass(node);
		pw.print("\t");
		pw.println(td.resolveBinding().getKey());

		pw.println();
		pw.println("call method");
		for (ASTNode ast : MethodInfoAccessor.getCallers(node)) {
			MethodDeclaration md = (MethodDeclaration) ast;
			pw.print("\t");
			pw.println(md.resolveBinding().getKey());
		}

		pw.println();
		pw.println("called");
		for (ASTNode ast : MethodInfoAccessor.getCallees(node)) {
			pw.print("\t");
			if (ast.getNodeType() == ASTNode.METHOD_DECLARATION) {
				MethodDeclaration md = (MethodDeclaration) ast;
				pw.println(md.resolveBinding().getKey());
			} else {
				pw.println(ast);
			}
		}

		pw.println();
		pw.println("override method");
		for (ASTNode ast : MethodInfoAccessor.getOverridees(node)) {
			MethodDeclaration md = (MethodDeclaration) ast;
			pw.print("\t");
			pw.println(md.resolveBinding().getKey());
		}

		pw.println();
		pw.println("overrided");
		for (ASTNode ast : MethodInfoAccessor.getOverriders(node)) {
			MethodDeclaration md = (MethodDeclaration) ast;
			pw.print("\t");
			pw.println(md.resolveBinding().getKey());
		}

		pw.println();
		pw.println("refered field");
		for (ASTNode ast : MethodInfoAccessor.getUsedFields(node)) {
			pw.print("\t");
			pw.println(NodeFinder.getDeclaringNode(ast));
		}

		pw.println();
		pw.println("assigned field");
		for (ASTNode ast : MethodInfoAccessor.getAssignedFields(node)) {
			pw.print("\t");
			pw.println(NodeFinder.getDeclaringNode(ast));
		}

		return true;
	}

}
