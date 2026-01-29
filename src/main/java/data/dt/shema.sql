
create type dish_type as enum ('START', 'MAIN', 'DESSERT');
drop table Dish CASCADE ;
create table  Dish(
                      id serial primary key,
                      name varchar(100),
                      dishType dish_type,
                      price numeric
);

create  type ingredient_category as enum ('VEGETABLE','ANIMAL', 'MARINE', 'DAIRY', 'OTHER');

create table  Ingredient(
                            id serial primary key,
                            name varchar(200),
                            price numeric,
                            category ingredient_category

);



create type unit_type as enum ('Pcs','Kg', 'L');

 create table dish_ingredients (
     id serial primary key,
     id_dish integer references  Dish(id),
     id_ingredient integer references Ingredient(id),
     unit unit_type,
     quantity_required numeric
 );

ALTER TABLE Ingredient
    ADD COLUMN IF NOT EXISTS quantity_in_stock NUMERIC DEFAULT 0;

UPDATE Ingredient SET quantity_in_stock = 100.0 WHERE quantity_in_stock = 0;

CREATE TABLE IF NOT EXISTS "Order" (
                                       id SERIAL PRIMARY KEY,
                                       reference VARCHAR(50) NOT NULL UNIQUE,
                                       creation_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS dish_order (
                                          id SERIAL PRIMARY KEY,
                                          id_order INTEGER NOT NULL REFERENCES "Order"(id) ON DELETE CASCADE,
                                          id_dish INTEGER NOT NULL REFERENCES Dish(id) ON DELETE CASCADE,
                                          quantity INTEGER NOT NULL CHECK (quantity > 0),
                                          UNIQUE(id_order, id_dish)
);

CREATE INDEX IF NOT EXISTS idx_order_reference ON "Order"(reference);
CREATE INDEX IF NOT EXISTS idx_dish_order_order ON dish_order(id_order);
CREATE INDEX IF NOT EXISTS idx_dish_order_dish ON dish_order(id_dish);

COMMENT ON TABLE "Order" IS 'Table des commandes clients';
COMMENT ON COLUMN "Order".reference IS 'Référence unique de la commande (format: ORDXXXXXX)';
COMMENT ON COLUMN "Order".creation_datetime IS 'Date et heure de création de la commande';

COMMENT ON TABLE dish_order IS 'Table de liaison entre les commandes et les plats';
COMMENT ON COLUMN dish_order.id_order IS 'ID de la commande';
COMMENT ON COLUMN dish_order.id_dish IS 'ID du plat';
COMMENT ON COLUMN dish_order.quantity IS 'Quantité de plats commandés';

COMMENT ON COLUMN Ingredient.quantity_in_stock IS 'Quantité disponible en stock pour cet ingrédient';


SELECT
    tablename,
    schemaname
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY tablename;

CREATE TYPE payment_status_enum AS ENUM ('PAID', 'UNPAID');

ALTER TABLE "Order"
    ADD COLUMN IF NOT EXISTS payment_status payment_status_enum DEFAULT 'UNPAID',
    ADD COLUMN IF NOT EXISTS id_sale INTEGER;

CREATE TABLE IF NOT EXISTS Sale (
                                    id SERIAL PRIMARY KEY,
                                    creation_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    id_order INTEGER NOT NULL UNIQUE REFERENCES "Order"(id) ON DELETE CASCADE
);

ALTER TABLE "Order"
    ADD CONSTRAINT fk_order_sale FOREIGN KEY (id_sale) REFERENCES Sale(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_order_payment_status ON "Order"(payment_status);
CREATE INDEX IF NOT EXISTS idx_order_id_sale ON "Order"(id_sale);
CREATE INDEX IF NOT EXISTS idx_sale_id_order ON Sale(id_order);


UPDATE "Order" SET payment_status = 'UNPAID' WHERE payment_status IS NULL;


COMMENT ON TYPE payment_status_enum IS 'Statut de paiement d''une commande: PAID (payée) ou UNPAID (non payée)';
COMMENT ON TABLE Sale IS 'Table des ventes - Une vente est créée à partir d''une commande payée';
COMMENT ON COLUMN "Order".payment_status IS 'Statut de paiement de la commande';
COMMENT ON COLUMN "Order".id_sale IS 'ID de la vente associée (nullable) - relation OneToOne';
COMMENT ON COLUMN Sale.creation_datetime IS 'Date et heure de création de la vente';
COMMENT ON COLUMN Sale.id_order IS 'ID de la commande associée - relation OneToOne';

--Vérification des modifications
SELECT
    'Tables créées:' as info,
    COUNT(*) as count
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_name IN ('Order', 'Sale');

-- Afficher la structure de la table Order
SELECT
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_name = 'Order'
ORDER BY ordinal_position;

-- Afficher la structure de la table Sale
SELECT
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_name = 'sale'
ORDER BY ordinal_position;

-- Message de confirmation
SELECT '✓ Modifications terminées - Système de ventes prêt' as statut;








select * from  dish_ingredients;
select * from dish;
select * from ingredient;