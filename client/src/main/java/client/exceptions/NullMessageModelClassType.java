package client.exceptions;

public class NullMessageModelClassType extends Throwable {

    /**
     * Constructor
     * @param message Error message
     */
    public NullMessageModelClassType(String message) {
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
     * @return cause of the Exception
     */
    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }
}
