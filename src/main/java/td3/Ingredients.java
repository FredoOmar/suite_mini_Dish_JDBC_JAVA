package td3;
import java.util.Objects;

public class Ingredients {
        private int id;
        private String name;
        private double price;
        private CategoryEnum category;
        private Dish dish;
        private double required_quantity;




    public Ingredients() {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.dish = dish;
        this.required_quantity = required_quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public double getRequired_quantity() {
        return required_quantity;
    }

    public void setRequired_quantity(double required_quantity) {
        this.required_quantity = required_quantity;
    }

    @Override
        public boolean equals(Object o) {
            if (!(o instanceof Ingredients that)) return false;
            return id == that.id && Double.compare(price, that.price) == 0 && Double.compare(required_quantity, that.required_quantity) == 0 && Objects.equals(name, that.name) && Objects.equals(category, that.category) && Objects.equals(dish, that.dish);
        }

            @Override
                public int hashCode() {
                    return Objects.hash(id, name, price, category, dish, required_quantity);
                }


            @Override
            public String toString() {
                return "Ingredients{" +
                        "id=" + id +
                        ", name='" + name + '\'' +
                        ", price=" + price +
                        ", category=" + category +
                        ", dish=" + (dish != null ? dish.getName() : "pas de plat associé") +
                        ", required_quantity=" + required_quantity +
                        '}';
            }


            public String getDishName() {
                if (dish != null) {
                    return dish.getName();
                }
                return null;
            }


}


