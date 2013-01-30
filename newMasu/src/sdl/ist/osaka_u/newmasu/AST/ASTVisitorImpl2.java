package sdl.ist.osaka_u.newmasu.AST;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import sdl.ist.osaka_u.newmasu.dataManager.BindingManager;
import sdl.ist.osaka_u.newmasu.util.NodeFinder;

public class ASTVisitorImpl2 extends ASTVisitor {

	// ////////////////////////////////////////////////////////
	// クラス情報に関する部分
	// ////////////////////////////////////////////////////////

	@Override
	public boolean visit(TypeDeclaration node) {

		ITypeBinding typeBinding = node.resolveBinding();

		if (!typeBinding.isTopLevel()) {
			registerInnerClass(node, typeBinding);
		}

		if (node.getJavadoc() != null)
			node.getJavadoc().accept(this);

		if (node.modifiers() != null)
			for (Object o : node.modifiers()) {
				ASTNode ast = (ASTNode) o;
				ast.accept(this);
			}

		if (node.getName() != null)
			node.getName().accept(this);

		if (node.typeParameters() != null)
			for (Object o : node.typeParameters()) {
				ASTNode ast = (ASTNode) o;
				ast.accept(this);
			}

		if (node.getSuperclassType() != null) {
			ITypeBinding binding = node.getSuperclassType().resolveBinding();
			BindingManager.getExt().put(binding,
					NodeFinder.getDeclaringNode(node));
		}

		if (node.superInterfaceTypes() != null)
			for (Object o : node.superInterfaceTypes()) {
				Type ast = (Type) o;
				ITypeBinding binding = ast.resolveBinding();
				BindingManager.getExt().put(binding,
						NodeFinder.getDeclaringNode(node));
			}

		if (node.bodyDeclarations() != null)
			for (Object o : node.bodyDeclarations()) {
				ASTNode ast = (ASTNode) o;
				ast.accept(this);
			}

		return false;
	}

	public boolean visit(AnonymousClassDeclaration node) {

		IBinding binding = node.resolveBinding();
		BindingManager.getDec().put(binding, NodeFinder.getDeclaringNode(node));

		return super.visit(node);
	}

	// ////////////////////////////////////////////////////////
	// メソッド情報に関する部分
	// ////////////////////////////////////////////////////////

