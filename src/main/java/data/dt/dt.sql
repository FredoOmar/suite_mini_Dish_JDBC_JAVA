

DROP DATABASE IF EXISTS dish_normalisation;
DROP USER IF EXISTS dish_normalisation_manager;

CREATE USER dish_normalisation_manager WITH PASSWORD '123456';

CREATE DATABASE dish_normalisation
    OWNER dish_normalisation_manager
    ENCODING 'UTF8';

ALTER DATABASE dish_normalisation OWNER TO dish_normalisation_manager;

GRANT ALL PRIVILEGES ON DATABASE dish_normalisation TO dish_normalisation_manager;

