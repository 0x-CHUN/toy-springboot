package springboot.orm.executor;

import springboot.orm.core.MapperHelper;
import springboot.orm.core.SqlResultCache;
import springboot.orm.handler.PreparedStatementHandler;
import springboot.orm.handler.ResultSetHandler;
import springboot.orm.transaction.TransactionFactory;
import springboot.orm.transaction.TransactionManager;
import springboot.orm.transaction.TransactionStatus;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SimpleExecutor implements Executor {
    // Transaction manager
    public TransactionManager transactionManager;
    // cache for sql result
    public SqlResultCache sqlResultCache;

    public SimpleExecutor(boolean openTransaction, boolean openCache) {
        if (openCache) {
            this.sqlResultCache = new SqlResultCache();
        }
        if (openTransaction) {
            this.transactionManager = TransactionFactory.newTransaction(Connection.TRANSACTION_REPEATABLE_READ, false);
        } else {
            this.transactionManager = TransactionFactory.newTransaction();
        }
    }

    /**
     * Execute the select method
     *
     * @param method:the @Select method
     * @param args:      the param of @Select
     * @return the result of select sql
     */
    @Override
    public <E> List<E> select(Method method, Object[] args) throws Exception {

        String cacheKey = generateCacheKey(method, args);
        if (sqlResultCache != null && sqlResultCache.getCache(cacheKey) != null) {
            return (List<E>) sqlResultCache.getCache(cacheKey);
        }
        // prepare statement
        PreparedStatementHandler preparedStatementHandler = new PreparedStatementHandler(transactionManager, method, args);
        PreparedStatement preparedStatement = preparedStatementHandler.generateStatement();

        ResultSet resultSet;
        // execute the sql
        preparedStatement.executeQuery();
        resultSet = preparedStatement.getResultSet();

        // map resultSet into return object
        Class<?> returnClass = MapperHelper.getMethodDetails(method).getReturnType();
        if (returnClass == null || void.class.equals(returnClass)) {
            preparedStatement.close();
            preparedStatementHandler.closeConnection();
            return null;
        } else {
            ResultSetHandler resultSetHandler = new ResultSetHandler(returnClass, resultSet);
            List<E> res = resultSetHandler.handle();
            if (sqlResultCache != null) {
                sqlResultCache.putCache(cacheKey, res);
            }
            preparedStatement.close();
            resultSet.close();
            preparedStatementHandler.closeConnection();
            return res;

        }

    }

    /**
     * Execute the update,delete,insert method
     *
     * @param method: @Update, @Delete, @Insert method
     * @param args:   the param
     * @return the result of sql
     */
    @Override
    public int update(Method method, Object[] args) throws SQLException {
        PreparedStatementHandler preparedStatementHandler = null;
        PreparedStatement preparedStatement = null;
        int count;
        if (sqlResultCache != null) {
            sqlResultCache.cleanCache();
        }
        try {
            preparedStatementHandler = new PreparedStatementHandler(transactionManager, method, args);
            preparedStatement = preparedStatementHandler.generateStatement();
            count = preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            assert preparedStatementHandler != null;
            preparedStatementHandler.closeConnection();
        }
        return count;
    }

    @Override
    public void commit(TransactionStatus status) throws SQLException {
        transactionManager.commit(status);
    }

    @Override
    public void rollback() throws SQLException {
        transactionManager.rollback();
    }

    @Override
    public void close() throws SQLException {
        transactionManager.close();
    }

    /**
     * Generate key by the method and param
     *
     * @param method :  @Select, @Update, @Delete, @Insert method
     * @param args:  the method param
     * @return a string represent the key of method
     */
    private String generateCacheKey(Method method, Object[] args) {
        StringBuilder builder = new StringBuilder(method.getDeclaringClass().getName() + method.getName());
        for (Object arg : args) {
            builder.append(arg.toString());
        }
        return builder.toString();
    }
}
