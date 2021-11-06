package springboot.orm.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Transaction manager interface
 */
public interface TransactionManager {
    // get the RDMS connection
    Connection getConnection() throws SQLException;

    // has transaction?
    boolean isTransactionPresent();

    // start transaction
    void beginTransaction(TransactionStatus status) throws SQLException;

    // commit transaction
    void commit(TransactionStatus status) throws SQLException;

    // rollback
    void rollback() throws SQLException;

    // close the connection
    void close() throws SQLException;

    // close the transaction
    void closeTransaction(TransactionStatus status) throws SQLException;

    // set isolation level
    void setLevel(Integer level);

    // set autocommit
    void setAutocommit(Boolean autocommit);

    // get transaction id
    String getTransactionId();
}
