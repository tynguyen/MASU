package jp.ac.osaka_u.ist.sel.metricstool.main.data.target;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * assert文を表すクラス
 * 
 * @author t-miyake，higo
 *
 */
@SuppressWarnings("serial")
public final class AssertStatementInfo extends SingleStatementInfo {

    /**
     * アサート文を生成
     * 
     * @param ownerSpace 外側のブロック
     * @param assertedExpression 検証式
     * @param messageExpression メッセージ
     * @param fromLine 開始行
     * @param fromColumn 開始列
     * @param toLine 終了行
     * @param toColumn 終了列
     */
    public AssertStatementInfo(final LocalSpaceInfo ownerSpace,
            final ExpressionInfo assertedExpression, final ExpressionInfo messageExpression,
            final int fromLine, final int fromColumn, final int toLine, final int toColumn) {
        super(ownerSpace, fromLine, fromColumn, toLine, toColumn);

        if (null == assertedExpression) {
            throw new IllegalArgumentException("assertedExpressoin is null.");
        }

        this.assertedExpression = assertedExpression;
        this.messageExpression = messageExpression;

        this.assertedExpression.setOwnerExecutableElement(this);
        if (null != this.messageExpression) {
            this.messageExpression.setOwnerExecutableElement(this);
        }

    }

    /**
     * 検証式を返す
     * 
     * @return　検証式
     */
    public final ExpressionInfo getAssertedExpression() {
        return this.assertedExpression;
    }

    /**
     * メッセージを返す
     * 
     * @return　メッセージ
     */
    public final ExpressionInfo getMessageExpression() {
        return this.messageExpression;
    }

    @Override
    public Set<VariableUsageInfo<?>> getVariableUsages() {
        SortedSet<VariableUsageInfo<? extends VariableInfo<? extends UnitInfo>>> usages = new TreeSet<VariableUsageInfo<? extends VariableInfo<? extends UnitInfo>>>();
        usages.addAll(this.assertedExpression.getVariableUsages());
        usages.addAll(this.messageExpression.getVariableUsages());
        return Collections.unmodifiableSet(usages);
    }

    /**
     * 定義された変数のSetを返す
     * 
     * @return 定義された変数のSet
     */
    @Override
    public Set<VariableInfo<? extends UnitInfo>> getDefinedVariables() {
        return VariableInfo.EmptySet;
    }

    /**
     * 呼び出しのSetを返す
     * 
     * @return 呼び出しのSet
     */
    @Override
    public Set<CallInfo<?>> getCalls() {
        final Set<CallInfo<?>> calls = new HashSet<CallInfo<?>>();
        final ExpressionInfo assertedExpression = this.getAssertedExpression();
        calls.addAll(assertedExpression.getCalls());
        final ExpressionInfo messageExpression = this.getMessageExpression();
        calls.addAll(messageExpression.getCalls());
        return Collections.unmodifiableSet(calls);
    }

    /**
     * このアサート文のテキスト表現（String型）を返す
     * 
     * @return このアサート文のテキスト表現（String型）
     */
    @Override
    public String getText() {

        StringBuilder sb = new StringBuilder();
        sb.append("assert ");

        final ExpressionInfo expression = this.getAssertedExpression();
        sb.append(expression.getText());

        sb.append(";");

        return sb.toString();
    }

    private final ExpressionInfo assertedExpression;

    private final ExpressionInfo messageExpression;

}
