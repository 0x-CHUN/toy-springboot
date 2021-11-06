package springboot.orm.core;

import java.util.List;

public class MethodDetails {
    // method return type
    private Class<?> returnType;
    // return set?
    private boolean hasSet;
    // parameter types
    private Class<?>[] paramTypes;
    // parameter names
    private List<String> paramNames;
    // sql
    private SqlSource sqlSource;

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public boolean isHasSet() {
        return hasSet;
    }

    public void setHasSet(boolean hasSet) {
        this.hasSet = hasSet;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public List<String> getParamNames() {
        return paramNames;
    }

    public void setParamNames(List<String> paramNames) {
        this.paramNames = paramNames;
    }

    public SqlSource getSqlSource() {
        return sqlSource;
    }

    public void setSqlSource(SqlSource sqlSource) {
        this.sqlSource = sqlSource;
    }
}
