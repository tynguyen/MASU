package jp.ac.osaka_u.ist.sel.metricstool.main.data.target;


import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * 変数使用を表す抽象クラス
 * 
 * @author higo
 * @param <T> 使用されている変数
 */
@SuppressWarnings("serial")
public abstract class VariableUsageInfo<T extends VariableInfo<? extends UnitInfo>> extends
        ExpressionInfo {

    /**
     * 変数使用のCollectionから使用されている変数のSetを返す
     * 
     * @param variableUsages 変数使用のCollection
     * @return 使用されている変数のSet
     */
    public static Set<VariableInfo<? extends UnitInfo>> getUsedVariables(
            Collection<VariableUsageInfo<? extends VariableInfo<? extends UnitInfo>>> variableUsages) {

        final Set<VariableInfo<?>> usedVariables = new HashSet<VariableInfo<?>>();
        for (final VariableUsageInfo<?> variableUsage : variableUsages) {
            final VariableInfo<?> variable = variableUsage.getUsedVariable();
            usedVariables.add(variable);
        }
        return Collections.unmodifiableSet(usedVariables);
    }

    /**
     * 引数で与えられてた変数使用に含まれる変数参照のSetを返す
     * 
     * @param variableUsages 変数使用のSet
     * @return 引数で与えられてた変数使用に含まれる変数参照のSet
     */
    public static Set<VariableUsageInfo<? extends VariableInfo<? extends UnitInfo>>> getReferencees(
            Collection<VariableUsageInfo<? extends VariableInfo<? extends UnitInfo>>> variableUsages) {

        final Set<VariableUsageInfo<?>> references = new HashSet<VariableUsageInfo<?>>();
        for (final VariableUsageInfo<?> variableUsage : variableUsages) {
            if (variableUsage.isReference()) {
                references.add(variableUsage);
            }
        }

        return Collections.unmodifiableSet(references);
    }

    /**
     * 引数で与えられてた変数使用に含まれる変数代入のSetを返す
     * 
     * @param variableUsages 変数使用のSet
     * @return 引数で与えられてた変数使用に含まれる変数代入のSet
     */
    public static Set<VariableUsageInfo<? extends VariableInfo<? extends UnitInfo>>> getAssignments(
            Collection<VariableUsageInfo<? extends VariableInfo<? extends UnitInfo>>> variableUsages) {

        final Set<VariableUsageInfo<?>> assignments = new HashSet<VariableUsageInfo<?>>();
        for (final VariableUsageInfo<?> variableUsage : variableUsages) {
            if (variableUsage.isAssignment()) {
                assignments.add(variableUsage);
            }
        }

        return Collections.unmodifiableSet(assignments);
    }

    /**
     * 
     * @param usedVariable 使用されている変数
     * @param reference 参照かどうか
     * @param assignment 代入かどうか
     * @param ownerMethod オーナーメソッド
     * @param fromLine 開始行
     * @param fromColumn 開始列
     * @param toLine 終了行
     * @param toColumn 終了列
     */
    VariableUsageInfo(final T usedVariable, final boolean reference, final boolean assignment,
            final CallableUnitInfo ownerMethod, final int fromLine, final int fromColumn,
            final int toLine, final int toColumn) {

        super(ownerMethod, fromLine, fromColumn, toLine, toColumn);

        this.usedVariable = usedVariable;
        this.reference = reference;
        this.assignment = assignment;
    }

    /**
     * 使用されている変数を返す
     * 
     * @return 使用されている変数
     */
    public final T getUsedVariable() {
        return this.usedVariable;
    }

    /**
     * 参照か代入かを返す
     * 
     * @return 参照である場合は true，代入である場合は false
     */
    public final boolean isReference() {
        return this.reference;
    }

    /**
     * このフィールド使用が代入であるかどうかを返す
     * 
     * @return 代入である場合は true，参照である場合は false
     */
    public final boolean isAssignment() {
        return this.assignment;
    }

    @Override
    public Set<VariableUsageInfo<?>> getVariableUsages() {
        final SortedSet<VariableUsageInfo<?>> variableUsage = new TreeSet<VariableUsageInfo<?>>();
        variableUsage.add(this);
        return Collections.unmodifiableSortedSet(variableUsage);
    }

    /**
     * 呼び出しのSetを返す
     * 
     * @return 呼び出しのSet
     */
    @Override
    public Set<CallInfo<?>> getCalls() {
        return CallInfo.EmptySet;
    }

    /**
     * この変数使用のテキスト表現（型）を返す
     * 
     * @return この変数使用のテキスト表現（型）
     */
    @Override
    public String getText() {
        final T variable = this.getUsedVariable();
        return variable.getName();
    }

    /**
     * 変数使用の型を返す
     * 
     * @return 変数使用の型
     */
    @Override
    public TypeInfo getType() {

        final T usedVariable = this.getUsedVariable();
        final TypeInfo definitionType = usedVariable.getType();
        return definitionType;

        /*
        // 定義の返り値が型パラメータでなければそのまま返せる
        if (!(definitionType instanceof TypeParameterTypeInfo)) {
            return definitionType;
        }

        ///// 以下の実装はJavaのメソッドがFirstClassでないことを前提としての実装となる

        // 型パラメータから，実際に使用されている型を取得し返す
        // メソッドの型パラメータかどうか
        final CallableUnitInfo ownerMethod = this.getOwnerMethod();
        returnType = this.getTypeFromOwnerCallableUnit(definitionType, ownerMethod);
        if (null != returnType) {
            return returnType;
        }

        // 型パラメタの定義を探索する
        for (UnitInfo ownerUnit = ownerMethod.getOwnerClass(); true;) {
            // クラス、メソッド、イニシャライザのどれかがくる
            if (ownerUnit instanceof ClassInfo) {
                // クラスの場合
                ClassInfo ownerClass = (ClassInfo) ownerUnit;
                returnType = this.getTypeFromOwnerClass(definitionType, ownerClass);
                if (null != returnType) {
                    return returnType;
                } else if (!(ownerClass instanceof TargetInnerClassInfo)) {
                    // 探索の終わり、TargetClassに到達したにも関わらず
                    // returnTypeがnull。型パラメタの定義が欠落していることを意味する
                    break;
                }
                // 内部クラスの場合、一つ上のUnitを見るよ
                ownerUnit = ((TargetInnerClassInfo) ownerClass).getOuterUnit();
            } else if (ownerUnit instanceof CallableUnitInfo) {
                //メソッドおよびイニシャライザの場合
                CallableUnitInfo ownerCallable = (CallableUnitInfo) ownerUnit;
                returnType = this.getTypeFromOwnerCallableUnit(definitionType, ownerCallable);
                if (null != returnType) {
                    return returnType;
                }
                //一つ上のUnitを見るよ
                ownerUnit = ownerCallable.getOwnerClass();
            }
        }
        throw new IllegalStateException();
        */
    }

    /** 
     * 変数の型と親クラスを渡して、型パラメタを返す<br />
     * rev.1258の{@link #getType()}の一部分を抽出したもの 
     * @param definitionType 変数が定義された型
     * @param ownerClass 親クラス
     * @return 親クラスで定義された場合、その型パラメタを、なければnullを返す
     */
    private TypeInfo getTypeFromOwnerClass(final TypeInfo definitionType, final ClassInfo ownerClass) {
        //　型パラメータがそのままか
        for (final TypeParameterInfo typeParameter : ownerClass.getTypeParameters()) {
            if (typeParameter.equals(definitionType)) {
                return ((TypeParameterTypeInfo) definitionType).getReferncedTypeParameter()
                        .getExtendsType();
            }
        }

        //　親クラスで定義された型パラメータか
        final Map<TypeParameterInfo, TypeInfo> typeParameterUsages = ownerClass
                .getTypeParameterUsages();
        for (final Map.Entry<TypeParameterInfo, TypeInfo> entry : typeParameterUsages.entrySet()) {
            final TypeParameterInfo typeParameter = entry.getKey();
            if (typeParameter.equals(definitionType)) {
                return entry.getValue();
            }
        }

        return null;
    }

    /** 
     * 変数の型と、その変数が出現しているCallableUnitを渡して、型パラメタを返す<br />
     * rev.1258の{@link #getType()}の一部分を抽出したもの 
     * @param definitionType 変数が定義された型
     * @param ownerClass 親クラス
     * @return 親クラスで定義された場合、その型パラメタを、なければnullを返す
     */
    private TypeInfo getTypeFromOwnerCallableUnit(final TypeInfo definitionType,
            final CallableUnitInfo ownerCallable) {
        // イニシャライザに型パラメタは存在しない
        if (ownerCallable instanceof InitializerInfo) {
            return null;
        }

        for (final TypeParameterInfo typeParameter : ownerCallable.getTypeParameters()) {
            if (typeParameter.equals(definitionType)) {
                return ((TypeParameterInfo) definitionType).getExtendsType();
            }
        }
        return null;
    }

    /**
     * この式で投げられる可能性がある例外のSetを返す
     * 
     * @return　この式で投げられる可能性がある例外のSet
     */
    @Override
    public final Set<ReferenceTypeInfo> getThrownExceptions() {
        return Collections.unmodifiableSet(new HashSet<ReferenceTypeInfo>());
    }

    private final T usedVariable;

    private final boolean reference;

    private final boolean assignment;

    /**
     * 空の変数利用のSetを表す
     */
    public static final SortedSet<VariableUsageInfo<? extends VariableInfo<? extends UnitInfo>>> EmptySet = Collections
            .unmodifiableSortedSet(new TreeSet<VariableUsageInfo<? extends VariableInfo<? extends UnitInfo>>>());
}
