package model.station;

import java.util.List;

import model.chef.Chef;
import model.enums.IngredientState;
import model.item.Dish;
import model.item.Item;
import model.item.Preparable;
import model.item.ingredient.Ingredient;
import model.item.utensils.Oven;
import model.item.dish.PizzaRecipeChecker;
import model.enums.PizzaType;

public class CookingStation extends Station {

    private final Oven oven;

    public CookingStation(int x, int y) {
        super(x, y,"Cooking");
        this.oven = new Oven();
    }

    @Override
    public String getSymbol() {
        return "R";
    }

    public Oven getOven() {
        return oven;
    }

    @Override
    public boolean interact(Chef chef) {
        Item hand = chef.getHeldItem();

        if (hand instanceof Ingredient ing) {
            IngredientState st = ing.getState();
            if (st == IngredientState.RAW || st == IngredientState.CHOPPED) {
                Preparable p = ing;
                if (oven.canAccept(p)) {
                    oven.addIngredient(p);
                    chef.setHeldItem(null);
                    return true;
                }
            }
            return false;
        }

        if (!oven.isCooking() && !oven.getContents().isEmpty()) {
            oven.startCooking();
            return true;
        }

        if (!oven.isCooking() && hand == null && !oven.getContents().isEmpty()) {
            List<Preparable> cooked = oven.takeOutAll();

            PizzaType type = PizzaRecipeChecker.detectPizza(cooked);
            Dish dish = PizzaRecipeChecker.createPizzaDish(type, cooked);

            if (dish != null) {
                chef.setHeldItem(dish);
                return true;
            }
            return false;
        }
        return false;
    }

    public void update(double deltaSeconds) {
        oven.update(deltaSeconds);
    }
}