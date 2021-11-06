package springboot.orm.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PoolDataSource implements DataSource {
    private String driverClassName;
    private String url;
    private String username;
    private String password;

    // idle connection pool
    private LinkedBlockingQueue<Connection> idlePool = new LinkedBlockingQueue<>();
    // active connection pool
    private LinkedBlockingQueue<Connection> activePool = new LinkedBlockingQueue<>();
    private AtomicInteger activeSize = new AtomicInteger(0);
    private Integer maxSize;
    private Long waitTime;

    public PoolDataSource(String driverClassName, String url, String username, String password,
                          Integer maxSize, Long waitTime) {
        this.driverClassName = driverClassName;
        this.url = url;
        this.username = username;
        this.password = password;
        this.maxSize = maxSize;
        this.waitTime = waitTime;
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
        return getConnection(this.url, this.username, this.password);
    }

    @Override
    public void releaseConnection(Connection connection) throws SQLException {
        if (connection != null) {
            if (!connection.isClosed()) {
                activePool.remove(connection);
                idlePool.offer(connection);
            } else {
                activePool.remove(connection);
                idlePool.remove(connection);
                connection = null;
            }
        }
    }

    @Override
    public Connection getConnection(String url, String username, String password) throws SQLException {
        Connection connection = idlePool.poll();
        if (connection != null) {
            activePool.offer(connection);
            return connection;
        }
        if (activeSize.get() < maxSize) {
            if (activeSize.incrementAndGet() <= maxSize) {
                connection = DriverManager.getConnection(url, username, password);
                activePool.add(connection);
                return connection;
            }
        }
        try {
            connection = idlePool.poll(waitTime, TimeUnit.MILLISECONDS);
            if (connection == null) {
                throw new RuntimeException("Wait SQL connection timeout");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
