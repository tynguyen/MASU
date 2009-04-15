package jp.ac.osaka_u.ist.sel.metricstool.pdg;


import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.VariableDeclarationStatementInfo;


/**
 * 変数宣言文を表すノード
 * 
 * @author higo
 *
 */
public class PDGVariableDeclarationStatementNode extends PDGStatementNode {

    public PDGVariableDeclarationStatementNode(
            final VariableDeclarationStatementInfo declarationStatement) {
        super(declarationStatement);
    }
}
