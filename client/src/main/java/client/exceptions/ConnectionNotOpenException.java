package client.exceptions;

public class ConnectionNotOpenException extends Throwable {
    /**
     * Constructor for exception Class
     * @param message Error message to be displayed
     */
    public ConnectionNotOpenException(String message) {
        super(message);
    }

    /**
     * Getter
     * @return the error message
     */
    @Override
    public String getMessage() {
        return super.getMessage();
    }

    /**
     * Getter
     * @return the cause of the Exception
     */
    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }
}
