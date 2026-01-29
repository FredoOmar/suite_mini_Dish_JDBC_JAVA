
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

create type payement_status as enum ('UNPAID','PAID');
CREATE TABLE sale (
    id serial primary key,
    creation_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
select * from  dish_ingredients;
select * from dish;
select * from ingredient;