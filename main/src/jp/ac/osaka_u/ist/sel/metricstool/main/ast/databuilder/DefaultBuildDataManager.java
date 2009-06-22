package jp.ac.osaka_u.ist.sel.metricstool.main.ast.databuilder;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import jp.ac.osaka_u.ist.sel.metricstool.main.data.DataManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.BlockInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.CallInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.CallableUnitInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.LocalSpaceInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.StatementInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.UnitInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.VariableInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.VariableUsageInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedClassImportStatementInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedBlockInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedCallInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedCallableUnitInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedClassInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedFieldInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedLabelInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedLocalSpaceInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedLocalVariableInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedParameterInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedStatementInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedTypeParameterInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedUnitInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedVariableInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedVariableUsageInfo;


/**
 * ビルダーが構築する情報を管理して，情報全体の整合性を取るクラス.
 * 以下の3種類の機能を連携して行う.
 * 
 * 1. 構築中のデータに関する情報の管理，提供及び構築状態の管理
 * 
 * 2. 名前空間，エイリアス，変数などのスコープ管理
 * 
 * 3. クラス情報，メソッド情報，変数代入，変数参照，メソッド呼び出し情報などの登録作業の代行
 * 
 * @author kou-tngt
 *
 */
public class DefaultBuildDataManager implements BuildDataManager {

    public DefaultBuildDataManager() {
        innerInit();
    }

    public void reset() {
        innerInit();
    }

    public void addField(final UnresolvedFieldInfo field) {
        if (!this.classStack.isEmpty() && MODE.CLASS == this.mode) {
            this.classStack.peek().addDefinedField(field);
            addScopedVariable(field);
        }
    }

    public void addVariableUsage(
            UnresolvedVariableUsageInfo<? extends VariableUsageInfo<? extends VariableInfo<? extends UnitInfo>>> usage) {
        final UnresolvedLocalSpaceInfo<? extends LocalSpaceInfo> currentLocal = this
                .getCurrentLocalSpace();
        if (null != currentLocal) {
            currentLocal.addVariableUsage(usage);
        }
    }

    public void addLocalParameter(final UnresolvedLocalVariableInfo localParameter) {
        if (!this.callableUnitStack.isEmpty() && MODE.METHOD == this.mode) {
            this.callableUnitStack.peek().addLocalVariable(localParameter);
            addNextScopedVariable(localParameter);
        } else if (!this.blockStack.isEmpty() && MODE.INNER_BLOCK == this.mode) {
            this.blockStack.peek().addLocalVariable(localParameter);
            addNextScopedVariable(localParameter);
        }
    }

    public void addLocalVariable(final UnresolvedLocalVariableInfo localVariable) {
        if (!this.callableUnitStack.isEmpty() && MODE.METHOD == this.mode) {
            this.callableUnitStack.peek().addLocalVariable(localVariable);
            addScopedVariable(localVariable);
        } else if (!this.blockStack.isEmpty() && MODE.INNER_BLOCK == this.mode) {
            this.blockStack.peek().addLocalVariable(localVariable);
            addScopedVariable(localVariable);
        }
    }

    public void addMethodCall(
            UnresolvedCallInfo<? extends CallInfo<? extends CallableUnitInfo>> memberCall) {
        final UnresolvedLocalSpaceInfo<? extends LocalSpaceInfo> currentLocalSpace = this
                .getCurrentLocalSpace();
        if (null != currentLocalSpace) {
            currentLocalSpace.addCall(memberCall);
        }
    }

    public void addMethodParameter(final UnresolvedParameterInfo parameter) {
        if (!this.callableUnitStack.isEmpty() && MODE.METHOD == this.mode) {
            final UnresolvedCallableUnitInfo<? extends CallableUnitInfo> method = this.callableUnitStack
                    .peek();
            method.addParameter(parameter);
            addNextScopedVariable(parameter);
        }
    }

