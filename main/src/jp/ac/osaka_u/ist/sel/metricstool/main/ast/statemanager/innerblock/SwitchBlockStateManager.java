package jp.ac.osaka_u.ist.sel.metricstool.main.ast.statemanager.innerblock;

import jp.ac.osaka_u.ist.sel.metricstool.main.ast.token.AstToken;

public class SwitchBlockStateManager extends InnerBlockStateManager {

    @Override
    protected boolean isDefinitionToken(AstToken token) {
        return token.isSwitch();
    }

}
