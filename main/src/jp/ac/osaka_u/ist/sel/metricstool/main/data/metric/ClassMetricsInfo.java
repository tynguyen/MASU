package jp.ac.osaka_u.ist.sel.metricstool.main.data.metric;


import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ClassInfo;


/**
 * クラスメトリクスを登録するためのデータクラス
 * 
 * @author y-higo
 * 
 */
public final class ClassMetricsInfo extends MetricsInfo<ClassInfo> {

    /**
     * 計測対象クラスを与えて初期化
     * 
     * @param classInfo 計測対象クラス
     */
    public ClassMetricsInfo(final ClassInfo classInfo) {
        super(classInfo);
    }

    /**
     * メッセージの送信者名を返す
     * 
     * @return メッセージの送信者名
     */
    public String getMessageSourceName() {
        return this.getClass().getName();
    }
}
