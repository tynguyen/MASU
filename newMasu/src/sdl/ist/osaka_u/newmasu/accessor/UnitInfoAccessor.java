package sdl.ist.osaka_u.newmasu.accessor;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import sdl.ist.osaka_u.newmasu.util.NodeFinder;

public class UnitInfoAccessor {

	/**
	 * 対象ノードの開始行を返す
	 *
	 * @param node
	 *            対象ノード
	 * @return 開始行
	 */
	public static int getFromLine(ASTNode node) {

		CompilationUnit unit = NodeFinder.getCompilationUnitNode(node);

		return unit.getLineNumber(node.getStartPosition());
	}

	/**
	 * 対象ノードの開始列を返す
	 *
	 * @param node
	 *            対象ノード
	 * @return 開始列
	 */
	public static int getFromColumn(ASTNode node) {

		CompilationUnit unit = NodeFinder.getCompilationUnitNode(node);

		return unit.getColumnNumber(node.getStartPosition());
	}

	/**
	 * 対象ノードの終了行を返す
	 *
	 * @param node
	 *            対象ノード
	 * @return 終了行
	 */
	public static int getToLine(ASTNode node) {

		CompilationUnit unit = NodeFinder.getCompilationUnitNode(node);

		return unit.getLineNumber(node.getStartPosition() + node.getLength());
	}

	/**
	 * 対象ノードの終了列を返す
	 *
	 * @param node
	 *            対象ノード
	 * @return 終了列
	 */
	public static int getToColumn(ASTNode node) {

		CompilationUnit unit = NodeFinder.getCompilationUnitNode(node);

		return unit.getLineNumber(node.getStartPosition() + node.getLength());
	}

	/**
	 * 対象ノードの行数を返す
	 *
	 * @param node
	 *            対象ノード
	 * @return 行数
	 */
	public static int getLOC(ASTNode node) {
		return getToLine(node) - getFromLine(node);
	}

	protected UnitInfoAccessor() {

	}
}
