package sdl.ist.osaka_u.newmasu;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import sdl.ist.osaka_u.newmasu.util.VERSION;

//import jp.ac.osaka_u.ist.sel.metricstool.main.security.MetricsToolSecurityManager;
//import jp.ac.osaka_u.ist.sel.metricstool.main.util.LANGUAGE;
//import jp.ac.osaka_u.ist.sel.metricstool.main.util.UnavailableLanguageException;

public class Settings {

	private static Settings INSTANCE = null;

	public static Settings getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new Settings();
		}
		return INSTANCE;
	}

	private Settings() {
		this.targetDirectories = new HashSet<String>();
		this.listFiles = new HashSet<String>();
//		this.language = null;
		this.fileMetricsFile = null;
		this.classMetricsFile = null;
		this.methodMetricsFile = null;
		this.fieldMetricsFile = null;
		this.statement = true;
		this.libraries = new LinkedList<String>();
	}

	/**
	 *
	 * @return 解析対象ディレクトリ
	 *
	 *         解析対象ディレクトリを返す．
	 *
	 */
	public Set<String> getTargetDirectories() {
		return Collections.unmodifiableSet(this.targetDirectories);
	}

	public void addTargetDirectory(final String targetDirectory) {
//		MetricsToolSecurityManager.getInstance().checkAccess();
		if (null == targetDirectory) {
			throw new IllegalArgumentException();
		}
		this.targetDirectories.add(targetDirectory);
	}

	public void setTargetDirectory(final String targetDirectory) {
		if (null == targetDirectory) {
			throw new IllegalArgumentException();
		}
		this.targetDirectories.clear();
		this.addTargetDirectory(targetDirectory);
	}

	/**
	 * 解析対象ファイルの記述言語を返す
	 *
	 * @return 解析対象ファイルの記述言語
	 * @throws UnavailableLanguageException
	 *             利用不可能な言語が指定されている場合にスローされる
	 */