    /**
     * 現在のブロックスコープに変数を追加する.
     * @param var 追加する変数
     */
    private void addScopedVariable(
            UnresolvedVariableInfo<? extends VariableInfo<? extends UnitInfo>, ? extends UnresolvedUnitInfo<? extends UnitInfo>> var) {
        if (!scopeStack.isEmpty()) {
            scopeStack.peek().addVariable(var);
        }
    }

    /**
     * 現在から次のブロック終了までスコープが有効な変数を追加する.
     * @param var　追加する変数
     */
    private void addNextScopedVariable(
            UnresolvedVariableInfo<? extends VariableInfo<? extends UnitInfo>, ? extends UnresolvedUnitInfo<? extends UnitInfo>> var) {
        nextScopedVariables.add(var);
    }

    public void addTypeParameger(UnresolvedTypeParameterInfo typeParameter) {
        if (!this.modeStack.isEmpty() && MODE.CLASS == this.mode) {
            if (!this.classStack.isEmpty()) {
                classStack.peek().addTypeParameter(typeParameter);
            }
        } else if (!this.modeStack.isEmpty() && MODE.METHOD == this.mode) {
            if (!this.callableUnitStack.isEmpty()) {
                callableUnitStack.peek().addTypeParameter(typeParameter);
            }
        }
    }

    public void addUsingAliase(final String aliase, final String[] realName) {
        if (!this.scopeStack.isEmpty()) {
            final BlockScope scope = this.scopeStack.peek();
            scope.addAlias(aliase, realName);

            //名前のエイリアス情報が変化したのでキャッシュをリセット
            aliaseNameSetCache = null;
            allAvaliableNameSetCache = null;
        }
    }

    public void addUsingNameSpace(final String[] nameSpace) {
        if (!this.scopeStack.isEmpty()) {
            final BlockScope scope = this.scopeStack.peek();
            scope.addUsingNameSpace(nameSpace);

            //名前空間情報が変化したのでキャッシュをリセット
            availableNameSpaceSetCache = null;
            allAvaliableNameSetCache = null;
        }
    }

    public void addStatement(final UnresolvedStatementInfo<? extends StatementInfo> statement) {
        if (null == statement) {
            throw new IllegalArgumentException("statement is null");
        }

        final UnresolvedLocalSpaceInfo<? extends LocalSpaceInfo> currentLocal = this
                .getCurrentLocalSpace();
        assert null != currentLocal;
        if (null != currentLocal) {
            currentLocal.addStatement(statement);
        }
    }
    
    @Override
    public void addLabel(UnresolvedLabelInfo label) {
        if(null == label) {
            throw new IllegalArgumentException("label is null");
        }
        
        this.availableLabelsStack.peek().add(label);
        this.addStatement(label);
    }

    public UnresolvedLabelInfo getAvailableLabel(final String labelName) {
        if (this.availableLabelsStack.isEmpty()) {
            return null;
        }

        final List<UnresolvedLabelInfo> availableLabels = new ArrayList<UnresolvedLabelInfo>(
                this.availableLabelsStack.peek());
        Collections.reverse(availableLabels);
        for (final UnresolvedLabelInfo label : availableLabels) {
            if (label.getName().equals(labelName)) {
                return label;
            }
        }

        return null;
    }

    public void endScopedBlock() {
        if (!this.scopeStack.isEmpty()) {
            this.scopeStack.pop();
            nextScopedVariables.clear();

            //名前情報キャッシュをリセット
            aliaseNameSetCache = null;
            availableNameSpaceSetCache = null;
            allAvaliableNameSetCache = null;
        }
    }

    public UnresolvedClassInfo endClassDefinition() {
        this.restoreMode();

        if (this.classStack.isEmpty()) {
            return null;
        } else {
            final UnresolvedClassInfo classInfo = this.classStack.pop();

            //外側のクラスがない場合にだけ登録を行う
            if (this.classStack.isEmpty()) {
                DataManager.getInstance().getUnresolvedClassInfoManager().addClass(classInfo);
            }

            if (!this.callableUnitStack.isEmpty()) {
                //TODO methodStack.peek().addInnerClass(classInfo);
            }

            return classInfo;
        }
    }

