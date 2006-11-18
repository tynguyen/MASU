package jp.ac.osaka_u.ist.sel.metricstool.main.plugin.loader;

/**
 * @author kou-tngt
 * 
 * ���̗�O�́C�v���O�C���̃f�B���N�g���\�����v���O�C���̋K���ɏ]���Ă��Ȃ��ꍇ�ɓ�������D
 * ��̓I�ɂ́Cplugin.xml���v���O�C���̃f�B���N�g�������ɑ��݂��Ȃ��ꍇ�Ȃǂł���D
 */
public class IllegalPluginDirectoryStructureException extends PluginLoadException {

    public IllegalPluginDirectoryStructureException() {
        super();
    }

    public IllegalPluginDirectoryStructureException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalPluginDirectoryStructureException(String message) {
        super(message);
    }

    public IllegalPluginDirectoryStructureException(Throwable cause) {
        super(cause);
    }

}