//	public LANGUAGE getLanguage() throws UnavailableLanguageException {
//		assert null != this.language : "\"language\" is not set";
//		return this.language;
//	}
//
//	public void setLanguage(final String language) {
//
//		MetricsToolSecurityManager.getInstance().checkAccess();
//		if (null == language) {
//			throw new IllegalArgumentException();
//		}
//
//		if (language.equalsIgnoreCase("java")
//				|| language.equalsIgnoreCase("java15")) {
//			this.language = LANGUAGE.JAVA15;
//			final File file = new File("./resource/jdk160java.lang.jar");
//			if (file.exists()) {
//				this.libraries.add(file.getAbsolutePath());
//			}
//		} else if (language.equalsIgnoreCase("java14")) {
//			this.language = LANGUAGE.JAVA14;
//			final File file = new File("./resource/jdk142java.lang.jar");
//			if (file.exists()) {
//				this.libraries.add(file.getAbsolutePath());
//			}
//		} else if (language.equalsIgnoreCase("java13")) {
//			this.language = LANGUAGE.JAVA13;
//			final File file = new File("./resource/jdk142java.lang.jar");
//			if (file.exists()) {
//				this.libraries.add(file.getAbsolutePath());
//			}
//		} else {
//			throw new UnavailableLanguageException("\"" + language
//					+ "\" is not an available programming language!");
//		}
//	}

	public VERSION getVersion(){
		return this.version;
	}

	public void setVersion(final String version) {

		if (null == version || version.equalsIgnoreCase("1.7")) {
			this.version = VERSION.JAVA17;
		} else if (version.equalsIgnoreCase("1.6")) {
			this.version = VERSION.JAVA16;
		} else if (version.equalsIgnoreCase("1.5")) {
			this.version = VERSION.JAVA15;
		} else if (version.equalsIgnoreCase("1.4")) {
			this.version = VERSION.JAVA14;
		} else if (version.equalsIgnoreCase("1.3")) {
			this.version = VERSION.JAVA13;
		} else {
			throw new IllegalArgumentException();
		}

	}

	/**
	 *
	 * @return 解析対象ファイルのパスを記述しているファイル
	 *
	 *         解析対象ファイルのパスを記述しているファイルのパスを返す
	 *
	 */
	public Set<String> getListFiles() {
		return Collections.unmodifiableSet(this.listFiles);
	}

	public void addListFile(final String listFile) {
//		MetricsToolSecurityManager.getInstance().checkAccess();
		if (null == listFile) {
			throw new IllegalArgumentException();
		}
		this.listFiles.add(listFile);
	}

	/**
	 *
	 * @return ファイルタイプのメトリクスを出力するファイル
	 *
	 *         ファイルタイプのメトリクスを出力するファイルのパスを返す
	 *
	 */
	public String getFileMetricsFile() {
		return this.fileMetricsFile;
	}

	public void setFileMetricsFile(final String fileMetricsFile) {
//		MetricsToolSecurityManager.getInstance().checkAccess();
		if (null == fileMetricsFile) {
			throw new IllegalArgumentException();
		}
		this.fileMetricsFile = fileMetricsFile;
	}

	/**
	 *
	 * @return クラスタイプのメトリクスを出力するファイル
	 *
	 *         クラスタイプのメトリクスを出力するファイルのパスを返す
	 *
	 */
	public String getClassMetricsFile() {
		return this.classMetricsFile;
	}

	public void setClassMetricsFile(final String classMetricsFile) {
//		MetricsToolSecurityManager.getInstance().checkAccess();
		if (null == classMetricsFile) {
			throw new IllegalArgumentException();
		}
		this.classMetricsFile = classMetricsFile;
	}

	/**
	 *
	 * @return メソッドタイプのメトリクスを出力するファイル
	 *
	 *         メソッドタイプのメトリクスを出力するファイルのパスを返す
	 *
	 */
	public String getMethodMetricsFile() {
		return methodMetricsFile;
	}

	public void setMethodMetricsFile(final String methodMetricsFile) {
//		MetricsToolSecurityManager.getInstance().checkAccess();
		if (null == methodMetricsFile) {
			throw new IllegalArgumentException();
		}
		this.methodMetricsFile = methodMetricsFile;
	}

	/**
	 *
	 * @return フィールドタイプのメトリクスを出力するファイル
	 */
	public String getFieldMetricsFile() {
		return this.fieldMetricsFile;
	}

	public void setFieldMetricsFile(final String fieldMetricsFile) {
//		MetricsToolSecurityManager.getInstance().checkAccess();
		if (null == fieldMetricsFile) {
			throw new IllegalArgumentException();
		}
		this.fieldMetricsFile = fieldMetricsFile;
	}

	/**
	 * 文情報を解析するかどうかを返す
	 *
	 * @return　解析する場合はtrue,しない場合はfalse
	 */
	public boolean isStatement() {
		return this.statement;
	}

	/**
	 * 文情報を解析するかどうかをセットする
	 *
	 * @param statement
	 *            解析する場合はtrue, しない場合はfalse
	 */
	public void setStatement(final boolean statement) {
		this.statement = statement;
	}

	/**
	 * ライブラリの位置を追加する． ライブラリとは，対象クラスの解析精度を上げるために与える解析対象外クラスのjarファイルや
	 * classファイルを置いているディレクトリ
	 *
	 * @param library
	 */
	public void addLibrary(final String library) {
//		MetricsToolSecurityManager.getInstance().checkAccess();
		if (null == library) {
			throw new IllegalArgumentException();
		}
		this.libraries.add(library);
	}

	/**
	 * ライブラリのListを返す
	 *
	 * @return ライブラリのList
	 */
	public List<String> getLibraries() {
		return Collections.unmodifiableList(this.libraries);
	}

	/**
	 * 解析対象ディレクトリを記録するための変数
	 */
	private final Set<String> targetDirectories;

	/**
	 * 解析対象ファイルのパスを記述したファイルのパスを記録するための変数
	 */
	private final Set<String> listFiles;

	/**
	 * 解析対象ファイルの記述言語を記録するための変数
	 */
//	private LANGUAGE language;

	/**
	 * 解析対象Javaファイルのバージョンを記録するための変数
	 */
	private VERSION version;

	/**
	 * ファイルタイプのメトリクスを出力するファイルのパスを記録するための変数
	 */
	private String fileMetricsFile;

	/**
	 * クラスタイプのメトリクスを出力するファイルのパスを記録するための変数
	 */
	private String classMetricsFile;

	/**
	 * メソッドタイプのメトリクスを出力するファイルのパスを記録するための変数
	 */
	private String methodMetricsFile;

	/**
	 * フィールドタイプのメトリクスを出力するファイルのパスを記録するための変数
	 */
	private String fieldMetricsFile;

	/**
	 * 文情報を取得するかどうかを記録するための変数
	 */
	private boolean statement;

	/**
	 * 外部クラスのパスを保存するための変数
	 */
	private List<String> libraries;

}
