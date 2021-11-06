package springboot.orm.executor;

import springboot.orm.transaction.TransactionStatus;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;

/**
 * Executor can execute the sql
 */
public interface Executor {
    <E> List<E> select(Method method, Object[] args) throws Exception;

    int update(Method method, Object[] args) throws SQLException;

    void commit(TransactionStatus status) throws SQLException;

    void rollback() throws SQLException;

    void close() throws SQLException;
}
