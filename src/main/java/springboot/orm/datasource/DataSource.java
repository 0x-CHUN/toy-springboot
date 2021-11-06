package springboot.orm.datasource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The data source interface
 */
public interface DataSource {
    // load driver
    void loadDriverClass(String driverClassName);

    // get default connection
    Connection getConnection() throws SQLException;

    // release connection
    void releaseConnection(Connection connection) throws SQLException;

    // get connection by param
    Connection getConnection(String url, String username, String password) throws SQLException;
}