	@Override
	public boolean visit(MethodInvocation node) {

		List<IMethodBinding> callers = null;
		ASTNode parent = NodeFinder.getMethodNode(node);
		Object o = parent.getProperty("Caller");

		if (o == null) {
			callers = new ArrayList<IMethodBinding>();
		} else {
			callers = (List<IMethodBinding>) o;
		}

		IMethodBinding binding = node.resolveMethodBinding();
		callers.add(binding);
		parent.setProperty("Caller", callers);

		return super.visit(node);
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {

		List<IMethodBinding> callers = null;
		ASTNode parent = NodeFinder.getMethodNode(node);
		Object obj = parent.getProperty("Caller");

		if (obj == null) {
			callers = new ArrayList<IMethodBinding>();
		} else {
			callers = (List<IMethodBinding>) obj;
		}

		IMethodBinding binding = node.resolveConstructorBinding();
		callers.add(binding);
		parent.setProperty("Caller", callers);

		BindingManager.getRef().put(binding, NodeFinder.getDeclaringNode(node));

		if (node.getExpression() != null)
			node.getExpression().accept(this);

		if (node.typeArguments() != null)
			for (Object o : node.typeArguments()) {
				ASTNode ast = (ASTNode) o;
				ast.accept(this);
			}

		if (node.arguments() != null)
			for (Object o : node.arguments()) {
				ASTNode ast = (ASTNode) o;
				ast.accept(this);
			}

		if (node.getAnonymousClassDeclaration() != null)
			node.getAnonymousClassDeclaration().accept(this);

		return false;
	}

	@Override
	public boolean visit(MethodDeclaration node) {

		if (!node.isConstructor()) {
			return true;
		}

		IBinding binding = node.resolveBinding();
		BindingManager.getDec().put(binding, NodeFinder.getDeclaringNode(node));

		if (node.getJavadoc() != null)
			node.getJavadoc().accept(this);

		if (node.modifiers() != null)
			for (Object o : node.modifiers()) {
				ASTNode ast = (ASTNode) o;
				ast.accept(this);
			}

		if (node.typeParameters() != null)
			for (Object o : node.typeParameters()) {
				ASTNode ast = (ASTNode) o;
				ast.accept(this);
			}

		if (node.parameters() != null)
			for (Object o : node.parameters()) {
				ASTNode ast = (ASTNode) o;
				ast.accept(this);
			}

		if (node.thrownExceptions() != null)
			for (Object o : node.thrownExceptions()) {
				ASTNode ast = (ASTNode) o;
				ast.accept(this);
			}

		if (node.getBody() != null)
			node.getBody().accept(this);

		return false;
	}

	// ////////////////////////////////////////////////////////
	// 変数情報に関する部分
	// ////////////////////////////////////////////////////////

	@Override
	public boolean visit(SimpleName node) {

		IBinding binding = node.resolveBinding();

		if (binding != null) {
			if (node.isDeclaration()) {
				if (binding.getKind() == IBinding.VARIABLE) {
					BindingManager.getDec().put(binding, node);
					registerVariables(node, (IVariableBinding) binding);
				} else {
					BindingManager.getDec().put(binding,
							NodeFinder.getDeclaringNode(node));
				}
			} else {
				if (binding.getKind() == IBinding.VARIABLE) {
					BindingManager.getRef().put(binding, node);
					registerVariables(node, (IVariableBinding) binding);
				} else {
					BindingManager.getRef().put(binding,
							NodeFinder.getDeclaringNode(node));
				}
			}
		} else {
			System.err.println("binding error in visit(SimpleName)");
		}

		return super.visit(node);
	}

	@Override
	public boolean visit(Assignment node) {

		Expression exp = node.getLeftHandSide();

		if (exp != null) {
			if (exp.getNodeType() == ASTNode.SIMPLE_NAME) {
				exp.setProperty("Assignment", true);
			} else {
				node.getLeftHandSide().accept(new AssignmentVisitor());
			}
		}

		return true;
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {

		SimpleName name = node.getName();
		Expression init = node.getInitializer();

		if (init != null) {
			name.setProperty("Assignment", true);
		}

		return true;
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {

		SimpleName name = node.getName();
		Expression init = node.getInitializer();

		if (init != null) {
			name.setProperty("Assignment", true);
		}

		return true;
	}

	// ////////////////////////////////////////////////////////

	private void registerInnerClass(TypeDeclaration node, ITypeBinding binding) {

		List<ITypeBinding> callers = null;
		ASTNode parent = NodeFinder.getTypeNode(node.getParent());
		Object o = parent.getProperty("Inner");

		if (o == null) {
			callers = new ArrayList<ITypeBinding>();
		} else {
			callers = (List<ITypeBinding>) o;
		}

		callers.add(binding);
		parent.setProperty("Inner", callers);

		TypeDeclaration outerClass = (TypeDeclaration) parent;
		if (!outerClass.resolveBinding().isTopLevel()) {
			registerInnerClass(outerClass, binding);
		}

	}

	private void registerVariables(SimpleName node, IVariableBinding binding) {

		List<IVariableBinding> refs = null;
		ASTNode parent = NodeFinder.getMethodNode(node.getParent());
		Object o = parent.getProperty("Variable");

		if (o == null) {
			refs = new ArrayList<IVariableBinding>();
		} else {
			refs = (List<IVariableBinding>) o;
		}

		refs.add(binding);
		parent.setProperty("Variable", refs);

	}

	// ////////////////////////////////////////////////////////

	/**
	 * 代入してる変数を見つけるためのクラス
	 *
	 * @author t-ishihr
	 *
	 */
	private class AssignmentVisitor extends ASTVisitor {

		@Override
		public boolean visit(FieldAccess node) {

			SimpleName name = node.getName();

			if (name != null) {
				name.setProperty("Assignment", true);
			}

			return false;
		}

		@Override
		public boolean visit(ParenthesizedExpression node) {

			Expression expression = node.getExpression();

			if (expression.getNodeType() == ASTNode.SIMPLE_NAME) {
				expression.setProperty("Assignment", true);
			}

			return true;
		}

		@Override
		public boolean visit(SuperFieldAccess node) {

			SimpleName name = node.getName();

			if (name != null) {
				name.setProperty("Assignment", true);
			}

			return false;
		}
	}

}
