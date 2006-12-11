package jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved;


import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jp.ac.osaka_u.ist.sel.metricstool.main.security.MetricsToolSecurityManager;


/**
 * 一度目のASTパースで取得したメソッド情報を一時的に格納するためのクラス．
 * 
 * 
 * @author y-higo
 * 
 */
public class UnresolvedMethodInfo {

    /**
     * メソッド情報を初期化，コンストラクタかどうかを与える
     * 
     * @param constructor コンストラクタかどうか
     */
    public UnresolvedMethodInfo(final String methodName, final UnresolvedTypeInfo returnType,
            final UnresolvedClassInfo ownerClass, final boolean constructor) {

        // 不正な呼び出しでないかをチェック
        MetricsToolSecurityManager.getInstance().checkAccess();
        if ((null == methodName) || (null == returnType) || (null == ownerClass)) {
            throw new NullPointerException();
        }

        this.methodName = methodName;
        this.returnType = returnType;
        this.ownerClass = ownerClass;
        this.constructor = constructor;
        this.parameterInfos = new LinkedList<UnresolvedParameterInfo>();
        this.methodCalls = new HashSet<UnresolvedMethodCall>();
        this.fieldReferences = new HashSet<UnresolvedFieldUsage>();
        this.fieldAssignments = new HashSet<UnresolvedFieldUsage>();
        this.localVariables = new HashSet<UnresolvedLocalVariableInfo>();
    }

    /**
     * コンストラクタかどうかを返す
     * 
     * @return コンストラクタの場合は true，そうでない場合は false
     */
    public boolean isConstructor() {
        return this.constructor;
    }

    /**
     * メソッド名を返す
     * 
     * @return メソッド名
     */
    public String getMethodName() {
        return this.methodName;
    }

    /**
     * メソッドの返り値の型を返す
     * 
     * @return メソッドの返り値の型
     */
    public UnresolvedTypeInfo getReturnType() {
        return this.returnType;
    }

    /**
     * このメソッドを定義しているクラスを返す
     * 
     * @return このメソッドを定義しているクラス
     */
    public UnresolvedClassInfo getOwnerClass() {
        return this.ownerClass;
    }

    /**
     * メソッドに引数を追加する
     * 
     * @param parameterInfo 追加する引数
     */
    public void adParameter(final UnresolvedParameterInfo parameterInfo) {

        // 不正な呼び出しでないかをチェック
        MetricsToolSecurityManager.getInstance().checkAccess();
        if (null == parameterInfo) {
            throw new NullPointerException();
        }

        this.parameterInfos.add(parameterInfo);
    }

    /**
     * メソッド呼び出しを追加する
     * 
     * @param methodCall メソッド呼び出し
     */
    public void addMethodCall(final UnresolvedMethodCall methodCall) {

        // 不正な呼び出しでないかをチェック
        MetricsToolSecurityManager.getInstance().checkAccess();
        if (null == methodCall) {
            throw new NullPointerException();
        }

        this.methodCalls.add(methodCall);
    }

    /**
     * フィールド参照を追加する
     * 
     * @param fieldUsage フィールド参照
     */
    public void addFieldReference(final UnresolvedFieldUsage fieldUsage) {

        // 不正な呼び出しでないかをチェック
        MetricsToolSecurityManager.getInstance().checkAccess();
        if (null == fieldUsage) {
            throw new NullPointerException();
        }

        this.fieldReferences.add(fieldUsage);
    }

    /**
     * フィールド代入を追加する
     * 
     * @param fieldUsage フィールド代入
     */
    public void addFieldAssignment(final UnresolvedFieldUsage fieldUsage) {

        // 不正な呼び出しでないかをチェック
        MetricsToolSecurityManager.getInstance().checkAccess();
        if (null == fieldUsage) {
            throw new NullPointerException();
        }

        this.fieldAssignments.add(fieldUsage);
    }

    /**
     * ローカル変数を追加する
     * 
     * @param localVariable ローカル変数 
     */
    public void addLocalVariable(final UnresolvedLocalVariableInfo localVariable){
    
        // 不正な呼び出しでないかをチェック
        MetricsToolSecurityManager.getInstance().checkAccess();
        if (null == localVariable) {
            throw new NullPointerException();
        }
        
        this.localVariables.add(localVariable);
    }
    
    /**
     * メソッドの引数のリストを返す
     * 
     * @return メソッドの引数のリスト
     */
    public List<UnresolvedParameterInfo> getParameterInfos() {
        return Collections.unmodifiableList(this.parameterInfos);
    }

    /**
     * メソッド呼び出しの Set を返す
     * 
     * @return メソッド呼び出しの Set
     */
    public Set<UnresolvedMethodCall> getMethodCalls() {
        return Collections.unmodifiableSet(this.methodCalls);
    }

    /**
     * フィールド参照の Set を返す
     * 
     * @return フィールド参照の Set
     */
    public Set<UnresolvedFieldUsage> getFieldReferences() {
        return Collections.unmodifiableSet(this.fieldReferences);
    }

    /**
     * フィールド代入の Set を返す
     * 
     * @return フィールド代入の Set
     */
    public Set<UnresolvedFieldUsage> getFieldAssignments() {
        return Collections.unmodifiableSet(this.fieldAssignments);
    }

    /**
     * 定義されているローカル変数の Set を返す
     * 
     * @return 定義されているローカル変数の Set 
     */
    public Set<UnresolvedLocalVariableInfo> getLocalVariables() {
        return Collections.unmodifiableSet(this.localVariables);
    }
    
    /**
     * このメソッドの行数を返す
     * 
     * @return メソッドの行数
     */
    public int getLOC() {
        return this.loc;
    }

    /**
     * このメソッドの行数を保存する
     * 
     * @param loc このメソッドの行数
     */
    public void setLOC(final int loc) {
        this.loc = loc;
    }

    /**
     * メソッド名を保存するための変数
     */
    private String methodName;

    /**
     * メソッド引数を保存するための変数
     */
    private final List<UnresolvedParameterInfo> parameterInfos;

    /**
     * メソッドの返り値を保存するための変数
     */
    private final UnresolvedTypeInfo returnType;

    /**
     * このメソッドを定義しているクラスを保存するための変数
     */
    private final UnresolvedClassInfo ownerClass;

    /**
     * コンストラクタかどうかを表す変数
     */
    private final boolean constructor;

    /**
     * メソッド呼び出しを保存する変数
     */
    private final Set<UnresolvedMethodCall> methodCalls;

    /**
     * フィールド参照を保存する変数
     */
    private final Set<UnresolvedFieldUsage> fieldReferences;

    /**
     * フィールド代入を保存する変数
     */
    private final Set<UnresolvedFieldUsage> fieldAssignments;

    /**
     * このメソッド内で定義されているローカル変数を保存する変数
     */
    private final Set<UnresolvedLocalVariableInfo> localVariables;
    
    /**
     * メソッドの行数を保存するための変数
     */
    private int loc;
}
