package jp.ac.osaka_u.ist.sel.metricstool.main.data.target;


import jp.ac.osaka_u.ist.sel.metricstool.main.security.MetricsToolSecurityManager;


/**
 * if文やwhile文の条件節を表すクラス
 * 
 * @author higo
 *
 */
public final class ConditionalClauseInfo extends LocalSpaceInfo {

    /**
     * 位置情報を与えて初期化
     * 
     * @param ownerClass このブロックを所有するクラス
     * @param fromLine 開始行
     * @param fromColumn 開始列
     * @param toLine 終了行
     * @param toColumn 終了列
     */
    public ConditionalClauseInfo(final TargetClassInfo ownerClass, final int fromLine,
            final int fromColumn, final int toLine, final int toColumn) {

        super(ownerClass, fromLine, fromColumn, toLine, toColumn);

        MetricsToolSecurityManager.getInstance().checkAccess();

    }

    /**
     * この条件節をもつブロックを返す
     * 
     * @return この場建設をもつブロック
     */
    public final ConditionalBlockInfo getOwnerBlock() {
        return this.ownerBlock;
    }
    
    /**
     * この条件節をもつブロックをセットする
     * 
     * @param ownerBlock この条件節をもつブロック 
     */
    public final void setOwnerBlock(final ConditionalBlockInfo ownerBlock) {
        MetricsToolSecurityManager.getInstance().checkAccess();
        if(null == ownerBlock) {
            throw new IllegalArgumentException("ownerBlock is null");
        }
        
        this.ownerBlock = ownerBlock;
    }

    private ConditionalBlockInfo ownerBlock;
}
