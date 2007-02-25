package jp.ac.osaka_u.ist.sel.metricstool.main.data.target;


/**
 * try ブロックを表すクラス
 * 
 * @author y-higo
 * 
 */
public final class TryBlockInfo extends BlockInfo {

    /**
     * 位置情報を与えて try ブロックを初期化
     * 
     * @param fromLine 開始行
     * @param fromColumn 開始列
     * @param toLine 終了行
     * @param toColumn 終了列
     */
    public TryBlockInfo(final int fromLine, final int fromColumn, final int toLine,
            final int toColumn) {
        super(fromLine, fromColumn, toLine, toColumn);
    }
}
