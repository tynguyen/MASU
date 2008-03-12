package jp.ac.osaka_u.ist.sel.metricstool.main.ast.java;

import jp.ac.osaka_u.ist.sel.metricstool.main.ast.databuilder.BuildDataManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.ast.databuilder.expression.ExpressionBuilder;
import jp.ac.osaka_u.ist.sel.metricstool.main.ast.databuilder.expression.ExpressionElement;
import jp.ac.osaka_u.ist.sel.metricstool.main.ast.databuilder.expression.ExpressionElementManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.ast.token.AstToken;
import jp.ac.osaka_u.ist.sel.metricstool.main.ast.visitor.AstVisitEvent;

public class JavaArrayInstantiationBuilder extends ExpressionBuilder{

    public JavaArrayInstantiationBuilder(final ExpressionElementManager expressionManager, final BuildDataManager buildDataManager) {
        super(expressionManager, buildDataManager);
    }

    @Override
    protected void afterExited(AstVisitEvent event) {
        AstToken token = event.getToken();
        if (token.equals(JavaAstToken.ARRAY_INSTANTIATION)){
            ExpressionElement[] elements = getAvailableElements();
            JavaArrayInstantiationElement array = JavaArrayInstantiationElement.getInstance();
            
            for(ExpressionElement element : elements){
                if (element.equals(array)){
                    pushElement(array);
                }
            }
            
            pushElement(array);
        }
    }

    @Override
    protected boolean isTriggerToken(AstToken token) {
        return token.equals(JavaAstToken.ARRAY_INIT) || token.equals(JavaAstToken.ARRAY_INSTANTIATION);
    }
}
