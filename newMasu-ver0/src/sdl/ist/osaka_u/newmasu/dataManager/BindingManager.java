package sdl.ist.osaka_u.newmasu.dataManager;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.multimap.MultiHashMap;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import sdl.ist.osaka_u.newmasu.util.ExternalNodeBuilder;
import sdl.ist.osaka_u.newmasu.util.NodeFinder;

public class BindingManager {

	/**
	 * CompilationUnitとPathを対応付けるMap
	 */
	static final HashMap<Path, CompilationUnit> rel = new HashMap<Path, CompilationUnit>();

	/**
	 * 宣言しているSimpleNameを格納 呼び出し先を探すときに使用 以下はbindingとNodeの対応関係 IVariableBinding :
	 * SimpleName IMethodBinding : BodyDeclaration(MethodDeclaration,
	 * Initializer, FieldDeclaration, EnumConstDeclaration) ITypeBinding :
	 * AbstractTypeDecralation(TypeDeclaration, EnumDeclaration)
	 */
	static final HashMap<IBinding, ASTNode> dec = new HashMap<IBinding, ASTNode>();

	/**
	 * 参照しているSimpleNameを格納 呼び出し元を探すときに使用
	 */
	static final MultiHashMap<IBinding, ASTNode> ref = new MultiHashMap<IBinding, ASTNode>();

	/**
	 * SimpleNameのうち，継承に関係するものを格納 継承関係を取得するときに使用
	 */
	static final MultiHashMap<ITypeBinding, ASTNode> ext = new MultiHashMap<ITypeBinding, ASTNode>();

	public static HashMap<Path, CompilationUnit> getRel() {
		return rel;
	}

	public static HashMap<IBinding, ASTNode> getDec() {
		return dec;
	}

	public static MultiHashMap<IBinding, ASTNode> getRef() {
		return ref;
	}