    public UnresolvedCallableUnitInfo<? extends CallableUnitInfo> endCallableUnitDefinition() {
        this.restoreMode();

        if (this.callableUnitStack.isEmpty()) {
            return null;
        } else {
            final UnresolvedCallableUnitInfo<? extends CallableUnitInfo> callableUnit = this.callableUnitStack
                    .pop();

            this.nextScopedVariables.clear();
            this.availableLabelsStack.pop();
            return callableUnit;
        }
    }

    public UnresolvedBlockInfo<? extends BlockInfo> endInnerBlockDefinition() {
        this.restoreMode();

        if (this.blockStack.isEmpty()) {
            return null;
        } else {
            final UnresolvedBlockInfo<? extends BlockInfo> blockInfo = this.blockStack.pop();
            final UnresolvedLocalSpaceInfo<? extends LocalSpaceInfo> outerSpace = blockInfo
                    .getOuterSpace();

            outerSpace.addChildSpaceInfo(blockInfo);

            return blockInfo;
        }
    }

    public void enterClassBlock() {
        int size = classStack.size();
        if (size > 1) {
            UnresolvedClassInfo current = classStack.peek();
            UnresolvedClassInfo outer = classStack.get(size - 2);
            outer.addInnerClass(current);
            current.setOuterUnit(outer);
        }
    }

    public void enterMethodBlock() {

    }

    public List<UnresolvedClassImportStatementInfo> getAllAvaliableNames() {
        //      nullじゃなければ変化してないのでキャッシュ使いまわし
        if (null != allAvaliableNameSetCache) {
            return allAvaliableNameSetCache;
        }

        List<UnresolvedClassImportStatementInfo> resultSet = getAvailableAliasSet();
        for (UnresolvedClassImportStatementInfo info : getAvailableNameSpaceSet()) {
            resultSet.add(info);
        }

        allAvaliableNameSetCache = resultSet;

        return resultSet;
    }

    public List<UnresolvedClassImportStatementInfo> getAvailableNameSpaceSet() {
        //nullじゃなければ変化してないのでキャッシュ使いまわし
        if (null != availableNameSpaceSetCache) {
            return availableNameSpaceSetCache;
        }

        final List<UnresolvedClassImportStatementInfo> result = new LinkedList<UnresolvedClassImportStatementInfo>();
        //まず先に今の名前空間を登録
        if (null == currentNameSpaceCache) {
            currentNameSpaceCache = new UnresolvedClassImportStatementInfo(getCurrentNameSpace(), true);
        }
        result.add(currentNameSpaceCache);

        final int size = this.scopeStack.size();
        for (int i = size - 1; i >= 0; i--) {//Stackの実体はVectorなので後ろからランダムアクセス
            final BlockScope scope = this.scopeStack.get(i);
            final List<UnresolvedClassImportStatementInfo> scopeLocalNameSpaceSet = scope
                    .getAvailableNameSpaces();
            for (final UnresolvedClassImportStatementInfo info : scopeLocalNameSpaceSet) {
                result.add(info);
            }
        }
        availableNameSpaceSetCache = result;

        return result;
    }

    public List<UnresolvedClassImportStatementInfo> getAvailableAliasSet() {
        //nullじゃなければ変化してないのでキャッシュ使いまわし
        if (null != aliaseNameSetCache) {
            return aliaseNameSetCache;
        }

        final List<UnresolvedClassImportStatementInfo> result = new LinkedList<UnresolvedClassImportStatementInfo>();
        final int size = this.scopeStack.size();
        for (int i = size - 1; i >= 0; i--) {//Stackの実体はVectorなので後ろからランダムアクセス
            final BlockScope scope = this.scopeStack.get(i);
            final List<UnresolvedClassImportStatementInfo> scopeLocalNameSpaceSet = scope.getAvailableAliases();
            for (final UnresolvedClassImportStatementInfo info : scopeLocalNameSpaceSet) {
                result.add(info);
            }
        }

        aliaseNameSetCache = result;

        return result;
    }

