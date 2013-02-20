package sdl.ist.osaka_u.newmasu.dataManager;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;

import sdl.ist.osaka_u.newmasu.util.DualMultiMap;

public class VariableManager {
	
	final private static DualMultiMap<ASTNode, IBinding> rel = new DualMultiMap<>();
	
	public static final DualMultiMap<ASTNode, IBinding> getRel() {
		return rel;
	}
//
//	// 呼び出し元→呼び出し先
//	final private static MultiHashMap<ASTNode, IBinding> calleeToCaller = new MultiHashMap<>();
//	public static final MultiHashMap<ASTNode, IBinding> getCalleetocaller() {
//		return calleeToCaller;
//	}
//
//	// 呼び出し先→呼び出し元
//	final private static MultiHashMap<IBinding, ASTNode> callerToCaller = new MultiHashMap<>();
//	public static final MultiHashMap<IBinding, ASTNode> getCallertocallee() {
//		return callerToCaller;
//	}
//	
//	public static void addRelation(final ASTNode node, final IBinding bind){
//		calleeToCaller.put(node, bind);
//		callerToCaller.put(bind, node);
//	}
	

	// インスタンスの生成を防ぐ
	private VariableManager() {
	}
}