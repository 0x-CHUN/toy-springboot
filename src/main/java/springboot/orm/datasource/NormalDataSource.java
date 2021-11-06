package springboot.orm.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class NormalDataSource implements DataSource {
    private String driverClassName;
    private String url;
    private String username;
    private String password;

    public NormalDataSource(String driverClassName, String url, String username, String password) {
        this.driverClassName = driverClassName;
        this.url = url;
        this.username = username;
        this.password = password;
        loadDriverClass(this.driverClassName);
    }

    @Override
    public void loadDriverClass(String driverClassName) {
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public void releaseConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    @Override
    public Connection getConnection(String url, String username, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