    public String[] getAliasedName(final String alias) {
        final int size = this.scopeStack.size();
        for (int i = size - 1; i >= 0; i--) {//Stackの実体はVectorなので後ろからランダムアクセス
            final BlockScope scope = this.scopeStack.get(i);
            if (scope.hasAlias(alias)) {
                return scope.replaceAlias(alias);
            }
        }
        return EMPTY_NAME;
    }

    public UnresolvedUnitInfo<? extends UnitInfo> getCurrentUnit() {
        UnresolvedUnitInfo<? extends UnitInfo> currentUnit = null;
        if (MODE.CLASS == this.mode) {
            currentUnit = this.getCurrentClass();
        } else if (MODE.METHOD == this.mode) {
            currentUnit = this.getCurrentCallableUnit();
        } else if (MODE.INNER_BLOCK == this.mode) {
            currentUnit = this.getCurrentBlock();
        }
        return currentUnit;
    }

    @Override
    public UnresolvedLocalSpaceInfo<? extends LocalSpaceInfo> getCurrentLocalSpace() {
        UnresolvedLocalSpaceInfo<? extends LocalSpaceInfo> currentLocal = null;

        if (MODE.METHOD == this.mode) {
            currentLocal = this.getCurrentCallableUnit();
        } else if (MODE.INNER_BLOCK == this.mode) {
            currentLocal = this.getCurrentBlock();
        }

        return currentLocal;
    }

    public UnresolvedClassInfo getCurrentClass() {
        return this.classStack.isEmpty() ? null : this.classStack.peek();
    }

    public UnresolvedCallableUnitInfo<? extends CallableUnitInfo> getCurrentCallableUnit() {
        return this.callableUnitStack.isEmpty() ? null : this.callableUnitStack.peek();
    }

    public UnresolvedBlockInfo<? extends BlockInfo> getCurrentBlock() {
        return this.blockStack.isEmpty() ? null : this.blockStack.peek();
    }

    public int getAnonymousClassCount(UnresolvedClassInfo classInfo) {
        if (null == classInfo) {
            throw new NullPointerException("classInfo is null.");
        }

        if (anonymousClassCountMap.containsKey(classInfo)) {
            int count = anonymousClassCountMap.get(classInfo);
            anonymousClassCountMap.put(classInfo, ++count);
            return count;
        } else {
            anonymousClassCountMap.put(classInfo, 1);
            return 1;
        }
    }

    /**
     * 現在の名前空間名を返す．
     * 
     * @return
     */
    public String[] getCurrentNameSpace() {
        final List<String> nameSpaceList = new ArrayList<String>();

        for (final String[] nameSpace : this.nameSpaceStack) {
            for (final String nameSpaceString : nameSpace) {
                nameSpaceList.add(nameSpaceString);
            }
        }

        return nameSpaceList.toArray(new String[nameSpaceList.size()]);
    }

    /**
     * スタックにつまれているクラスのクラス名も付けた名前空間を返す.
     * @return
     */
    public String[] getCurrentFullNameSpace() {
        final List<String> nameSpaceList = new ArrayList<String>();

        for (final String[] nameSpace : this.nameSpaceStack) {
            for (final String nameSpaceString : nameSpace) {
                nameSpaceList.add(nameSpaceString);
            }
        }

        for (final UnresolvedClassInfo classes : this.classStack) {
            final String className = classes.getClassName();
            nameSpaceList.add(className);
        }

        return nameSpaceList.toArray(new String[nameSpaceList.size()]);
    }

