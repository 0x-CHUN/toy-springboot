package springboot.orm.transaction;

import springboot.orm.constant.PropagationLevel;
import springboot.orm.datasource.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Stack;

public class SimpleTransactionManager implements TransactionManager {
    // ThreadLocal ensure each thread has own connection
    private ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();
    // The upper-level transaction is connected to the cache,
    // and the stack can ensure the cache of multi-level transactions
    private ThreadLocal<Stack<Connection>> delayThreadLocal = new ThreadLocal<>();
    // datasource
    private DataSource dataSource;
    // isolation level
    private Integer level = Connection.TRANSACTION_REPEATABLE_READ;
    // autocommit
    private Boolean autocommit = true;

    public SimpleTransactionManager(DataSource dataSource) {
        this(dataSource, null, null);
    }

    public SimpleTransactionManager(DataSource dataSource, Integer level, Boolean autocommit) {
        this.dataSource = dataSource;
        if (level != null) {
            this.level = level;
        }
        if (autocommit != null) {
            this.autocommit = autocommit;
        }
    }

    /**
     * Get the connection from thread local if not then get a new connection
     */
    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = connectionThreadLocal.get();
        if (connection != null && !connection.isClosed()) {
            return connection;
        } else {
            Connection tempConnection = dataSource.getConnection();
            tempConnection.setAutoCommit(autocommit);
            tempConnection.setTransactionIsolation(level);
            connectionThreadLocal.set(tempConnection);
            return tempConnection;
        }
    }

    /**
     * The transaction is present? if connection is not closed and not autocommit,the return true
     */
    public boolean isTransactionPresent() {
        Connection connection = connectionThreadLocal.get();
        try {
            if (connection != null && !connection.isClosed() && !connection.getAutoCommit()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * start transaction
     */
    @Override
    public void beginTransaction(TransactionStatus status) throws SQLException {
        //PROPAGATION_REQUIRED：if there exist transaction then join it, if not create a new one
        if (status.propagationLevel == PropagationLevel.PROPAGATION_REQUIRED) {
            if (!status.isTrans) {
                doCreateTransaction(status.isolationLevel);
            }
        }
        //PROPAGATION_SUPPORTS：if there exist transaction then join it, if not run as not a transaction
        else if (status.propagationLevel == PropagationLevel.PROPAGATION_SUPPORTS) {
            if (!status.isTrans) {
            }
        }
        //PROPAGATION_MANDATORY：if there exist transaction then join it, if not throw error
        else if (status.propagationLevel == PropagationLevel.PROPAGATION_MANDATORY) {
            if (!status.isTrans) {
                throw new RuntimeException("Transaction is PROPAGATION_MANDATORY, but no transaction");
            }
        }
        //PROPAGATION_REQUIRES_NEW: recreate a new transaction,if there exists a transaction, delay the cur one
        else if (status.propagationLevel == PropagationLevel.PROPAGATION_REQUIRES_NEW) {
            if (status.isTrans) {
                // save cur transaction into thread local,delay run
                if (delayThreadLocal.get() == null) {
                    Stack<Connection> stack = new Stack<>();
                    delayThreadLocal.set(stack);
                }
                Stack<Connection> stack = delayThreadLocal.get();
                stack.push(connectionThreadLocal.get());
                delayThreadLocal.set(stack);
                connectionThreadLocal.remove();
            }
            // recreate a new transaction
            doCreateTransaction(status.isolationLevel);
        }
        //PROPAGATION_NOT_SUPPORTED: run as not a transaction, if there exists a transaction, delay cur one
        else if (status.propagationLevel == PropagationLevel.PROPAGATION_NOT_SUPPORTED) {
            if (status.isTrans) {
                if (delayThreadLocal.get() == null) {
                    Stack<Connection> stack = new Stack<>();
                    delayThreadLocal.set(stack);
                }
                Stack<Connection> stack = delayThreadLocal.get();
                stack.push(connectionThreadLocal.get());
                delayThreadLocal.set(stack);
                connectionThreadLocal.remove();
            }
        }
        //PROPAGATION_NEVER：run as not a transaction, if there exists a transaction, throw error
        else if (status.propagationLevel == PropagationLevel.PROPAGATION_NEVER) {
            if (status.isTrans) {
                throw new RuntimeException("PROPAGATION_NEVER, but exist a transaction");
            }
        }
        //PROPAGATION_NESTED: if no transaction then create one,else just run
        else if (status.propagationLevel == PropagationLevel.PROPAGATION_NESTED) {
            if (!status.isTrans) {
                doCreateTransaction(status.isolationLevel);
            }
        }

    }

    /**
     * create a new transaction.Get a connection and  set as not autocommit .
     * save if into connectionThreadLocal
     *
     * @param level isolation level
     */
    private void doCreateTransaction(Integer level) throws SQLException {
        Connection tempConnection = dataSource.getConnection();
        tempConnection.setAutoCommit(false);
        if (level != null) {
            tempConnection.setTransactionIsolation(level);
        } else {
            tempConnection.setTransactionIsolation(this.level);
        }
        connectionThreadLocal.set(tempConnection);
    }


    /**
     * Commit transaction
     */
    @Override
    public void commit(TransactionStatus status) throws SQLException {
        // not transaction, commit
        if (!status.isTrans) {
            Connection connection = connectionThreadLocal.get();
            if (connection != null && !connection.isClosed() && !connection.getAutoCommit()) {
                connection.commit();
            }
        }
        //has transaction, and is PROPAGATION_REQUIRES_NEW or PROPAGATION_NESTED, auto commit
        else if (status.isTrans && status.propagationLevel == PropagationLevel.PROPAGATION_REQUIRES_NEW) {
            Connection connection = connectionThreadLocal.get();
            if (connection != null && !connection.isClosed() && !connection.getAutoCommit()) {
                connection.commit();
            }
        } else if (status.isTrans && status.propagationLevel == PropagationLevel.PROPAGATION_NESTED) {
            Connection connection = connectionThreadLocal.get();
            if (connection != null && !connection.isClosed() && !connection.getAutoCommit()) {
                connection.commit();
            }
        }

    }

    /**
     * rollback
     */
    @Override
    public void rollback() throws SQLException {
        // if not auto commit, rollback
        Connection connection = connectionThreadLocal.get();
        if (connection != null && !connection.isClosed() && !connection.getAutoCommit()) {
            connection.rollback();
        }
    }

    /**
     * close connection
     */
    @Override
    public void close() throws SQLException {
        Connection connection = connectionThreadLocal.get();
        if (connection != null && !connection.isClosed() && connection.getAutoCommit()) {
            dataSource.releaseConnection(connection);
            //连接设为null
            connectionThreadLocal.remove();
        }
    }

    /**
     * close transaction,reset to not auto commit and default isolation level, and close it
     * if trans is auto commit is false, then rollback first
     */
    @Override
    public void closeTransaction(TransactionStatus status) throws SQLException {
        Connection connection = connectionThreadLocal.get();
        if (!status.isTrans && connection != null && !connection.isClosed() && !connection.getAutoCommit()) {
            connection.rollback();
            connection.setAutoCommit(autocommit);
            connection.setTransactionIsolation(level);
            dataSource.releaseConnection(connection);
            connectionThreadLocal.remove();
        } else if (status.isTrans && status.propagationLevel == PropagationLevel.PROPAGATION_REQUIRES_NEW) {
            connection.rollback();
            connection.setAutoCommit(autocommit);
            connection.setTransactionIsolation(level);
            dataSource.releaseConnection(connection);
            connectionThreadLocal.remove();
            connectionThreadLocal.set(delayThreadLocal.get().pop());
        } else if (status.isTrans && status.propagationLevel == PropagationLevel.PROPAGATION_NOT_SUPPORTED) {
            connectionThreadLocal.set(delayThreadLocal.get().pop());
        }
    }

    /**
     * set isolation level
     */
    @Override
    public void setLevel(Integer level) {
        this.level = level;
    }

    /**
     * set auto commit
     */
    @Override
    public void setAutocommit(Boolean autocommit) {
        this.autocommit = autocommit;
    }

    /**
     * Get the transaction, just return connection name
     */
    @Override
    public String getTransactionId() {
        Connection connection = connectionThreadLocal.get();
        String transactionId = null;
        try {
            if (connection != null && !connection.isClosed() && !connection.getAutoCommit()) {
                transactionId = connection.toString();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactionId;
    }
}
