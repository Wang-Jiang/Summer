package space.wangjiang.summer.model.transaction;

/**
 * 事务异常
 */
public class TransactionException extends RuntimeException {

    public TransactionException() {
    }

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionException(Throwable cause) {
        super(cause);
    }
}
