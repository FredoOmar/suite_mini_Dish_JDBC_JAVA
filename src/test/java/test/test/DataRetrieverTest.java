package test.test;

import org.junit.jupiter.api.*;
import td3.*;
import td3.DishTypeEnum;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DataRetrieverTest {

    private DataRetriever dataRetriever;
    private static int createdDishId = 0;

    @BeforeAll
    public void setUp() {
        System.out.println("=== Initialisation des tests DataRetriever ===");
        dataRetriever = new DataRetriever();

        // Vérifier la connexion à la base de données
        assertNotNull(DBconnection.getDBConnection(),
                "La connexion à la base de données devrait être établie");
    }

    @Test
    @Order(1)
    @DisplayName("Test 1: Récupération d'un plat existant par ID")
    public void testFindDishById_ExistingDish() {
        System.out.println("\n--- Test: findDishById avec ID existant ---");

        // Récupérer le plat avec ID = 1 (devrait exister selon vos données)
        Dish dish = dataRetriever.findDishById(1);

        // Vérifications
        assertNotNull(dish, "Le plat devrait être trouvé");
        assertEquals(1, dish.getId(), "L'ID du plat devrait être 1");
        assertNotNull(dish.getName(), "Le nom du plat ne devrait pas être null");
        assertNotNull(dish.getDishType(), "Le type du plat ne devrait pas être null");

        System.out.println("Plat trouvé: " + dish.getName() + " (" + dish.getDishType() + ")");
        System.out.println("Nombre d'ingrédients: " + dish.getIngredients().size());
        System.out.println("Prix calculé: " + dish.getDishPrice() + " Ar");

        // Vérifier que la liste d'ingrédients est initialisée
        assertNotNull(dish.getIngredients(), "La liste d'ingrédients ne devrait pas être null");
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Récupération d'un plat inexistant")
    public void testFindDishById_NonExistingDish() {
        System.out.println("\n--- Test: findDishById avec ID inexistant ---");

        // Récupérer un plat avec un ID très grand qui n'existe probablement pas
        Dish dish = dataRetriever.findDishById(99999);

        // Vérification
        assertNull(dish, "Le plat ne devrait pas être trouvé");
        System.out.println("Résultat attendu: null - OK");
    }

    @Test
    @Order(3)
    @DisplayName("Test 3: Pagination des ingrédients - Page 0")
    public void testFindIngredients_FirstPage() {
        System.out.println("\n--- Test: findIngredients - Première page ---");

        int page = 0;
        int size = 3;

        List<Ingredients> ingredients = dataRetriever.findIngredients(page, size);

        // Vérifications
        assertNotNull(ingredients, "La liste ne devrait pas être null");
        assertTrue(ingredients.size() <= size,
                "Le nombre d'ingrédients ne devrait pas dépasser " + size);

        System.out.println("Ingrédients récupérés: " + ingredients.size());
        for (Ingredients ing : ingredients) {
            System.out.println("  - " + ing.getName() + " (" + ing.getCategory() + ") - " + ing.getPrice() + " Ar");
            assertNotNull(ing.getName(), "Le nom ne devrait pas être null");
            assertNotNull(ing.getCategory(), "La catégorie ne devrait pas être null");
        }
    }

    @Test
    @Order(4)
    @DisplayName("Test 4: Pagination des ingrédients - Page 1")
    public void testFindIngredients_SecondPage() {
        System.out.println("\n--- Test: findIngredients - Deuxième page ---");

        int page = 1;
        int size = 2;

        List<Ingredients> ingredients = dataRetriever.findIngredients(page, size);


        assertNotNull(ingredients, "La liste ne devrait pas être null");
        System.out.println("Ingrédients page 1: " + ingredients.size());
    }

    @Test
    @Order(5)
    @DisplayName("Test 5: Création d'ingrédients valides")
    public void testCreateIngredients_Valid() {
        System.out.println("\n--- Test: createIngredients - Ingrédients valides ---");

        List<Ingredients> newIngredients = new ArrayList<>();

        // Créer des ingrédients avec des noms uniques basés sur timestamp
        long timestamp = System.currentTimeMillis();

        Ingredients ingredient1 = new Ingredients();
        ingredient1.setName("Test_Carotte_" + timestamp);
        ingredient1.setPrice(500.0);
        ingredient1.setCategory(CategoryEnum.VEGETABLE);
        newIngredients.add(ingredient1);

        Ingredients ingredient2 = new Ingredients();
        ingredient2.setName("Test_Oignon_" + timestamp);
        ingredient2.setPrice(400.0);
        ingredient2.setCategory(CategoryEnum.VEGETABLE);
        newIngredients.add(ingredient2);

        // Exécution
        List<Ingredients> created = dataRetriever.createIngredients(newIngredients);

        // Vérifications
        assertNotNull(created, "La liste des ingrédients créés ne devrait pas être null");
        assertEquals(2, created.size(), "Deux ingrédients devraient être créés");

        for (Ingredients ing : created) {
            assertTrue(ing.getId() > 0, "L'ID devrait être assigné");
            System.out.println("Ingrédient créé: ID=" + ing.getId() + ", Nom=" + ing.getName());
        }
    }

    @Test
    @Order(6)
    @DisplayName("Test 6: Création d'ingrédients avec doublon")
    public void testCreateIngredients_Duplicate() {
        System.out.println("\n--- Test: createIngredients - Avec doublon ---");

        List<Ingredients> newIngredients = new ArrayList<>();

        // Utiliser un nom existant (Laitue existe dans vos données initiales)
        Ingredients duplicate = new Ingredients();
        duplicate.setName("Laitue");
        duplicate.setPrice(800.0);
        duplicate.setCategory(CategoryEnum.VEGETABLE);
        newIngredients.add(duplicate);

        // Vérification qu'une exception est levée
        assertThrows(DuplicateIngredientException.class, () -> {
            dataRetriever.createIngredients(newIngredients);
        }, "Une DuplicateIngredientException devrait être levée");

        System.out.println("Exception DuplicateIngredientException levée comme attendu - OK");
    }

    @Test
    @Order(7)
    @DisplayName("Test 7: Sauvegarde d'un nouveau plat")
    public void testSaveDish_Insert() {
        System.out.println("\n--- Test: saveDish - Insertion nouveau plat ---");

        // Créer un nouveau plat
        Dish newDish = new Dish();
        newDish.setName("Test_Salade_César_" + System.currentTimeMillis());
        newDish.setDishType(DishTypeEnum.START);

        // Ajouter des ingrédients existants
        Ingredients ing1 = new Ingredients();
        ing1.setId(1); // Laitue
        ing1.setPrice(800.0);
        newDish.addIngredients(ing1);

        Ingredients ing2 = new Ingredients();
        ing2.setId(2); // Tomate
        ing2.setPrice(600.0);
        newDish.addIngredients(ing2);

        // Sauvegarde
        Dish savedDish = dataRetriever.saveDish(newDish);

        // Vérifications
        assertNotNull(savedDish, "Le plat sauvegardé ne devrait pas être null");
        assertTrue(savedDish.getId() > 0, "Un ID devrait être assigné");

        createdDishId = savedDish.getId(); // Sauvegarder pour tests suivants

        System.out.println("Plat créé avec ID: " + savedDish.getId());
        System.out.println("Nom: " + savedDish.getName());
    }

    @Test
    @Order(8)
    @DisplayName("Test 8: Mise à jour d'un plat existant")
    public void testSaveDish_Update() {
        System.out.println("\n--- Test: saveDish - Mise à jour ---");

        // Récupérer le plat créé dans le test précédent
        if (createdDishId == 0) {
            System.out.println("Aucun plat créé, test ignoré");
            return;
        }

        Dish dishToUpdate = dataRetriever.findDishById(createdDishId);
        assertNotNull(dishToUpdate, "Le plat devrait exister");

        // Modifier le plat
        String newName = "Test_Salade_Modifiée_" + System.currentTimeMillis();
        dishToUpdate.setName(newName);
        dishToUpdate.setDishType(DishTypeEnum.MAIN);

        // Mettre à jour
        Dish updatedDish = dataRetriever.saveDish(dishToUpdate);

        // Vérifications
        assertNotNull(updatedDish, "Le plat mis à jour ne devrait pas être null");
        assertEquals(createdDishId, updatedDish.getId(), "L'ID devrait rester le même");
        assertEquals(newName, updatedDish.getName(), "Le nom devrait être mis à jour");

        System.out.println("Plat mis à jour: " + updatedDish.getName());
    }

    @Test
    @Order(9)
    @DisplayName("Test 9: Recherche par nom d'ingrédient")
    public void testFindIngredientsByCriteria_ByName() {
        System.out.println("\n--- Test: findIngredientsByCriteria - Par nom ---");

        // Rechercher les ingrédients contenant "tomate"
        List<Ingredients> ingredients = dataRetriever.findIngredientsByCriteria("tomate", null, null, 0, 10);

        // Vérifications
        assertNotNull(ingredients, "La liste ne devrait pas être null");
        System.out.println("Ingrédients trouvés contenant 'tomate': " + ingredients.size());

        for (Ingredients ing : ingredients) {
            System.out.println("  - " + ing.getName());
            assertTrue(ing.getName().toLowerCase().contains("tomate"),
                    "Le nom devrait contenir 'tomate'");
        }
    }

    @Test
    @Order(10)
    @DisplayName("Test 10: Recherche par catégorie")
    public void testFindIngredientsByCriteria_ByCategory() {
        System.out.println("\n--- Test: findIngredientsByCriteria - Par catégorie ---");

        // Rechercher les ingrédients de catégorie VEGETABLE
        List<Ingredients> ingredients = dataRetriever.findIngredientsByCriteria(null, CategoryEnum.VEGETABLE, null, 0, 10);

        // Vérifications
        assertNotNull(ingredients, "La liste ne devrait pas être null");
        System.out.println("Ingrédients VEGETABLE trouvés: " + ingredients.size());

        for (Ingredients ing : ingredients) {
            assertEquals(CategoryEnum.VEGETABLE, ing.getCategory(),
                    "La catégorie devrait être VEGETABLE");
            System.out.println("  - " + ing.getName() + " (" + ing.getCategory() + ")");
        }
    }

    @Test
    @Order(11)
    @DisplayName("Test 11: Recherche multicritères")
    public void testFindIngredientsByCriteria_MultiCriteria() {
        System.out.println("\n--- Test: findIngredientsByCriteria - Multicritères ---");

        // Rechercher avec plusieurs critères
        List<Ingredients> ingredients = dataRetriever.findIngredientsByCriteria(
                "a", // Nom contenant 'a'
                CategoryEnum.VEGETABLE, // Catégorie VEGETABLE
                null, // Pas de filtre sur le plat
                0,
                5
        );

        // Vérifications
        assertNotNull(ingredients, "La liste ne devrait pas être null");
        System.out.println("Ingrédients trouvés (multicritères): " + ingredients.size());

        for (Ingredients ing : ingredients) {
            System.out.println("  - " + ing.getName() + " (" + ing.getCategory() + ")");
        }
    }

    @Test
    @Order(12)
    @DisplayName("Test 12: Recherche sans critères (tous les ingrédients)")
    public void testFindIngredientsByCriteria_NoCriteria() {
        System.out.println("\n--- Test: findIngredientsByCriteria - Sans critères ---");

        // Rechercher sans critères (devrait retourner tous les ingrédients paginés)
        List<Ingredients> ingredients = dataRetriever.findIngredientsByCriteria(null, null, null, 0, 100);

        // Vérifications
        assertNotNull(ingredients, "La liste ne devrait pas être null");
        assertTrue(ingredients.size() > 0, "Il devrait y avoir au moins un ingrédient");
        System.out.println("Total d'ingrédients trouvés: " + ingredients.size());
    }

    @Test
    @Order(13)
    @DisplayName("Test 13: Méthode surchargée findIngredientsByCriteria")
    public void testFindIngredientsByCriteria_Overloaded() {
        System.out.println("\n--- Test: findIngredientsByCriteria - Méthode surchargée ---");

        // Utiliser la méthode surchargée avec uniquement le nom
        List<Ingredients> ingredients = dataRetriever.findIngredientsByCriteria("poulet");

        // Vérifications
        assertNotNull(ingredients, "La liste ne devrait pas être null");
        System.out.println("Ingrédients trouvés avec 'poulet': " + ingredients.size());
    }

    @AfterAll
    public void tearDown() {
        System.out.println("\n=== Fin des tests DataRetriever ===");
        System.out.println("Tous les tests sont terminés.");

        // Note: Dans un environnement de production, vous devriez nettoyer
        // les données de test créées (supprimer le plat créé, etc.)
    }
}