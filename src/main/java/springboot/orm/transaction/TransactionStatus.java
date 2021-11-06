package springboot.orm.transaction;

/**
 * Transaction status
 */
public class TransactionStatus {
    // need to use transaction?
    public Boolean isNeed;
    // exists transaction
    public Boolean isTrans;
    // isolation level
    public Integer isolationLevel;
    // propagation level
    public Integer propagationLevel;
    // rollback error
    public Class<? extends Throwable>[] rollbackFor;
}
