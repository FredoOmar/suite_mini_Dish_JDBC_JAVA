package td3;


    public class DishIngredient {
        private int id;
        private int idDish;
        private int idIngredient;
        private String unit; // 'Pcs', 'Kg', 'L'
        private double quantityRequired;

        // Constructeur par défaut
        public DishIngredient() {
        }

        // Constructeur avec paramètres
        public DishIngredient(int idDish, int idIngredient, String unit, double quantityRequired) {
            this.idDish = idDish;
            this.idIngredient = idIngredient;
            this.unit = unit;
            this.quantityRequired = quantityRequired;
        }

        // Getters et Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getIdDish() {
            return idDish;
        }

        public void setIdDish(int idDish) {
            this.idDish = idDish;
        }

        public int getIdIngredient() {
            return idIngredient;
        }

        public void setIdIngredient(int idIngredient) {
            this.idIngredient = idIngredient;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public double getQuantityRequired() {
            return quantityRequired;
        }

        public void setQuantityRequired(double quantityRequired) {
            this.quantityRequired = quantityRequired;
        }
    }

