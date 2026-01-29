insert into Dish (name,dishType ,price) values
('Salade fraiche', 'START',3500.00),
('Poulet grille', 'MAIN',12000.00),
('Riz au legume', 'MAIN',NULL),
('Gateau au chocolat', 'DESSERT',800.00),
('Salade fraiche', 'DESSERT',NULL);


INSERT INTO ingredient ( name,category, price) VALUES
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


UPDATE Ingredient SET quantity_in_stock = 50.0 WHERE name = 'Laitue';
UPDATE Ingredient SET quantity_in_stock = 40.0 WHERE name = 'Tomate';
UPDATE Ingredient SET quantity_in_stock = 30.0 WHERE name = 'Poulet';
UPDATE Ingredient SET quantity_in_stock = 20.0 WHERE name = 'Chocolat';
UPDATE Ingredient SET quantity_in_stock = 25.0 WHERE name = 'Beurre';


INSERT INTO "Order" (reference, creation_datetime) VALUES
                                                       ('ORD00001', '2026-01-20 10:30:00'),
                                                       ('ORD00002', '2026-01-21 14:45:00'),
                                                       ('ORD00003', '2026-01-22 09:15:00'),
                                                       ('ORD00004', '2026-01-23 16:20:00'),
                                                       ('ORD00005', '2026-01-24 11:00:00');

INSERT INTO dish_order (id_order, id_dish, quantity) VALUES
                                                         ((SELECT id FROM "Order" WHERE reference = 'ORD00001'), 1, 2),
                                                         ((SELECT id FROM "Order" WHERE reference = 'ORD00001'), 2, 1);


INSERT INTO dish_order (id_order, id_dish, quantity) VALUES
                                                         ((SELECT id FROM "Order" WHERE reference = 'ORD00002'), 2, 3),
                                                         ((SELECT id FROM "Order" WHERE reference = 'ORD00002'), 4, 2);

INSERT INTO dish_order (id_order, id_dish, quantity) VALUES
                                                         ((SELECT id FROM "Order" WHERE reference = 'ORD00003'), 1, 1),
                                                         ((SELECT id FROM "Order" WHERE reference = 'ORD00003'), 3, 1),
                                                         ((SELECT id FROM "Order" WHERE reference = 'ORD00003'), 4, 1);


INSERT INTO dish_order (id_order, id_dish, quantity) VALUES
    ((SELECT id FROM "Order" WHERE reference = 'ORD00004'), 1, 4);


INSERT INTO dish_order (id_order, id_dish, quantity) VALUES
                                                         ((SELECT id FROM "Order" WHERE reference = 'ORD00005'), 2, 2),
                                                         ((SELECT id FROM "Order" WHERE reference = 'ORD00005'), 4, 3);

SELECT
    o.id,
    o.reference,
    o.creation_datetime,
    COUNT(dorder.id) as nombre_plats
FROM "Order" o
         LEFT JOIN dish_order dorder ON o.id = dorder.id_order
GROUP BY o.id, o.reference, o.creation_datetime
ORDER BY o.creation_datetime;

-- Affichage détaillé des commandes avec leurs plats
SELECT
    o.reference,
    d.name as plat,
    dorder.quantity,
    d.price,
    (dorder.quantity * d.price) as sous_total
FROM "Order" o
         JOIN dish_order dorder ON o.id = dorder.id_order
         JOIN Dish d ON dorder.id_dish = d.id
ORDER BY o.reference, d.name;

-- Calcul du total par commande
SELECT
    o.reference,
    o.creation_datetime,
    COUNT(dorder.id) as nombre_plats_differents,
    SUM(dorder.quantity) as quantite_totale,
    SUM(dorder.quantity * d.price) as montant_ht,
    SUM(dorder.quantity * d.price) * 1.20 as montant_ttc
FROM "Order" o
         JOIN dish_order dorder ON o.id = dorder.id_order
         JOIN Dish d ON dorder.id_dish = d.id
GROUP BY o.id, o.reference, o.creation_datetime
ORDER BY o.creation_datetime;