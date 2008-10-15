package jp.ac.osaka_u.ist.sel.metricstool.printmethods;


import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import jp.ac.osaka_u.ist.sel.metricstool.main.MetricsTool;
import jp.ac.osaka_u.ist.sel.metricstool.main.Settings;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ClassInfoManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ParameterInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TargetClassInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.TargetMethodInfo;


/**
 * 対象ファイルに含まれるJavaメソッドのシグニチャを出力するプログラム
 * 対象ファイルは引数で与えられる．
 * 
 * @author higo
 */
public class PrintMethods extends MetricsTool {

    public static void main(String[] args) {

        // 解析用設定
        try {

            Class c = Settings.class;
            Field language = c.getDeclaredField("language");
            language.setAccessible(true);
            language.set(null, "java");
            Field directory = c.getDeclaredField("targetDirectory");
            directory.setAccessible(true);
            directory.set(null, args[0]);

        } catch (NoSuchFieldException e) {
            System.out.println(e.getMessage());
        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }

        // 対象ディレクトリ以下のJavaファイルを登録し，解析
        final PrintMethods printMethods = new PrintMethods();
        printMethods.registerFilesFromDirectory();
        printMethods.analyzeTargetFiles();

        // 対象クラス一覧を取得
        final Set<TargetClassInfo> classes = ClassInfoManager.getInstance().getTargetClassInfos();
        for (final TargetClassInfo classInfo : classes) {

            // クラス名を出力
            System.out.println("class: " + classInfo.getFullQualifiedName("."));

            // 各クラス内のメソッド一覧を取得
            final Set<TargetMethodInfo> methods = classInfo.getDefinedMethods();
            for (final TargetMethodInfo methodInfo : methods) {

                System.out.println("\tmethod: " + methodInfo.getMethodName());

                // 各メソッドの引数一覧を取得
                System.out.print("\t\tparameters: ");
                final List<ParameterInfo> parameters = methodInfo.getParameters();
                for (final ParameterInfo parameterInfo : parameters) {

                    System.out.print(parameterInfo.getType().getTypeName() + " "
                            + parameterInfo.getName() + ", ");
                }
                System.out.println();

                // 各メソッドの返り値を取得
                System.out.println("\t\treturn type: " + methodInfo.getReturnType().getTypeName());
            }
        }
    }
}
