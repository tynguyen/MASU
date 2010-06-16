package jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved;


import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.CallableUnitInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ClassInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.FieldInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.MethodInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ReferenceTypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.SuperTypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TargetClassInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.security.MetricsToolSecurityManager;


public class UnresolvedSuperTypeInfo implements UnresolvedTypeInfo<SuperTypeInfo> {

    public UnresolvedSuperTypeInfo(final UnresolvedReferenceTypeInfo<?> superType) {
        this.superType = superType;
    }

    public UnresolvedReferenceTypeInfo<?> getSuperType() {
        return this.superType;
    }

    @Override
    public boolean alreadyResolved() {
        return null != this.resolvedInfo;
    }

    @Override
    public SuperTypeInfo getResolved() {

        if (!this.alreadyResolved()) {
            throw new IllegalStateException();
        }

        return this.resolvedInfo;
    }

    @Override
    public SuperTypeInfo resolve(TargetClassInfo usingClass, CallableUnitInfo usingMethod,
            ClassInfoManager classInfoManager, FieldInfoManager fieldInfoManager,
            MethodInfoManager methodInfoManager) {

        // �s���ȌĂяo���łȂ������`�F�b�N
        MetricsToolSecurityManager.getInstance().checkAccess();
        if ((null == usingClass) || (null == usingMethod) || (null == classInfoManager)) {
            throw new NullPointerException();
        }

        // ���ɉ����ς݂ł���ꍇ�́C�L���b�V����Ԃ�
        if (this.alreadyResolved()) {
            return this.getResolved();
        }

        final UnresolvedReferenceTypeInfo<?> unresolvedSuperType = this.getSuperType();
        final ReferenceTypeInfo superType = unresolvedSuperType.resolve(usingClass, usingMethod,
                classInfoManager, fieldInfoManager, methodInfoManager);

        this.resolvedInfo = new SuperTypeInfo(superType);
        return this.resolvedInfo;
    }

    private final UnresolvedReferenceTypeInfo<?> superType;

    private SuperTypeInfo resolvedInfo;
}