	public static MultiHashMap<ITypeBinding, ASTNode> getExt() {
		return ext;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * オーバーライドも考慮して，引数で与えられたメソッドを呼び出しているメソッド一覧を取得する．呼び出し元のノードは以下のどれかに該当する．<br>
	 * MethodDeclaration<br>
	 * FieldDeclaration <br>
	 * initializer <br>
	 * EnumDeclaration<br>
	 *
	 * @param node
	 *            対象とするメソッドのノード
	 * @return
	 */
	public static final Set<ASTNode> getCalleeMethods(MethodDeclaration node) {

		Set<ASTNode> set = new HashSet<ASTNode>();

		IMethodBinding binding = node.resolveBinding();
		set = getCalleeMethods(binding, set);

		for (ASTNode ast : getOverridingMethod(node)) {
			if (ast.getNodeType() == ASTNode.METHOD_DECLARATION) {
				MethodDeclaration md = (MethodDeclaration) ast;
				set = getCalleeMethods(md.resolveBinding(), set);
			} else {
				System.err.println("ERROR in getCalleeMethod");
			}
		}

		return set;
	}

	/**
	 * 引数で与えられたメソッドを呼び出しているメソッド一覧を取得する．呼び出し元のノードは以下のどれかに該当する．<br>
	 * MethodDeclaration<br>
	 * FieldDeclaration <br>
	 * initializer <br>
	 * EnumDeclaration<br>
	 *
	 * @param binding
	 *            対象とするメソッドのbinding
	 * @param set
	 *            取得したノードを格納するset
	 * @return
	 */
	private static final Set<ASTNode> getCalleeMethods(IBinding binding,
			Set<ASTNode> set) {

		Set<ASTNode> nodes = set;
		Collection<ASTNode> col = ref.getCollection(binding);

		if (col != null) {
			for (ASTNode node : col) {
				nodes.add(NodeFinder.getMethodNode(node));
			}
		}

		return nodes;
	}

	// ////////////////////////////////////////////////////////////////////////////////

	/**
	 * 引数で与えられたメソッドが呼び出しているメソッド一覧を取得する．
	 *
	 * @param node
	 *            対象とするメソッドのノード
	 * @return
	 */
	public static final Set<ASTNode> getCallerMethods(ASTNode node) {

		Set<ASTNode> set = new HashSet<ASTNode>();

		Object o = node.getProperty("Caller");
		if (o != null) {
			List<IMethodBinding> callers = (List<IMethodBinding>) o;
			for (IMethodBinding binding : callers) {
				set = getCallerMethods(binding, set);
			}
		}

		return set;
	}

	/**
	 * オーバーライドも考慮して，引数で与えられたメソッドが呼び出しているメソッド一覧を取得する．
	 *
	 * @param binding
	 *            対象とするメソッドののbinding
	 * @param set
	 *            取得したノードを格納するためのset
	 * @return
	 */
	private static final Set<ASTNode> getCallerMethods(IMethodBinding binding,
			Set<ASTNode> set) {

		Set<ASTNode> methodSet = set;
		ASTNode ast = dec.get(binding);

		if (ast != null) {

			ASTNode methodNode = NodeFinder.getMethodNode(ast);
			methodSet.add(methodNode);

			methodSet = getOverridedMethod(binding, methodSet);

			return methodSet;

		} else {

			if (binding == null) {
				System.err.println("can't resolve binding");
			} else {
				System.out
						.println("binding to class file " + binding.getName());
			}

			return methodSet;
		}
	}

	// /////////////////////////////////////////////////////////////////////////

	public static final Set<ASTNode> getCalleeVariable(SimpleName node) {

		IBinding binding = node.resolveBinding();

		Set<ASTNode> set = new HashSet<ASTNode>();
		Collection<ASTNode> col = ref.getCollection(binding);

		if (col != null) {
			for (ASTNode ast : col) {
				set.add(ast);
			}
		}

		return set;

	}

	// /////////////////////////////////////////////////////////////////////////

	public static final ASTNode getCallerVariable(SimpleName node) {

		IBinding binding = node.resolveBinding();
		ASTNode ast = dec.get(binding);

		if (ast != null) {
			return ast;
		} else {
			return null;
		}
	}

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * 引数で与えられたクラスを継承しているクラス一覧を取得する．
	 *
	 * @param node
	 *            対象とするクラスのノード
	 * @return
	 */
	public static final Set<AbstractTypeDeclaration> getExtendedClass(
			AbstractTypeDeclaration node) {

		Set<AbstractTypeDeclaration> set = new HashSet<AbstractTypeDeclaration>();
		IBinding binding = node.resolveBinding();

		set = getExtendedClass(binding, set);

		return set;
	}

	/**
	 * 引数で与えられたクラスを継承しているクラス一覧を取得する．
	 *
	 * @param binding
	 *            対象とするクラスのbinding
	 * @param set
	 *
	 * @return
	 */
	private static final Set<AbstractTypeDeclaration> getExtendedClass(
			IBinding binding, Set<AbstractTypeDeclaration> set) {

		Set<AbstractTypeDeclaration> nodes = set;
		Collection<ASTNode> col = ext.getCollection(binding);

		if (col != null) {
			for (ASTNode node : col) {
				AbstractTypeDeclaration ast = NodeFinder.getTypeNode(node);
				nodes.add(ast);
				nodes = getExtendedClass(ast.resolveBinding(), nodes);
			}
		}

		return nodes;
	}

	// ///////////////////////////////////////////////////////////////////////////

	/**
	 * 引数で与えられたクラスが継承しているクラス一覧を取得する．
	 *
	 * @param node
	 *            対象とするクラスのノード
	 * @return
	 */
	public static final Set<AbstractTypeDeclaration> getExtendingClass(
			AbstractTypeDeclaration node) {

		Set<AbstractTypeDeclaration> set = new HashSet<AbstractTypeDeclaration>();

		ITypeBinding binding = node.resolveBinding();
		if (binding != null) {
			getExtendingClass(binding, set);
		}

		return set;
	}

	/**
	 * 引数で与えられたクラスが継承しているクラス一覧を取得する．
	 *
	 * @param binding
	 *            対象とするクラスのノード
	 * @param set
	 *            取得したクラス一覧を格納するためのset
	 * @return
	 */
	private static final Set<AbstractTypeDeclaration> getExtendingClass(
			ITypeBinding binding, Set<AbstractTypeDeclaration> set) {

		Set<AbstractTypeDeclaration> nodes = set;

		ITypeBinding tBinding = binding.getSuperclass();
		if (tBinding != null) {
			set = addNode(tBinding, set);

		}

		for (ITypeBinding tb : binding.getInterfaces()) {
			if (tb != null) {
				set = addNode(tb, set);
			}
		}

		return nodes;
	}

	private static final Set<AbstractTypeDeclaration> addNode(
			ITypeBinding binding, Set<AbstractTypeDeclaration> set) {

		Set<AbstractTypeDeclaration> nodes = set;
		ASTNode node = dec.get(binding);

		if (node == null) {
			if (binding.isClass()) {
				nodes.add(ExternalNodeBuilder.createTypedeDeclaration(binding));
			} else {
				nodes.add(ExternalNodeBuilder.createEnumDeclaration(binding));
			}
		} else {
			nodes.add((AbstractTypeDeclaration) node);
			getExtendingClass((AbstractTypeDeclaration) node);
		}

		return nodes;
	}

	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * 引数で与えられたメソッドをオーバーライドしているメソッド一覧を取得する．
	 *
	 * @param node
	 *            対象とするメソッドのノード
	 * @return
	 */
	public static final Set<ASTNode> getOverridedMethod(MethodDeclaration node) {

		Set<ASTNode> set = new HashSet<ASTNode>();
		set = getOverridedMethod(node.resolveBinding(), set);

		return set;
	}

	/**
	 * 引数で与えられたメソッドをオーバーライドしているメソッド一覧を取得する．
	 *
	 * @param binding
	 *            対象とするメソッドのbinding
	 * @param set
	 *            取得したノードを格納するためのset
	 * @return
	 */
	private static final Set<ASTNode> getOverridedMethod(
			IMethodBinding binding, Set<ASTNode> set) {

		Set<ASTNode> methods = set;

		Set<AbstractTypeDeclaration> classSet = new HashSet<AbstractTypeDeclaration>();
		ITypeBinding typeBinding = binding.getDeclaringClass();
		classSet = getExtendedClass(typeBinding, classSet);

		for (ASTNode classNode : classSet) {

			if (classNode.getNodeType() == ASTNode.TYPE_DECLARATION) {
				TypeDeclaration td = (TypeDeclaration) classNode;
				for (IMethodBinding mb : td.resolveBinding()
						.getDeclaredMethods()) {
					if (mb.overrides(binding)) {
						ASTNode overrideNode = dec.get(mb);
						if (overrideNode != null) {
							methods.add(NodeFinder.getMethodNode(overrideNode));
						}
					}
				}
			}
		}

		return methods;
	}

	// //////////////////////////////////////////////////////////////////////////////////

	/**
	 * 引数で与えられたメソッドがオーバーライドしているメソッド一覧を取得する．
	 *
	 * @param node
	 *            対象とするメソッドのノード
	 * @return
	 */
	public static final Set<ASTNode> getOverridingMethod(MethodDeclaration node) {

		Set<ASTNode> methods = new HashSet<ASTNode>();
		IMethodBinding binding = node.resolveBinding();

		// 対象とするメソッドがあるクラスの親クラスを取得する
		ITypeBinding typeBinding = binding.getDeclaringClass();
		ASTNode typeNode = dec.get(typeBinding);
		if (typeNode == null) {
			return methods;
		}

		// 対象とするメソッドがあるクラスの親クラス一覧を取得する
		TypeDeclaration td = (TypeDeclaration) typeNode;
		Set<AbstractTypeDeclaration> classSet = getExtendingClass(td);

		for (AbstractTypeDeclaration typeDecNode : classSet) {
			ITypeBinding tBinding = typeDecNode.resolveBinding();
			if (tBinding != null) {
				for (IMethodBinding mb : tBinding.getDeclaredMethods()) {
					if (binding.overrides(mb)) {
						ASTNode overrideNode = dec.get(mb);
						if (overrideNode != null) {
							methods.add(NodeFinder.getMethodNode(overrideNode));
						}
					}
				}
			}

		}

		return methods;
	}

	// //////////////////////////////////////////////////////////////////////////////////

	public static final Set<ASTNode> getInnerClass(TypeDeclaration node) {

		Set<ASTNode> set = new HashSet<ASTNode>();

		Object o = node.getProperty("Inner");
		if (o != null) {
			List<ITypeBinding> inners = (List<ITypeBinding>) o;
			for (ITypeBinding binding : inners) {
				set.add(dec.get(binding));
			}
		}

		return set;
	}

	// //////////////////////////////////////////////////////////////////////////////////

	// public static final Set<ASTNode> getAccesibleClass(TypeDeclaration node)
	// {
	//
	// Set<ASTNode> set = getInnerClass(node);
	//
	// for (ASTNode ast : getExtendingClass(node)) {
	//
	// set.add(ast);
	// TypeDeclaration td = (TypeDeclaration) ast;
	//
	// for (ASTNode innerNode : getInnerClass(td)) {
	// TypeDeclaration type = (TypeDeclaration) innerNode;
	// if (!type.resolveBinding().isLocal())
	// set.add(innerNode);
	// }
	// }
	//
	// return set;
	// }

	// //////////////////////////////////////////////////////////////////////////////////
	// インスタンスを生成しないようにする
	private BindingManager() {
	}
}