    public UnresolvedVariableInfo<? extends VariableInfo<? extends UnitInfo>, ? extends UnresolvedUnitInfo<? extends UnitInfo>> getCurrentScopeVariable(
            String name) {

        for (UnresolvedVariableInfo<? extends VariableInfo<? extends UnitInfo>, ? extends UnresolvedUnitInfo<? extends UnitInfo>> var : nextScopedVariables) {
            if (name.equals(var.getName())) {
                return var;
            }
        }

        final int size = this.scopeStack.size();
        for (int i = size - 1; i >= 0; i--) {
            final BlockScope scope = this.scopeStack.get(i);
            if (scope.hasVariable(name)) {
                return scope.getVariable(name);
            }
        }
        return null;
    }

    public UnresolvedTypeParameterInfo getTypeParameter(String name) {
        for (int i = modeStack.size() - 1, cli = classStack.size() - 1, mei = callableUnitStack
                .size() - 1; i >= 0; i--) {
            MODE mode = modeStack.get(i);

            if (MODE.CLASS == mode) {
                assert (cli >= 0);
                if (cli >= 0) {
                    UnresolvedClassInfo classInfo = classStack.get(cli--);
                    for (UnresolvedTypeParameterInfo param : classInfo.getTypeParameters()) {
                        if (param.getName().equals(name)) {
                            return param;
                        }
                    }
                }
            } else if (MODE.METHOD == mode) {
                assert (mei >= 0);
                if (mei >= 0) {
                    UnresolvedCallableUnitInfo<?> methodInfo = callableUnitStack.get(mei--);
                    for (UnresolvedTypeParameterInfo param : methodInfo.getTypeParameters()) {
                        if (param.getName().equals(name)) {
                            return param;
                        }
                    }
                }
            }
        }

        return null;
    }

    public int getCurrentTypeParameterCount() {
        int count = -1;
        if (!this.modeStack.isEmpty() && MODE.CLASS == this.mode) {
            if (!this.classStack.isEmpty()) {
                count = classStack.peek().getTypeParameters().size();
            }
        } else if (!this.modeStack.isEmpty() && MODE.METHOD == this.mode) {
            if (!this.callableUnitStack.isEmpty()) {
                count = callableUnitStack.peek().getTypeParameters().size();
            }
        }

        return count;
    }

    public int getCurrentParameterCount() {
        int count = -1;
        if (!this.callableUnitStack.isEmpty()) {
            count = callableUnitStack.peek().getParameters().size();
        }
        return count;
    }

    public boolean hasAlias(final String name) {
        final int size = this.scopeStack.size();
        for (int i = size - 1; i >= 0; i--) {
            final BlockScope scope = this.scopeStack.get(i);
            if (scope.hasAlias(name)) {
                return true;
            }
        }
        return false;
    }

    public void startScopedBlock() {
        BlockScope newScope = new BlockScope();
        this.scopeStack.push(newScope);

        for (UnresolvedVariableInfo<? extends VariableInfo<? extends UnitInfo>, ? extends UnresolvedUnitInfo<? extends UnitInfo>> var : nextScopedVariables) {
            newScope.addVariable(var);
        }
    }

    public void pushNewNameSpace(final String[] nameSpace) {
        if (null == nameSpace) {
            throw new NullPointerException("nameSpace is null.");
        }

        if (0 == nameSpace.length) {
            throw new IllegalArgumentException("nameSpace has no entry.");
        }
        this.nameSpaceStack.push(nameSpace);
    }

    public String[] popNameSpace() {
        if (this.nameSpaceStack.isEmpty()) {
            return null;
        } else {
            return this.nameSpaceStack.pop();
        }
    }

    public String[] resolveAliase(String[] name) {
        if (name == null) {
            throw new NullPointerException("empty name.");
        }

        if (0 == name.length) {
            throw new IllegalArgumentException("empty name.");
        }

        List<String> resolvedName = new ArrayList<String>();
        int startPoint = 0;
        if (hasAlias(name[0])) {
            startPoint++;
            String[] aliasedName = getAliasedName(name[0]);
            for (String str : aliasedName) {
                resolvedName.add(str);
            }
        }

        for (int i = startPoint; i < name.length; i++) {
            resolvedName.add(name[i]);
        }

        return resolvedName.toArray(new String[resolvedName.size()]);
    }

