package sdl.ist.osaka_u.newmasu.accessor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SimpleName;

import sdl.ist.osaka_u.newmasu.dataManager.BindingManager;
import sdl.ist.osaka_u.newmasu.util.ExternalNodeBuilder;

public class MethodInfoAccessor extends UnitInfoAccessor {

	/**
	 * 対象メソッドの修飾子を取得する
	 *
	 * @param node
	 *            対象となるメソッド
	 * @return 対象メソッドの修飾子のset
	 */
	public static Set<Modifier> getModifiers(MethodDeclaration node) {

		Set<Modifier> set = new HashSet<Modifier>();

		if (node.modifiers() != null) {
			for (Object o : node.modifiers()) {
				IExtendedModifier eModifier = (IExtendedModifier) o;
				if (eModifier.isModifier()) {
					set.add((Modifier) eModifier);
				}
			}
		}

		return set;
	}

	/**
	 * 対象メソッドがオーバーライドしているメソッドのノードを取得する．
	 *
	 * @param node
	 *            対象となるメソッド
	 * @return 対象メソッドがオーバーライドしているメソッドのset
	 */
	public static Set<ASTNode> getOverridees(MethodDeclaration node) {

		return BindingManager.getOverridingMethod(node);
	}

	/**
	 * 対象メソッドをオーバーライドしているメソッドのノードを取得する．
	 *
	 * @param node
	 *            対象となるメソッド
	 * @return 対象メソッドをオーバーライドしているメソッドのset
	 */
	public static Set<ASTNode> getOverriders(MethodDeclaration node) {

		return BindingManager.getOverridedMethod(node);
	}

	/**
	 * 対象メソッドを呼び出しているメソッドのノードを取得する．
	 *
	 * @param node
	 *            対象となるメソッド
	 * @return 対象メソッドを呼び出しているメソッドのset
	 */
	public static Set<ASTNode> getCallees(MethodDeclaration node) {

		return BindingManager.getCalleeMethods(node);
	}

	/**
	 * 対象メソッドが呼び出しているメソッドのノードを取得する．
	 *
	 * @param node
	 *            対象となるメソッド
	 * @return 対象メソッドが呼び出しているメソッドのset
	 */
	public static Set<ASTNode> getCallers(MethodDeclaration node) {

		return BindingManager.getCallerMethods(node);
	}

	/**
	 * 対象メソッドが所属しているクラスのノードを取得する．
	 *
	 * @param node
	 *            対象となるメソッド
	 * @return 対象メソッドが所属しているクラスのノード
	 */
	public static ASTNode getDeclaringClass(
			MethodDeclaration node) {

		IMethodBinding mBinding = node.resolveBinding();
		if (mBinding == null) {
			System.err.println("binding error in getDeclaringClass");
			return null;
		}

		ITypeBinding tBinding = mBinding.getDeclaringClass();
		ASTNode td = (ASTNode) BindingManager
				.getDec().get(tBinding);

		return td;
	}

	/**
	 * 対象メソッドで定義されている変数のノードを取得する．
	 *
	 * @param node
	 *            対象となるメソッド
	 * @return 対象メソッドで定義されている変数のset
	 */
	public static Set<ASTNode> getLocalVariables(MethodDeclaration node) {

		Set<ASTNode> set = new HashSet<ASTNode>();
		List<IVariableBinding> list = (List<IVariableBinding>) node
				.getProperty("Variable");

		if (list != null)
			for (Object o : list) {
				IVariableBinding vb = (IVariableBinding) o;
				if (!vb.isField() && !vb.isParameter()) {
					ASTNode ast = BindingManager.getDec().get(vb);
					set.add(ast);
				}
			}

		return set;
	}

	/**
	 * 対象メソッドで参照されているフィールドのノードを取得する．
	 *
	 * @param node
	 *            対象となるメソッド
	 * @return 対象メソッドで参照されているフィールドのset
	 */
	public static Set<ASTNode> getUsedFields(MethodDeclaration node) {

		Set<ASTNode> set = new HashSet<ASTNode>();
		List<IVariableBinding> list = (List<IVariableBinding>) node
				.getProperty("Variable");

		if (list != null)
			for (Object o : list) {
				IVariableBinding vb = (IVariableBinding) o;
				if (vb.isField()) {
					for (ASTNode ast : BindingManager.getRef().get(vb)) {
						Object obj = ast.getProperty("Assignment");
						if (obj == null) {
							set.add(BindingManager.getDec().get(vb));
						}
					}
				}
			}

		return set;
	}

	/**
	 * 対象メソッドで代入されているフィールドのノードを取得する．
	 *
	 * @param node
	 *            対象となるメソッド
	 * @return 対象メソッドで代入されているフィールドのset
	 */
	public static Set<SimpleName> getAssignedFields(MethodDeclaration node) {

		Set<SimpleName> set = new HashSet<SimpleName>();
		List<IVariableBinding> list = (List<IVariableBinding>) node
				.getProperty("Variable");

		if (list != null)
			for (Object o : list) {
				IVariableBinding vb = (IVariableBinding) o;
				if (vb.isField()) {
					for (ASTNode ast : BindingManager.getRef().get(vb)) {
						Object obj = ast.getProperty("Assignment");
						if (obj != null) {
							ASTNode decNode = BindingManager.getDec().get(vb);
							if (decNode == null) {
								set.add(ExternalNodeBuilder
										.createSimpleName(vb));
							} else {
								set.add((SimpleName) decNode);
							}
						}
					}
				}
			}

		return set;
	}

	private MethodInfoAccessor() {

	}
}