package client.exceptions;

public class WebSocketConnectionException extends Throwable {

    /**
     * Constructor
     * @param message Error message
     */
    public WebSocketConnectionException(String message) {
        super(message);
    }
    /**
     * Constructor
     * @param message Error message
     * @param cause Cause for the exception
     */
    public WebSocketConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * Getter
     * @return Error message
     */
    @Override
    public String getMessage() {
        return super.getMessage();
    }
    /**
     * Getter
     * @return Cause for the exception
     */
    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }
}
