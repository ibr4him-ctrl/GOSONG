package util;

import model.item.Dish;
import model.item.Preparable;
import model.item.ingredient.Ingredient;
import model.enums.IngredientState;

/**
 * Class untuk mem-validasi berbagai kondisi dalam game
 * seperti ingredient, dish, dan aksi preparasi.
 */
public class Validator {

    public boolean canChop(Ingredient ing) {
        if (ing == null) return false;
        return ing.getState() == IngredientState.RAW;
    }

    public boolean canCook(Ingredient ing) {
        if (ing == null) return false;
        IngredientState st = ing.getState();
        return st == IngredientState.RAW || st == IngredientState.CHOPPED;
    }

    public boolean canPlate(Ingredient ing) {
        if (ing == null) return false;
        IngredientState st = ing.getState();
        return st == IngredientState.CHOPPED || st == IngredientState.COOKED;
    }

    public boolean isDishStructurallyValid(Dish dish) {
        if (dish == null) return false;
        if (dish.getName() == null) return false;
        if (dish.getComponents().isEmpty()) return false;

        for (Preparable p : dish.getComponents()) {
            if (p instanceof Ingredient ing) {
                if (ing.getState() == IngredientState.BURNED) return false;
            }
        }
        return true;
    }

    public boolean isDishCompletelyValid(Dish dish, RecipeManager recipeManager) {
        if (!isDishStructurallyValid(dish)) return false;
        if (recipeManager == null) return false;
        return recipeManager.isValidDish(dish);
    }
}
