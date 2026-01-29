package td3;

public class OrderNotFoundException extends RuntimeException {
    private String reference;

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(String message, String reference) {
        super(message);
        this.reference = reference;
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getReference() {
        return reference;
    }

    @Override
    public String toString() {
        if (reference != null) {
            return "OrderNotFoundException: " + getMessage() +
                    " [Référence: " + reference + "]";
        }
        return super.toString();
    }
}