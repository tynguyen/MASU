package jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved;




/**
 * ���O��������Ă��Ȃ����ł��邱�Ƃ�\���C���^�[�t�F�[�X
 * 
 * @author y-higo
 */
public interface Unresolved<T> {

    /**
     * ���O�������ꂽ�����Z�b�g����
     * 
     * @param resolvedInfo ���O�������ꂽ���
     */
    void setResolvedInfo(T resolvedInfo);

    /**
     * ���O�������ꂽ����Ԃ�
     * 
     * @return ���O�������ꂽ���
     */
    T getResolvedInfo();
}