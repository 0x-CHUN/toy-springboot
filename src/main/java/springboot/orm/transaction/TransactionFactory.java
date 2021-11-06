package springboot.orm.transaction;

import springboot.orm.datasource.DataSource;
import springboot.orm.datasource.PoolDataSource;
import springboot.core.config.ConfigurationFactory;


public class TransactionFactory {

    private static volatile TransactionManager transaction = null;

    // get the config
    private static String driver = ConfigurationFactory.getConfig().getString("driver");
    private static String url = ConfigurationFactory.getConfig().getString("url");
    private static String username = ConfigurationFactory.getConfig().getString("username");
    private static String password = ConfigurationFactory.getConfig().getString("password");
    private static int poolSize = 10;
    private static long waitTime = 10L;

    /**
     * an TransactionManager singleton instance
     */
    public static TransactionManager newTransaction(Integer level, Boolean autocommit) {
        if (transaction == null) {
            synchronized (TransactionManager.class) {
                if (transaction == null) {
                    DataSource dataSource;
                    dataSource = new PoolDataSource(driver, url, username, password, poolSize, waitTime);
                    transaction = new SimpleTransactionManager(dataSource, level, autocommit);
                    return transaction;
                }
            }
        }
        return transaction;
    }

    public static TransactionManager newTransaction() {
        if (transaction == null) {
            synchronized (TransactionManager.class) {
                if (transaction == null) {
                    DataSource dataSource;
                    dataSource = new PoolDataSource(driver, url, username, password, poolSize, waitTime);
                    transaction = new SimpleTransactionManager(dataSource);
                    return transaction;
                }
            }
        }
        return transaction;
    }
}
