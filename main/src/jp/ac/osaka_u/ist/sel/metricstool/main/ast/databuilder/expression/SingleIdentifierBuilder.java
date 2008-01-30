package jp.ac.osaka_u.ist.sel.metricstool.main.ast.databuilder.expression;

import jp.ac.osaka_u.ist.sel.metricstool.main.ast.databuilder.BuildDataManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.ast.token.AstToken;
import jp.ac.osaka_u.ist.sel.metricstool.main.ast.visitor.AstVisitEvent;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedClassReferenceInfo;

public class SingleIdentifierBuilder extends ExpressionBuilder{

    public SingleIdentifierBuilder(ExpressionElementManager expressionManager,BuildDataManager buildDataManager) {
        super(expressionManager);
        
        this.buildDataManager = buildDataManager;
    }

    @Override
    protected void afterExited(AstVisitEvent event) {
        AstToken token = event.getToken();
        if (token.isIdentifier()){
//            AvailableNamespaceInfoSet nameSpaceset = buildDataManager.getAllAvaliableNames();
//            UnresolvedReferenceTypeInfo unresolvedReference = new UnresolvedReferenceTypeInfo(nameSpaceset,new String[]{token.toString()});
        	UnresolvedClassReferenceInfo currentClassReference = InstanceSpecificElement.getThisInstanceType(buildDataManager);
            pushElement(new SingleIdentifierElement(token.toString(), currentClassReference));
        }
    }

    @Override
    protected boolean isTriggerToken(AstToken token) {
        return token.isIdentifier();
    }
    
    private final BuildDataManager buildDataManager;

}
