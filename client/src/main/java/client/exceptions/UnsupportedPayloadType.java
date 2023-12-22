package client.exceptions;

public class UnsupportedPayloadType extends Throwable {
    /**
     * Constructor
     * @param message Error message
     */
    public UnsupportedPayloadType(String message) {
        super(message);
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
