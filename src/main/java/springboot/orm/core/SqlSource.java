package springboot.orm.core;

import java.util.ArrayList;
import java.util.List;

/**
 * translate string to sql
 */
public class SqlSource {
    // sql sentence
    private String sql;
    // sql params
    private List<String> params = new ArrayList<>();
    // inject types (0:concat,1:dynamic)
    private List<Integer> injectTypes = new ArrayList<>();
    // sql type
    private Integer executeType;

    public SqlSource(String sql) {
        this.sql = inject(sql);
    }

    private String inject(String sql) {
        String prefix1 = "${";
        String prefix2 = "#{";
        String suffix = "}";
        while ((sql.indexOf(prefix1) > 0 || sql.indexOf(prefix2) > 0) && sql.indexOf(suffix) > 0) {
            int idx1 = sql.indexOf(prefix1);
            int idx2 = sql.indexOf(prefix2);
            String sqlParamName;
            int injectType;
            if (idx1 > 0 && idx2 <= 0) {
                sqlParamName = sql.substring(idx1, sql.indexOf(suffix) + 1);
                injectType = 0;
            } else if (idx2 > 0 && idx1 <= 0) {
                sqlParamName = sql.substring(idx2, sql.indexOf(suffix) + 1);
                injectType = 1;
            } else if (idx1 > 0 && idx2 > 0 && idx1 < idx2) {
                sqlParamName = sql.substring(idx1, sql.indexOf(suffix) + 1);
                injectType = 0;
            } else if (idx1 > 0 && idx2 > 0 && idx1 > idx2) {
                sqlParamName = sql.substring(idx2, sql.indexOf(suffix) + 1);
                injectType = 1;
            } else {
                continue;
            }
            sql = sql.replace(sqlParamName, "?");
            if (injectType == 0) {
                params.add(sqlParamName.replace("${", "").replace("}", ""));
                injectTypes.add(0);
            } else {
                params.add(sqlParamName.replace("#{", "").replace("}", ""));
                injectTypes.add(1);
            }
        }
        return sql;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<String> getParam() {
        return params;
    }

    public void setParam(List<String> params) {
        this.params = params;
    }

    public List<Integer> getInjectTypes() {
        return injectTypes;
    }

    public void setInjectTypes(List<Integer> injectTypes) {
        this.injectTypes = injectTypes;
    }

    public Integer getExecuteType() {
        return executeType;
    }

    public void setExecuteType(Integer executeType) {
        this.executeType = executeType;
    }
}
