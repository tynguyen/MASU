package jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved;


import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.PrimitiveTypeInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.external.ExternalClassInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.util.LANGUAGE;


/**
 * �^�ϊ����[�e�B���e�B�D���O�����ɗp����D
 * 
 * @author y-higo
 */
public abstract class TypeConverter {

    /**
     * �e��������̌^�ϊ����Ԃ��t�@�N�g�����\�b�h�i�̂悤�Ȃ��́j
     * 
     * @param language ����
     * @return ���̌���p�̌^�ϊ���
     */
    public static final TypeConverter getTypeConverter(final LANGUAGE language) {

        if (null == language) {
            throw new NullPointerException();
        }

        switch (language) {
        case JAVA:
            return JavaTypeConverter.SINGLETON;
        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * �����ŗ^����ꂽ�v���~�e�B�u�^�̃��b�p�[�N���X��Ԃ�
     * 
     * @param primitiveType �v���~�e�B�u�^
     * @return �Ή����郉�b�p�[�N���X
     */
    public abstract ExternalClassInfo getWrapperClass(PrimitiveTypeInfo primitiveType);

}