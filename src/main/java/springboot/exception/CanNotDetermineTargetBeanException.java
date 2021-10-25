package springboot.exception;

public class CanNotDetermineTargetBeanException extends RuntimeException {
    public CanNotDetermineTargetBeanException(String msg) {
        super(msg);
    }
}
