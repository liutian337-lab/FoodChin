package demo.exception;

public class BlockchainException extends BusinessException {
    public BlockchainException(String message, Throwable cause) {
        super(502, message);
        initCause(cause);
    }
}
