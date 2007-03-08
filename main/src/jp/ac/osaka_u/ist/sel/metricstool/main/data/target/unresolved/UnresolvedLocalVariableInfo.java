package jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved;


import java.util.Set;

import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ArrayTypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ClassInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ClassReferenceInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.FieldInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.LocalVariableInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.MethodInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ModifierInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TargetClassInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TargetMethodInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.UnknownTypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.external.ExternalClassInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.security.MetricsToolSecurityManager;


/**
 * ローカル変数を表すためのクラス． 以下の情報を持つ．
 * <ul>
 * <li>変数名</li>
 * <li>未解決型名</li>
 * </ul>
 * 
 * @author y-higo
 * 
 */
public final class UnresolvedLocalVariableInfo extends UnresolvedVariableInfo<LocalVariableInfo> {

    /**
     * ローカル変数ブジェクトを初期化する．
     * 
     * @param name 変数名
     * @param type 未解決型名
     */
    public UnresolvedLocalVariableInfo(final String name, final UnresolvedTypeInfo type) {
        super(name, type);
    }

    /**
     * 未解決ローカル変数情報を解決し，解決済み参照を返す．
     * 
     * @param usingClass 未解決ローカル変数の定義が行われているクラス
     * @param usingMethod 未解決ローカル変数の定義が行われているメソッド
     * @param classInfoManager 用いるクラスマネージャ
     * @param fieldInfoManager 用いるフィールドマネージャ
     * @param methodInfoManager 用いるメソッドマネージャ
     * @return 解決済みローカル変数情報
     */
    public LocalVariableInfo resolveUnit(final TargetClassInfo usingClass,
            final TargetMethodInfo usingMethod, final ClassInfoManager classInfoManager,
            final FieldInfoManager fieldInfoManager, final MethodInfoManager methodInfoManager) {

        // 不正な呼び出しでないかをチェック
        MetricsToolSecurityManager.getInstance().checkAccess();
        if ((null == usingClass) || (null == usingMethod) || (null == classInfoManager)) {
            throw new NullPointerException();
        }

        // 既に解決済みである場合は，キャッシュを返す
        if (this.alreadyResolved()) {
            return this.getResolvedUnit();
        }

        // 修飾子，変数名，型を取得
        final Set<ModifierInfo> localModifiers = this.getModifiers();
        final String variableName = this.getName();
        final UnresolvedTypeInfo unresolvedVariableType = this.getType();
        TypeInfo variableType = unresolvedVariableType.resolveType(usingClass, usingMethod,
                classInfoManager, null, null);
        assert variableType != null : "resolveTypeInfo returned null!";
        if (variableType instanceof UnknownTypeInfo) {
            if (unresolvedVariableType instanceof UnresolvedClassReferenceInfo) {
                variableType = NameResolver
                        .createExternalClassInfo((UnresolvedClassReferenceInfo) unresolvedVariableType);
                classInfoManager.add((ExternalClassInfo) variableType);
            } else if (unresolvedVariableType instanceof UnresolvedArrayTypeInfo) {
                final UnresolvedEntityUsageInfo unresolvedElementType = ((UnresolvedArrayTypeInfo) unresolvedVariableType)
                        .getElementType();
                final int dimension = ((UnresolvedArrayTypeInfo) unresolvedVariableType)
                        .getDimension();
                final ExternalClassInfo elementType = NameResolver
                        .createExternalClassInfo((UnresolvedClassReferenceInfo) unresolvedElementType);
                classInfoManager.add(elementType);
                variableType = ArrayTypeInfo
                        .getType(new ClassReferenceInfo(elementType), dimension);
            } else {
                assert false : "Can't resolve method local variable type : "
                        + unresolvedVariableType.toString();
            }
        }
        final int localFromLine = this.getFromLine();
        final int localFromColumn = this.getFromColumn();
        final int localToLine = this.getToLine();
        final int localToColumn = this.getToColumn();

        // ローカル変数オブジェクトを生成し，MethodInfoに追加
        this.resolvedInfo = new LocalVariableInfo(localModifiers, variableName, variableType,
                localFromLine, localFromColumn, localToLine, localToColumn);
        return this.resolvedInfo;
    }

}
