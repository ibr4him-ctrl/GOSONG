package model.station;

import java.util.List;
import model.chef.Chef;
import model.enums.PizzaType;
import model.item.Dish;
import model.item.Item;
import model.item.Preparable;
import model.item.dish.PizzaRecipeChecker;
import model.item.ingredient.Ingredient;
import model.item.utensils.Oven;

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
        if (!isAdjacentTo(chef)) return false; 
        
        Item hand = chef.getHeldItem();
        // 1) chef pegang ingredient dan coba ngemasukin ke oven
        if (hand instanceof Ingredient ing) {
            Preparable p = ing;
            if (!oven.canAccept(p)){
                return false; 
            }

            //masukin ke oven 
            oven.addIngredient(p);
            chef.setHeldItem(null);

            //proses masuk 
            if (!oven.isCooking()){
                oven.startCooking();
            }
            //chef tidak jadi busy -> bisa langsung pergi 
            return true; 
        }

        //chef tgn kosong, oven sudah selesai masak, isi masih ada --. keluarkan 
        //selama masak : isCooking() = true 
        //setelah selesai/gosong issCooking = false, tapi content masih ada

        if (!oven.isCooking() && hand == null && !oven.getContents().isEmpty()) {
            List<Preparable> cooked = oven.takeOutAll();

            PizzaType type = PizzaRecipeChecker.detectPizza(cooked);
            Dish dish = PizzaRecipeChecker.createPizzaDish(type, cooked);

            if (dish != null) {
                chef.setHeldItem(dish);
                return true;
            }

            //kalau kombinasi ngak cocok resep pizza manapun : sementara kita anggap gagal 
            return false;
        }
        // misal oven masih masak atau chef tangannya ngak kosong -> ngak ada yang terjadi 
        return false;
    }

    public void update(double deltaSeconds) {
        oven.update(deltaSeconds);
    }
}