package springboot.orm.executor;

public class ExecutorFactory {
    public static Executor getExecutor() {
        return new SimpleExecutor(false, false);
    }
}
