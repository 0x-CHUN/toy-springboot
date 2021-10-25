package springboot.exception;

public class InterfaceNotImplementedException extends RuntimeException {
    public InterfaceNotImplementedException(String msg) {
        super(msg);
    }
}
