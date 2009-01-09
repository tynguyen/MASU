package jp.ac.osaka_u.ist.sel.metricstool.main.data.target;


import java.util.HashSet;



/**
 * 配列の長さを表す変数 length を表すクラス
 * 
 * @author higo
 *
 */
public final class ArrayLengthInfo extends FieldInfo {

    /**
     * このオブジェクトを(便宜上)定義している配列オブジェクトを与えて初期化
     * 
     * @param ownerArray 配列オブジェクト
     */
    public ArrayLengthInfo(final ArrayTypeInfo ownerArray) {

        super(new HashSet<ModifierInfo>(), "length", PrimitiveTypeInfo.INT, new ArrayTypeClassInfo(
                ownerArray), 0, 0, 0, 0);
    }
}