    public void startClassDefinition(final UnresolvedClassInfo classInfo) {
        if (null == classInfo) {
            throw new NullPointerException("class info was null.");
        }

        classInfo.setNamespace(this.getCurrentFullNameSpace());
        
        final BlockScope currentScope = scopeStack.peek();
        classInfo.addImportStatements(currentScope.getAvailableAliases());
        classInfo.addImportStatements(currentScope.getAvailableNameSpaces());
        
        this.classStack.push(classInfo);

        this.toClassMode();
    }

    public void startCallableUnitDefinition(
            final UnresolvedCallableUnitInfo<? extends CallableUnitInfo> callableUnit) {
        if (null == callableUnit) {
            throw new NullPointerException("method info was null.");
        }

        this.callableUnitStack.push(callableUnit);

        this.availableLabelsStack.push(new ArrayList<UnresolvedLabelInfo>());

        this.toMethodMode();
    }

    public void startInnerBlockDefinition(final UnresolvedBlockInfo<? extends BlockInfo> blockInfo) {
        if (null == blockInfo) {
            throw new IllegalArgumentException("block info was null.");
        }

        this.toBlockMode();

        this.blockStack.push(blockInfo);
    }

    protected void toClassMode() {
        this.modeStack.push(this.mode);
        this.mode = MODE.CLASS;
    }

    protected void toMethodMode() {
        this.modeStack.push(this.mode);
        this.mode = MODE.METHOD;
    }

    protected void toBlockMode() {
        this.modeStack.push(this.mode);
        this.mode = MODE.INNER_BLOCK;
    }

    protected void restoreMode() {
        if (!modeStack.isEmpty()) {
            this.mode = modeStack.pop();
        }
    }

    protected static class BlockScope {
        private final Map<String, UnresolvedVariableInfo<? extends VariableInfo<? extends UnitInfo>, ? extends UnresolvedUnitInfo<? extends UnitInfo>>> variables = new LinkedHashMap<String, UnresolvedVariableInfo<? extends VariableInfo<? extends UnitInfo>, ? extends UnresolvedUnitInfo<? extends UnitInfo>>>();

        //        private final Map<String, String[]> nameAliases = new LinkedHashMap<String, String[]>();
        private final Map<String, UnresolvedClassImportStatementInfo> nameAliases = new LinkedHashMap<String, UnresolvedClassImportStatementInfo>();

        private final List<UnresolvedClassImportStatementInfo> availableNameSpaces = new LinkedList<UnresolvedClassImportStatementInfo>();

        public void addVariable(
                final UnresolvedVariableInfo<? extends VariableInfo<? extends UnitInfo>, ? extends UnresolvedUnitInfo<? extends UnitInfo>> variable) {
            this.variables.put(variable.getName(), variable);
        }

        public void addAlias(final String alias, final String[] name) {
            if (null == name || name.length == 0) {
                throw new IllegalArgumentException("Illegal name alias.");
            }

            final String[] tmp = new String[name.length];
            System.arraycopy(name, 0, tmp, 0, name.length);

            UnresolvedClassImportStatementInfo info = new UnresolvedClassImportStatementInfo(tmp, false);

            this.nameAliases.put(alias, info);
        }

        public void addUsingNameSpace(final String[] name) {
            final String[] tmp = new String[name.length];
            System.arraycopy(name, 0, tmp, 0, name.length);
            final UnresolvedClassImportStatementInfo info = new UnresolvedClassImportStatementInfo(tmp, true);
            this.availableNameSpaces.add(info);
        }

        public List<UnresolvedClassImportStatementInfo> getAvailableNameSpaces() {
            return this.availableNameSpaces;
        }

