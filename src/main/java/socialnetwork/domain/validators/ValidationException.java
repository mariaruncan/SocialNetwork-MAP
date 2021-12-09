package socialnetwork.domain.validators;

/**
 * exception class for validation
 * inherits from RunTimeException
 */
public class ValidationException extends RuntimeException {
    /**
     * default constructor
     */
    public ValidationException() {}

    /**
     * constructor with message
     * @param message
     */
    public ValidationException(String message) {
        super(message);
    }

    /***
     * constructor 2
     * @param message
     * @param cause
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /***
     *
     * @param cause
     */
    public ValidationException(Throwable cause) {
        super(cause);
    }

    /***
     * constructor complet
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
