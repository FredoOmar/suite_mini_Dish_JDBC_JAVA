package td3;

import java.util.Objects;

public class DishOrder {
    private int id;
    private Order order;
    private Dish dish;
    private int quantity;

    // Constructeur par défaut
    public DishOrder() {
    }

    // Constructeur avec paramètres
    public DishOrder(Order order, Dish dish, int quantity) {
        this.order = order;
        this.dish = dish;
        this.quantity = quantity;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Calcule le sous-total pour ce plat (prix * quantité)
     * @return Le sous-total
     */
    public double getSubtotal() {
        if (dish == null || dish.getPrice() == null) {
            return 0.0;
        }
        return dish.getPrice() * quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DishOrder)) return false;
        DishOrder dishOrder = (DishOrder) o;
        return id == dishOrder.id &&
                quantity == dishOrder.quantity &&
                Objects.equals(order, dishOrder.order) &&
                Objects.equals(dish, dishOrder.dish);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantity);
    }

    @Override
    public String toString() {
        return "DishOrder{" +
                "id=" + id +
                ", dish=" + (dish != null ? dish.getName() : "null") +
                ", quantity=" + quantity +
                ", subtotal=" + String.format("%.2f", getSubtotal()) +
                '}';
    }
}
