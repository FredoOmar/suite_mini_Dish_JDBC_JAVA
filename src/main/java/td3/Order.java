
package td3;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

    public class Order {
        private int id;
        private String reference;
        private Timestamp creationDateTime;
        private List<DishOrder> dishOrders;

        // Constructeur par défaut
        public Order() {
            this.dishOrders = new ArrayList<>();
            this.creationDateTime = new Timestamp(System.currentTimeMillis());
        }

        // Constructeur avec paramètres
        public Order(String reference) {
            this();
            this.reference = reference;
        }

        // Getters et Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public Timestamp getCreationDateTime() {
            return creationDateTime;
        }

        public void setCreationDateTime(Timestamp creationDateTime) {
            this.creationDateTime = creationDateTime;
        }

        public List<DishOrder> getDishOrders() {
            return dishOrders;
        }

        public void setDishOrders(List<DishOrder> dishOrders) {
            this.dishOrders = dishOrders;
        }

        /**
         * Calcule le montant total de la commande hors taxes
         * @return Le montant total HT
         */
        public double getTotalAmountExcludingTax() {
            if (this.dishOrders == null || this.dishOrders.isEmpty()) {
                return 0.0;
            }

            double total = 0.0;
            for (DishOrder dishOrder : this.dishOrders) {
                total += dishOrder.getSubtotal();
            }
            return total;
        }

        /**
         * Calcule le montant total de la commande TTC (avec TVA à 20%)
         * @return Le montant total TTC
         */
        public double getTotalAmountIncludingTax() {
            return getTotalAmountExcludingTax() * 1.20; // TVA à 20%
        }

        /**
         * Ajoute un plat à la commande
         * @param dishOrder Le plat commandé à ajouter
         */
        public void addDishOrder(DishOrder dishOrder) {
            if (this.dishOrders == null) {
                this.dishOrders = new ArrayList<>();
            }
            dishOrder.setOrder(this);
            this.dishOrders.add(dishOrder);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Order)) return false;
            Order order = (Order) o;
            return id == order.id &&
                    Objects.equals(reference, order.reference) &&
                    Objects.equals(creationDateTime, order.creationDateTime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, reference, creationDateTime);
        }

        @Override
        public String toString() {
            return "Order{" +
                    "id=" + id +
                    ", reference='" + reference + '\'' +
                    ", creationDateTime=" + creationDateTime +
                    ", totalAmountExcludingTax=" + String.format("%.2f", getTotalAmountExcludingTax()) +
                    ", totalAmountIncludingTax=" + String.format("%.2f", getTotalAmountIncludingTax()) +
                    ", dishOrders=" + (dishOrders != null ? dishOrders.size() : 0) + " plats" +
                    '}';
        }
    }

