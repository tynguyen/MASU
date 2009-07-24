package jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved;


import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.CallableUnitInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ClassInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ExpressionInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.FieldInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ForeachConditionInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.LocalVariableInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.MethodInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TargetClassInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.security.MetricsToolSecurityManager;


public class UnresolvedForeachConditionInfo extends UnresolvedExpressionInfo<ForeachConditionInfo> {

    UnresolvedForeachConditionInfo(final int fromLine, final int fromColumn, final int toLine,
            final int toColumn) {
        super(fromLine, fromColumn, toLine, toColumn);
    }

    @Override
    public ForeachConditionInfo resolve(TargetClassInfo usingClass, CallableUnitInfo usingMethod,
            ClassInfoManager classInfoManager, FieldInfoManager fieldInfoManager,
            MethodInfoManager methodInfoManager) {

        // �s���ȌĂяo���łȂ������`�F�b�N
        MetricsToolSecurityManager.getInstance().checkAccess();
        if ((null == usingClass) || (null == usingMethod) || (null == classInfoManager)
                || (null == methodInfoManager)) {
            throw new NullPointerException();
        }

        // ���ɉ����ς݂ł���ꍇ�́C�L���b�V����Ԃ�
        if (this.alreadyResolved()) {
            return this.getResolved();
        }

        // ���� foreach���̈ʒu�����擾
        final int fromLine = this.getFromLine();
        final int fromColumn = this.getFromColumn();
        final int toLine = this.getToLine();
        final int toColumn = this.getToColumn();

        // �J��Ԃ��p�̎����擾
        final UnresolvedExpressionInfo<?> unresolvedIteratorExpression = this
                .getIteratorExpression();
        final ExpressionInfo iteratorExpression = unresolvedIteratorExpression.resolve(usingClass,
                usingMethod, classInfoManager, fieldInfoManager, methodInfoManager);

        // �J��Ԃ��p�̕ϐ����擾
        final UnresolvedLocalVariableInfo unresolvedIteratorVariable = this.getIteratorVariable();
        final LocalVariableInfo iteratorVariable = unresolvedIteratorVariable.resolve(usingClass,
                usingMethod, classInfoManager, fieldInfoManager, methodInfoManager);

        this.resolvedInfo = new ForeachConditionInfo(usingMethod, fromLine, fromColumn, toLine,
                toColumn, iteratorVariable, iteratorExpression);
        return this.resolvedInfo;
    }

    public UnresolvedLocalVariableInfo getIteratorVariable() {
        return this.iteratorVariable;
    }

    public void setIteratorVariable(final UnresolvedLocalVariableInfo iteratorVariable) {
        this.iteratorVariable = iteratorVariable;
    }

    public UnresolvedExpressionInfo getIteratorExpression() {
        return this.iteratorExpression;
    }

    public void setIteratorExpression(final UnresolvedExpressionInfo iteratorExpression) {
        this.iteratorExpression = iteratorExpression;
    }

    private UnresolvedLocalVariableInfo iteratorVariable;

    private UnresolvedExpressionInfo iteratorExpression;
}