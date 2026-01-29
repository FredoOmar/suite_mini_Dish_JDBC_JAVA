
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




select * from  dish_ingredients;
select * from dish;
select * from ingredient;