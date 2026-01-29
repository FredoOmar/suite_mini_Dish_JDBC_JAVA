insert into Dish (name,dishType ,price) values
('Salade fraiche', 'START',3500.00),
('Poulet grille', 'MAIN',12000.00),
('Riz au legume', 'MAIN',NULL),
('Gateau au chocolat', 'DESSERT',800.00),
('Salade fraiche', 'DESSERT',NULL);


INSERT INTO ingredient ( name,category, price,) VALUES
                                                                ( 'Laitue', 800.00, 'VEGETABLE'),
                                                                ('Tomate', 600.00, 'VEGETABLE'),
                                                                ( 'Poulet', 4500.00, 'ANIMAL'),
                                                                ( 'Chocolat', 3000.00, 'OTHER'),
                                                                ( 'Beurre', 2500.00, 'DAIRY');
INSERT INTO dish_ingredients (id_dish,id_ingredient,quantity_required, unit) VALUES
(1,1,0.20,'Kg'),
(1,2,0.15,'Kg'),
(2,3,1.00,'Kg'),
(4,4,0.30,'Kg'),
(4,5,0.20,'Kg');