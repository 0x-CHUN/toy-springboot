package springboot.orm.handler;

import springboot.orm.core.MapperHelper;
import springboot.orm.core.MethodDetails;
import springboot.orm.core.SqlSource;
import springboot.orm.transaction.TransactionManager;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Prepare the statement
 */
public class PreparedStatementHandler {
    // the statement method
    private Method method;
    // the transaction manager
    private TransactionManager transactionManager;
    // the connection to RDMS
    private Connection connection;
    // the method param
    private Object[] args;

    public PreparedStatementHandler(TransactionManager transactionManager, Method method, Object[] args) throws SQLException {
        this.method = method;
        this.transactionManager = transactionManager;
        this.args = args;
        this.connection = transactionManager.getConnection();
    }

    /**
     * Inject the param into method, get the prepared statement
     */
    public PreparedStatement generateStatement() throws SQLException {
        // get the method details from cached objects
        MethodDetails methodDetails = MapperHelper.getMethodDetails(method);
        assert methodDetails != null;
        SqlSource sqlSource = methodDetails.getSqlSource();
        Class<?>[] paramTypes = methodDetails.getParamTypes();
        List<String> paramNames = methodDetails.getParamNames();
        List<String> params = sqlSource.getParam();
        List<Integer> paramInjectTypes = sqlSource.getInjectTypes();
        String sql = sqlSource.getSql();
        // parse the sql
        String parsedSql = parseSql(sql, paramTypes, paramNames, params, paramInjectTypes, args);
        PreparedStatement preparedStatement = connection.prepareStatement(parsedSql);
        //inject #{ } param
        preparedStatement = typeInject(preparedStatement, paramTypes, paramNames, params, paramInjectTypes, args);
        return preparedStatement;
    }

    /**
     * inject into ${ }
     *
     * @param sql              : the sql sentence
     * @param paramTypes       : the method's param types
     * @param paramNames       : the method's param names
     * @param params           : the params
     * @param paramInjectTypes : the params' inject type
     * @param args             : the params value
     */
    private String parseSql(String sql, Class<?>[] paramTypes, List<String> paramNames, List<String> params, List<Integer> paramInjectTypes, Object[] args) {
        StringBuilder sqlBuilder = new StringBuilder(sql);
        int index = sqlBuilder.indexOf("?");
        int i = 0;
        while (index > 0 && i < paramInjectTypes.size()) {
            if (paramInjectTypes.get(i) == 1) { // dynamic inject
                i++;
                continue;
            }
            String param = params.get(i);
            int paramIndex = paramNames.indexOf(param);
            Object arg = args[paramIndex];
            Class<?> type = paramTypes[paramIndex];
            String injectValue = "";
            if (String.class.equals(type)) {
                injectValue = "'" + arg + "'";
            } else if (Integer.class.equals(type)) {
                injectValue = Integer.toString((Integer) arg);
            } else if (Float.class.equals(type)) {
                injectValue = Float.toString((Float) arg);
            } else if (Double.class.equals(type)) {
                injectValue = Double.toString((Double) arg);
            } else if (Long.class.equals(type)) {
                injectValue = Long.toString((Long) arg);
            } else if (Short.class.equals(type)) {
                injectValue = Short.toString((Short) arg);
            }
            sqlBuilder.replace(index, index + 1, injectValue);
            index = sqlBuilder.indexOf("?");
            i++;
        }
        return sqlBuilder.toString();

    }

    /**
     * inject #{ }
     *
     * @param preparedStatement : the prepared statement
     * @param paramTypes        : the method's param types
     * @param paramNames        : the method's param names
     * @param params            : the params
     * @param paramInjectTypes  : the params' inject type
     * @param args              : the params value
     **/
    private PreparedStatement typeInject(PreparedStatement preparedStatement, Class<?>[] paramTypes, List<String> paramNames, List<String> params, List<Integer> paramInjectTypes, Object[] args) throws SQLException {
        for (int i = 0; i < paramNames.size(); i++) {
            String paramName = paramNames.get(i);
            Class<?> type = paramTypes[i];
            int injectIndex = params.indexOf(paramName);
            if (paramInjectTypes.get(injectIndex) == 0) {
                continue;
            }
            if (String.class.equals(type)) {
                if (injectIndex >= 0) {
                    preparedStatement.setString(injectIndex + 1, (String) args[i]);
                }
            } else if (Integer.class.equals(type) || int.class.equals(type)) {
                if (injectIndex >= 0) {
                    preparedStatement.setInt(injectIndex + 1, (Integer) args[i]);
                }
            } else if (Float.class.equals(type) || float.class.equals(type)) {
                if (injectIndex >= 0) {
                    preparedStatement.setFloat(injectIndex + 1, (Float) args[i]);
                }
            } else if (Double.class.equals(type) || double.class.equals(type)) {
                if (injectIndex >= 0) {
                    preparedStatement.setDouble(injectIndex + 1, (Double) args[i]);
                }
            } else if (Long.class.equals(type) || long.class.equals(type)) {
                if (injectIndex >= 0) {
                    preparedStatement.setLong(injectIndex + 1, (Long) args[i]);
                }
            }
        }
        return preparedStatement;
    }

    /**
     * Close the connection
     *
     * @throws SQLException
     */
    public void closeConnection() throws SQLException {
        transactionManager.close();
    }
}
