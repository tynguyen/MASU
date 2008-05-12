package jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved;


import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.CallableUnitInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ClassInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.FieldInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.LabelInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.MethodInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.StatementInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TargetClassInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.security.MetricsToolSecurityManager;


/**
 * ���������x����`��\���N���X
 * 
 * @author higo
 *
 */
public final class UnresolvedLabelInfo extends UnresolvedUnitInfo<LabelInfo> implements
        UnresolvedStatementInfo<LabelInfo> {

    public UnresolvedLabelInfo(final String name) {

        // �s���ȌĂяo���łȂ������`�F�b�N
        MetricsToolSecurityManager.getInstance().checkAccess();
        if (null == name) {
            throw new IllegalArgumentException();
        }

        this.name = name;
        this.labeledStatement = null;
        this.resolvedInfo = null;
    }

    /**
     * ���O�������s�����\�b�h
     */
    @Override
    public LabelInfo resolve(final TargetClassInfo usingClass, final CallableUnitInfo usingMethod,
            final ClassInfoManager classInfoManager, final FieldInfoManager fieldInfoManager,
            final MethodInfoManager methodInfoManager) {

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

        // ���̃��x���̈ʒu�����擾
        final int fromLine = this.getFromLine();
        final int fromColumn = this.getFromColumn();
        final int toLine = this.getToLine();
        final int toColumn = this.getToColumn();

        // ���̃��x���̖��O���擾
        final String name = this.getName();

        // ���̃��x�����t���������擾
        final UnresolvedStatementInfo<?> unresolvedLabeledStatement = this.getLabeledStatement();
        final StatementInfo labeledStatement = unresolvedLabeledStatement.resolve(usingClass,
                usingMethod, classInfoManager, fieldInfoManager, methodInfoManager);

        this.resolvedInfo = new LabelInfo(name, labeledStatement, fromLine, fromColumn, toLine,
                toColumn);
        return this.resolvedInfo;
    }

    /**
     * ���̃��x���̖��O��Ԃ�
     * 
     * @return ���̃��x���̖��O
     */
    public String getName() {
        return this.name;
    }

    /**
     * ���̃��x�����t���������Z�b�g����
     * 
     * @param labeledStatement ���̃��x�����t������
     */
    public void setLabeledStatement(final UnresolvedStatementInfo<?> labeledStatement) {
        this.labeledStatement = labeledStatement;
    }

    /**
     * ���̃��x�����t��������Ԃ�
     * 
     * @return ���̃��x�����t������
     */
    public UnresolvedStatementInfo<?> getLabeledStatement() {
        return this.labeledStatement;
    }

    private String name;

    private UnresolvedStatementInfo<?> labeledStatement;
}