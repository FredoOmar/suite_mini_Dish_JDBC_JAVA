package td3;

import java.sql.Timestamp;
import java.util.Objects;

public class Sale {
    private int id;
    private Timestamp creationDateTime;
    private Order order;


    public Sale() {
        this.creationDateTime = new Timestamp(System.currentTimeMillis());
    }

    public Sale(Order order) {
        this();
        this.order = order;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(Timestamp creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }


    public double getTotalAmountExcludingTax() {
        if (order != null) {
            return order.getTotalAmountExcludingTax();
        }
        return 0.0;
    }


    public double getTotalAmountIncludingTax() {
        if (order != null) {
            return order.getTotalAmountIncludingTax();
        }
        return 0.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sale)) return false;
        Sale sale = (Sale) o;
        return id == sale.id &&
                Objects.equals(creationDateTime, sale.creationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creationDateTime);
    }

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", creationDateTime=" + creationDateTime +
                ", orderReference=" + (order != null ? order.getReference() : "null") +
                ", montantHT=" + String.format("%.2f", getTotalAmountExcludingTax()) + " Ar" +
                ", montantTTC=" + String.format("%.2f", getTotalAmountIncludingTax()) + " Ar" +
                '}';
    }
}