package td3;

public class OrderAlreadyHasSaleException extends RuntimeException {
    private String orderReference;
    private int saleId;

    public OrderAlreadyHasSaleException(String message) {
        super(message);
    }

    public OrderAlreadyHasSaleException(String message, String orderReference, int saleId) {
        super(message);
        this.orderReference = orderReference;
        this.saleId = saleId;
    }

    public OrderAlreadyHasSaleException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getOrderReference() {
        return orderReference;
    }

    public int getSaleId() {
        return saleId;
    }

    @Override
    public String toString() {
        if (orderReference != null) {
            return "OrderAlreadyHasSaleException: " + getMessage() +
                    " [Référence commande: " + orderReference +
                    ", ID Vente: " + saleId + "]";
        }
        return super.toString();
    }
}