package jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved;


import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ClassInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ElseBlockInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.FieldInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.IfBlockInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.MethodInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TargetClassInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TargetMethodInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.security.MetricsToolSecurityManager;


/**
 * 未解決 else ブロックを表すクラス
 * 
 * @author y-higo
 */
public final class UnresolvedElseBlockInfo extends UnresolvedBlockInfo<ElseBlockInfo> {

    /**
     * 対応する if ブロックを与えて，else ブロック情報を初期化
     * 
     * @param ownerIfBlock
     */
    UnresolvedElseBlockInfo(final UnresolvedIfBlockInfo ownerIfBlock) {

        MetricsToolSecurityManager.getInstance().checkAccess();
        if (null == ownerIfBlock) {
            throw new NullPointerException();
        }

        this.ownerIfBlock = ownerIfBlock;
    }

    /**
     * この未解決 else ブロックを解決する
     * 
     * @param usingClass 所属クラス
     * @param usingMethod 所属メソッド
     * @param classInfoManager 用いるクラスマネージャ
     * @param fieldInfoManager 用いるフィールドマネージャ
     * @param methodInfoManger 用いるメソッドマネージャ
     */
    public ElseBlockInfo resolveUnit(final TargetClassInfo usingClass,
            final TargetMethodInfo usingMethod, final ClassInfoManager classInfoManager,
            final FieldInfoManager fieldInfoManager, final MethodInfoManager methodInfoManager) {

        // 不正な呼び出しでないかをチェック
        MetricsToolSecurityManager.getInstance().checkAccess();
        if ((null == usingClass) || (null == usingMethod) || (null == classInfoManager)
                || (null == fieldInfoManager) || (null == methodInfoManager)) {
            throw new NullPointerException();
        }

        // 既に解決済みである場合は，キャッシュを返す
        if (this.alreadyResolved()) {
            return this.getResolvedUnit();
        }

        // この else ブロックが属する if ブロックを取得
        final UnresolvedIfBlockInfo unresolvedOwnerIfBlock = this.getOwnerIfBlock();
        final IfBlockInfo ownerIfBlock = unresolvedOwnerIfBlock.resolveUnit(usingClass,
                usingMethod, classInfoManager, fieldInfoManager, methodInfoManager);

        // この else ブロックの位置情報を取得
        final int fromLine = this.getFromLine();
        final int fromColumn = this.getFromColumn();
        final int toLine = this.getToLine();
        final int toColumn = this.getToColumn();

        this.resolvedInfo = new ElseBlockInfo(usingClass, usingMethod, fromLine, fromColumn,
                toLine, toColumn, ownerIfBlock);
        return this.resolvedInfo;
    }

    /**
     * この else ブロックと対応する if ブロックを返す
     * 
     * @return この else ブロックと対応する if ブロック
     */
    public UnresolvedIfBlockInfo getOwnerIfBlock() {
        return this.ownerIfBlock;
    }

    /**
     * この else ブロックと対応する if ブロックを保存するための変数
     */
    private final UnresolvedIfBlockInfo ownerIfBlock;
}
