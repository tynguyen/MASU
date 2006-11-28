package jp.ac.osaka_u.ist.sel.metricstool.main.data.accessor;


import jp.ac.osaka_u.ist.sel.metricstool.main.data.metric.MetricAlreadyRegisteredException;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ClassInfo;


/**
 * ���̃C���^�[�t�F�[�X�́C�N���X���g���N�X��o�^���邽�߂̃��\�b�h�Q��񋟂���D
 * 
 * @author y-higo
 * 
 */
public interface ClassMetricsRegister {

    /**
     * �������̃N���X�̃��g���N�X�l�i�������j��o�^����
     * 
     * @param classInfo ���g���N�X�̌v���ΏۃN���X
     * @param value ���g���N�X�l
     * @throws �o�^���悤�Ƃ��Ă��郁�g���N�X�����ɓo�^����Ă���ꍇ�ɃX���[�����
     */
    void registMetric(ClassInfo classInfo, int value) throws MetricAlreadyRegisteredException;
}