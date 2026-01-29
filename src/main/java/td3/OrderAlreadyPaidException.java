package td3;

public class OrderAlreadyPaidException extends RuntimeException {
    private String orderReference;

    public OrderAlreadyPaidException(String message) {
        super(message);
    }

    public OrderAlreadyPaidException(String message, String orderReference) {
        super(message);
        this.orderReference = orderReference;
    }

    public OrderAlreadyPaidException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getOrderReference() {
        return orderReference;
    }

    @Override
    public String toString() {
        if (orderReference != null) {
            return "OrderAlreadyPaidException: " + getMessage() +
                    " [Référence commande: " + orderReference + "]";
        }
        return super.toString();
    }
}