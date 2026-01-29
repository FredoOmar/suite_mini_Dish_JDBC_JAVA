package td3;

public class InsufficientStockException extends RuntimeException {
    private String ingredientName;
    private double requiredQuantity;
    private double availableQuantity;

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String message, String ingredientName,
                                      double requiredQuantity, double availableQuantity) {
        super(message);
        this.ingredientName = ingredientName;
        this.requiredQuantity = requiredQuantity;
        this.availableQuantity = availableQuantity;
    }

    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public double getRequiredQuantity() {
        return requiredQuantity;
    }

    public double getAvailableQuantity() {
        return availableQuantity;
    }

    @Override
    public String toString() {
        if (ingredientName != null) {
            return "InsufficientStockException: " + getMessage() +
                    " [Ingrédient: " + ingredientName +
                    ", Requis: " + requiredQuantity +
                    ", Disponible: " + availableQuantity + "]";
        }
        return super.toString();
    }
}