        public List<UnresolvedClassImportStatementInfo> getAvailableAliases() {
            List<UnresolvedClassImportStatementInfo> resultSet = new LinkedList<UnresolvedClassImportStatementInfo>();
            for (UnresolvedClassImportStatementInfo info : this.nameAliases.values()) {
                resultSet.add(info);
            }
            return resultSet;
        }

        public UnresolvedVariableInfo<? extends VariableInfo<? extends UnitInfo>, ? extends UnresolvedUnitInfo<? extends UnitInfo>> getVariable(
                String name) {
            return this.variables.get(name);
        }

        public boolean hasVariable(final String varName) {
            return this.variables.containsKey(varName);
        }

        public boolean hasAlias(final String alias) {
            return this.nameAliases.containsKey(alias);
        }

        public String[] replaceAlias(final String alias) {
            Set<String[]> cycleCheckSet = new HashSet<String[]>();

            String aliasString = alias;
            if (this.nameAliases.containsKey(aliasString)) {
                String[] result = this.nameAliases.get(aliasString).getImportName();
                cycleCheckSet.add(result);

                if (result.length == 1) {
                    aliasString = result[0];
                    while (this.nameAliases.containsKey(aliasString)) {
                        result = this.nameAliases.get(aliasString).getImportName();
                        if (result.length == 1) {
                            if (cycleCheckSet.contains(result)) {
                                return result;
                            } else {
                                cycleCheckSet.add(result);
                                aliasString = result[0];
                            }
                        } else {
                            return result;
                        }
                    }
                } else {
                    return result;
                }
            }
            return EMPTY_NAME;
        }
    }

    private void innerInit() {
        this.classStack.clear();
        this.callableUnitStack.clear();
        this.blockStack.clear();
        this.nameSpaceStack.clear();
        this.scopeStack.clear();
        this.availableLabelsStack.clear();

        this.scopeStack.add(new BlockScope());

        aliaseNameSetCache = null;
        availableNameSpaceSetCache = null;
        allAvaliableNameSetCache = null;
        currentNameSpaceCache = null;
    }

    private static final String[] EMPTY_NAME = new String[0];

    private List<UnresolvedClassImportStatementInfo> aliaseNameSetCache = null;

    private List<UnresolvedClassImportStatementInfo> availableNameSpaceSetCache = null;

    private List<UnresolvedClassImportStatementInfo> allAvaliableNameSetCache = null;

    private UnresolvedClassImportStatementInfo currentNameSpaceCache = null;

    private final Stack<BlockScope> scopeStack = new Stack<BlockScope>();

    private final Stack<String[]> nameSpaceStack = new Stack<String[]>();

    private final Stack<UnresolvedClassInfo> classStack = new Stack<UnresolvedClassInfo>();

    private final Stack<UnresolvedCallableUnitInfo<? extends CallableUnitInfo>> callableUnitStack = new Stack<UnresolvedCallableUnitInfo<? extends CallableUnitInfo>>();

    private final Stack<UnresolvedBlockInfo<? extends BlockInfo>> blockStack = new Stack<UnresolvedBlockInfo<? extends BlockInfo>>();

    private final Set<UnresolvedVariableInfo<? extends VariableInfo<? extends UnitInfo>, ? extends UnresolvedUnitInfo<? extends UnitInfo>>> nextScopedVariables = new HashSet<UnresolvedVariableInfo<? extends VariableInfo<? extends UnitInfo>, ? extends UnresolvedUnitInfo<? extends UnitInfo>>>();

    private final Map<UnresolvedClassInfo, Integer> anonymousClassCountMap = new HashMap<UnresolvedClassInfo, Integer>();

    private final Stack<List<UnresolvedLabelInfo>> availableLabelsStack = new Stack<List<UnresolvedLabelInfo>>();

    private MODE mode = MODE.INIT;

    private Stack<MODE> modeStack = new Stack<MODE>();

    private static enum MODE {
        INIT, INNER_BLOCK, METHOD, CLASS
    }
}
