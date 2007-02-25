package jp.ac.osaka_u.ist.sel.metricstool.main.data.target;


/**
 * catch ブロック情報を表すクラス
 * 
 * @author y-higo
 */
public final class CatchBlockInfo extends BlockInfo {

    /**
     * 対応する try ブロック情報を与えて catch ブロックを初期化
     * 
     * @param correspondingTryBlock
     */
    public CatchBlockInfo(final int fromLine, final int fromColumn, final int toLine,
            final int toColumn, final TryBlockInfo correspondingTryBlock) {

        super(fromLine, fromColumn, toLine, toColumn);

        if (null == correspondingTryBlock) {
            throw new NullPointerException();
        }

        this.correspondingTryBlock = correspondingTryBlock;
    }

    /**
     * 対応する try ブロックを返す
     * 
     * @return 対応する try ブロック
     */
    public TryBlockInfo getCorrespondingTryBlock() {
        return this.correspondingTryBlock;
    }

    private final TryBlockInfo correspondingTryBlock;
}
