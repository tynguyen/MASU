package jp.ac.osaka_u.ist.sel.metricstool.main.data.target;


import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sel.metricstool.main.security.MetricsToolSecurityManager;


/**
 * 
 * @author y-higo
 * 
 * ファイルの情報を表すクラス．
 */
public final class FileInfo {

    /**
     * 指定されたファイル名のオブジェクトを初期化する．
     * 
     * @param name ファイル名
     */
    public FileInfo(final String name) {

        MetricsToolSecurityManager.getInstance().checkAccess();
        if (null == name) {
            throw new NullPointerException();
        }

        this.name = name;
        this.definedClasses = new TreeSet<ClassInfo>();
    }

    /**
     * このファイルに定義されているクラスを追加する．
     * 
     * @param definedClass 定義されたクラス．
     */
    public void addDefinedClass(final ClassInfo definedClass) {

        MetricsToolSecurityManager.getInstance().checkAccess();
        if (null == definedClass) {
            throw new NullPointerException();
        }

        this.definedClasses.add(definedClass);
    }

    /**
     * このファイルに定義されているクラスのイテレータを返す
     * 
     * @return このファイルに定義されているクラスのイテレータ
     */
    public Iterator<ClassInfo> definedClassIterator() {
        Set<ClassInfo> unmodifiableDefinedClasses = Collections
                .unmodifiableSet(this.definedClasses);
        return unmodifiableDefinedClasses.iterator();
    }

    /**
     * 引数とこのファイルが等しいかを判定する．判定には，変数nameを用いる．
     * 
     * @param o 比較対象ファイル
     * @return 等しい場合は true, 等しくない場合は false
     */
    @Override
    public boolean equals(Object o) {

        if (null == o) {
            throw new NullPointerException();
        }

        String thisName = this.getName();
        String correspondName = ((FileInfo) o).getName();
        return thisName.equals(correspondName);
    }

    /**
     * 行数を返す．
     * 
     * @return 行数
     */
    public int getLOC() {
        return loc;
    }

    /**
     * ファイル名を返す． 現在フルパスで返すが，ディレクトリとファイル名を分けた方が良いかも．
     * 
     * @return ファイル名
     */
    public String getName() {
        return name;
    }

    /**
     * ファイルのハッシュコードを返す．ハッシュコードはファイル名（フルパス）を用いて計算される
     * 
     * @return このファイルのハッシュコード
     */
    @Override
    public int hashCode() {
        String name = this.getName();
        return name.hashCode();
    }

    /**
     * 変数 loc の setter．行数情報をセットする．
     * 
     * @param loc 行数
     */
    public void setLOC(final int loc) {

        MetricsToolSecurityManager.getInstance().checkAccess();
        if (loc < 0) {
            throw new IllegalArgumentException("LOC must be 0 or more!");
        }

        this.loc = loc;
    }

    /**
     * ファイルの行数を表す変数．
     */
    private int loc;

    /**
     * ファイル名を表す変数. ハッシュコードの計算に使っている．
     */
    private final String name;

    private final Set<ClassInfo> definedClasses;

    // TODO 宣言されているクラスの情報を追加
    // TODO importしているクラスの情報を追加
    // TODO includeしているファイルの情報を追加
}
