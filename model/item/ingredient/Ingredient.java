package model.item.ingredient;

import model.enums.IngredientState;
import model.enums.ItemLocation;
import model.enums.ItemType;
import model.item.Item; 
import model.item.Preparable;


public class Ingredient extends Item implements Preparable {
    private IngredientState state; 

    protected Ingredient(String name, ItemLocation location){
        super(name, ItemType.INGREDIENT, location);
        this.state = IngredientState.RAW; 
        setEdible(false);
    }

    public IngredientState getState(){
        return state; 
    }

    public void setState(IngredientState state){
        this.state = state; 
    }

    //Implementasi Preparable 
    @Override
    public boolean canBeChopped(){
        return state == IngredientState.RAW; 
    }
    @Override
    public boolean canBeCooked(){
        return state == IngredientState.RAW || state == IngredientState.CHOPPED; 
    }
    @Override
    public boolean canBePlacedOnPlate(){
        //ini aku masih bingung apa aja yang bisa ditaruh di plate mungkin ini. sesuain ama game -tyara
        return state == IngredientState.RAW || state == IngredientState.CHOPPED || state == IngredientState.COOKED; 
    }

    @Override
    public void chop() {
        if (!canBeChopped()) {
            throw new IllegalStateException(
                "Ingredient " + getName() + " tidak bisa dipotong dari state " + state
            );
        }
        this.state = IngredientState.CHOPPED;
    }
    @Override
    public void cook() {
        if (!canBeCooked()) {
            throw new IllegalStateException(
                "Ingredient " + getName() + " tidak bisa dimasak dari state " + state
            );
        }
        // di game loop / CookingDevice kamu bisa atur transisi COOKING → COOKED/BURNED,
        // di sini kita langsung anggap COOKED
        this.state = IngredientState.COOKED;
        setEdible(true);
    }

    public void burn(){
        this.state = IngredientState.BURNED; 
        setEdible(false); 
    }
    @Override
    public String toString(){
        return super.toString() + "[state=" + state + "]"; 
    }

    //KODE HELPER YANG MUNGKIN BERGUNA ATAU TIDAK 
    public boolean isRaw(){
        return state == IngredientState.RAW; 
    }

    public boolean isChopped(){
        return state == IngredientState.CHOPPED; 
    }

    public boolean isCooking(){
        return state == IngredientState.COOKING; 
    }

    public boolean isCooked(){
        return state == IngredientState.COOKED; 
    }

    public boolean isBurned(){
        return state == IngredientState.BURNED; 
    }



}
