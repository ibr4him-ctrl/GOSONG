package util;

import java.util.*;
import model.item.Dish;
import model.item.Preparable;
import model.item.ingredient.Ingredient;
import model.enums.IngredientState;

/**
 * RecipeManager tanpa class Recipe terpisah.
 * Menggunakan mapping dari nama dish → list ingredients yang dibutuhkan.
 */
public class RecipeManager {

    /**
     * Inner class untuk menyimpan requirement:
     * ingredientName + requiredState (CHOPPED/COOKED/etc)
     */
    private static class ExpectedIngredient {
        String name;
        IngredientState state;

        ExpectedIngredient(String name, IngredientState state) {
            this.name = name;
            this.state = state;
        }
    }

    private Map<String, List<ExpectedIngredient>> recipes;

    public RecipeManager() {
        this.recipes = new HashMap<>();
        loadDefaultRecipes();
    }

    private void loadDefaultRecipes() {
        recipes.put("Pizza Margherita", Arrays.asList(
            new ExpectedIngredient("Adonan", IngredientState.CHOPPED),
            new ExpectedIngredient("Tomat", IngredientState.CHOPPED),
            new ExpectedIngredient("Keju", IngredientState.CHOPPED)
        ));

        recipes.put("Pizza Sosis", Arrays.asList(
            new ExpectedIngredient("Adonan", IngredientState.CHOPPED),
            new ExpectedIngredient("Tomat", IngredientState.CHOPPED),
            new ExpectedIngredient("Sosis", IngredientState.CHOPPED)
        ));

        recipes.put("Pizza Ayam", Arrays.asList(
            new ExpectedIngredient("Adonan", IngredientState.CHOPPED),
            new ExpectedIngredient("Tomat", IngredientState.CHOPPED),
            new ExpectedIngredient("Ayam", IngredientState.CHOPPED)
        ));

        // Tambahkan resep lain sesuai kebutuhan...
    }

    /**
     * Cek  sebuah Dish sesuai 
     */
    public boolean isValidDish(Dish dish) {
        if (dish == null || dish.getComponents().isEmpty()) return false;

        List<Preparable> userComponents = dish.getComponents();

        for (String recipeName : recipes.keySet()) {
            if (matchesRecipe(userComponents, recipes.get(recipeName))) {
                return true;
            }
        }
        return false;
    }
    public String getMatchingRecipeName(Dish dish) {
        if (dish == null) return null;

        List<Preparable> userComponents = dish.getComponents();

        for (Map.Entry<String, List<ExpectedIngredient>> entry : recipes.entrySet()) {
            if (matchesRecipe(userComponents, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Core matcher ingredients.
     */
    private boolean matchesRecipe(List<Preparable> userIngredients, List<ExpectedIngredient> expected) {
        if (userIngredients.size() != expected.size()) return false;

        List<ExpectedIngredient> expectedCopy = new ArrayList<>(expected);

        for (Preparable p : userIngredients) {
            if (!(p instanceof Ingredient)) return false;
            Ingredient ing = (Ingredient) p;

            boolean found = false;

            Iterator<ExpectedIngredient> it = expectedCopy.iterator();
            while (it.hasNext()) {
                ExpectedIngredient exp = it.next();
                if (ing.getName().equals(exp.name) && ing.getState() == exp.state) {
                    found = true;
                    it.remove(); 
                    break;
                }
            }

            if (!found) return false;
        }

        return expectedCopy.isEmpty();
    }

    public Set<String> getAllRecipeNames() {
        return recipes.keySet();
    }

    public void addRecipe(String dishName, List<ExpectedIngredient> ingredients) {
        recipes.put(dishName, ingredients);
    }
}
