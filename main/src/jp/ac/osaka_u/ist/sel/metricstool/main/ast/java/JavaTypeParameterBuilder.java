package jp.ac.osaka_u.ist.sel.metricstool.main.ast.java;

import jp.ac.osaka_u.ist.sel.metricstool.main.ast.databuilder.BuildDataManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.ast.databuilder.NameBuilder;
import jp.ac.osaka_u.ist.sel.metricstool.main.ast.databuilder.TypeParameterBuilder;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedTypeInfo;

/**
 * Java�̌^�p�����[�^�����\�z����D
 * 
 * {@link TypeParameterBuilder}�@�� {@link #getUpperBounds()}���\�b�h���I�[�o�[���C�h���āC
 * ����N���X���w�肳��Ă��Ȃ��ꍇ�́Cjava.lang.Object��\���^�����\�z����D
 * @author kou-tngt
 *
 */
public class JavaTypeParameterBuilder extends TypeParameterBuilder{

    /**
     * �e�N���X�̃R���X�g���N�^���Ăяo���D
     * @param buildDataManager
     */
    public JavaTypeParameterBuilder(BuildDataManager buildDataManager) {
        super(buildDataManager,new NameBuilder(),new JavaTypeBuilder(buildDataManager));
    }

    /**
     * �\�z����^�p�����[�^�̏���N���X��Ԃ��D
     * �e�N���X�ŏ���̌^������ł���΂�����C�ł��Ȃ����java.lang.Object��\���^����Ԃ��D
     * 
     * @return �e�N���X�ŏ���̌^������ł���΂��̏��C�ł��Ȃ����java.lang.Object��\���^���
     * @see jp.ac.osaka_u.ist.sel.metricstool.main.ast.databuilder.TypeParameterBuilder#getUpperBounds()
     */
    protected UnresolvedTypeInfo getUpperBounds(){
        UnresolvedTypeInfo extendsTypeInfo = super.getUpperBounds();
        if (null == extendsTypeInfo){
            return JavaTypeBuilder.JAVA_LANG_OBJECT;
        } else {
            return extendsTypeInfo;
        }
    }
}