package jp.ac.osaka_u.ist.sel.metricstool.main.data.target;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


/**
 * 配列の長さを表す変数 length を表すクラス
 * 
 * @author higo
 *
 */
@SuppressWarnings("serial")
public final class ArrayLengthInfo extends FieldInfo {

    /**
     * このオブジェクトを(便宜上)定義している配列オブジェクトを与えて初期化
     * 
     * @param ownerArray 配列オブジェクト
     */
    private ArrayLengthInfo(final ArrayTypeInfo ownerArray) {

        super(new HashSet<ModifierInfo>(), "length", PrimitiveTypeInfo.INT, new ArrayTypeClassInfo(
                ownerArray), true, true, true, true, true, 0, 0, 0, 0);
    }

    /**
     * ArrayLengthInfo　を得るためのファクトリメソッド
     * 
     * @param ownerArray
     * @return
     */
    public static ArrayLengthInfo getArrayLengthInfo(final ArrayTypeInfo ownerArray) {

        if (null == ownerArray) {
            throw new IllegalArgumentException();
        }

        ArrayLengthInfo arrayLength = arrayMap.get(ownerArray);
        if (null == arrayLength) {
            arrayLength = new ArrayLengthInfo(ownerArray);
            arrayMap.put(ownerArray, arrayLength);
        }
        return arrayLength;
    }

    private final static Map<ArrayTypeInfo, ArrayLengthInfo> arrayMap = new HashMap<ArrayTypeInfo, ArrayLengthInfo>();
}
