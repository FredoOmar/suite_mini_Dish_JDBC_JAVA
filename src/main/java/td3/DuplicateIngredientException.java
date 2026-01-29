package td3;
import java.lang.RuntimeException;

public class DuplicateIngredientException extends RuntimeException {
    public DuplicateIngredientException(String message) {
        super(message);
    }

    public DuplicateIngredientException(String message, Throwable cause) {
        super(message, cause);
    }
}
