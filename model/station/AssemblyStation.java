package model.station;

import model.chef.Chef;
import model.item.Item;
import model.item.Preparable;
import model.item.ingredient.Ingredient;
import model.item.utensils.Plate;

public class AssemblyStation extends Station {
    public AssemblyStation(int x, int y) {
        super(x, y, "Assembly");
    }
    
    @Override
    public String getSymbol() {
        return "A";
    }

    @Override
    public boolean interact(Chef chef) {
        if (!isAdjacentTo(chef)) {
            System.out.println("❌ Chef terlalu jauh dari assembly station!");
            return false;
        }
        
        Item hand = chef.getHeldItem();
    
        if (hand == null && itemOnStation != null) {
            chef.setHeldItem(itemOnStation);
            
            itemOnStation = null;
            return true;
        }
        
        if (hand != null && itemOnStation == null) {
            itemOnStation = hand;
            chef.setHeldItem(null);
            
            System.out.println("📍 Chef " + chef.getName() + " meletakkan " + 
                             itemOnStation.getName() + " di assembly station");
            return true;
        }
        
        if (hand instanceof Plate plate && itemOnStation instanceof Ingredient ing) {
            return performPlating(chef, plate, ing, true);
        }
        
        if (hand instanceof Ingredient ing && itemOnStation instanceof Plate plate) {
            return performPlating(chef, plate, ing, false);
        }
        
        if (hand != null && itemOnStation != null) {
            System.out.println("Udah ada item diatasnya!");
            return false;
        }
        
        return false;
    }
    
    private boolean performPlating(Chef chef, Plate plate, Preparable ingredient, boolean plateInHand) {
        if (!plate.isClean()) {
            System.out.println("Plate kotor tidak bisa digunakan untuk plating!");
            System.out.println("Cuci plate di Washing Station dulu.");
            return false;
        }
        
        // Validasi: ingredient harus bisa di-plate
        if (!plate.canAccept(ingredient)) {
            System.out.println("Ingredient tidak bisa ditambahkan ke plate!");
            
            if (ingredient == null) {
                System.out.println("Loh gada ingredientnya.");
            } else if (!ingredient.canBePlacedOnPlate()) {
                System.out.println("Ingredient harus diproses dulu (potong/masak)");
            }
            
            return false;
        }
        
        // Tambahkan ingredient ke plate
        boolean success = plate.addIngredient(ingredient);
        
        if (success) {
            String ingredientName = "Ingredient";
            if (ingredient instanceof Item item) {
                ingredientName = item.getName();
            }
            
            System.out.println("🍽️ Plating: " + ingredientName + " → Plate");
            
            if (plateInHand) {
                itemOnStation = null;
                chef.setHeldItem(plate);
                
            } else {
                chef.setHeldItem(null);
                itemOnStation = plate;
            }
            
            System.out.println("   Plate sekarang berisi: " + plate.getContents().size() + " ingredient(s)");
            return true;
            
        } else {
            System.out.println("   Ingredient mungkin sudah ada di plate.");
            return false;
        }
    }
    
    public Item takeItem() {
        Item temp = itemOnStation;
        itemOnStation = null;
        return temp;
    }
    
    /**
     * Taruh item di station (manual, untuk special cases)
     */
    public boolean placeItem(Item item) {
        if (itemOnStation != null) {
            return false; // Sudah ada item
        }
        itemOnStation = item;
        return true;
    }
    
    /**
     * Cek apakah station kosong
     */
    @Override
    public boolean isEmpty() {
        return itemOnStation == null;
    }
    
    @Override
    public String toString() {
        return String.format("AssemblyStation{pos=(%d,%d), item=%s}",
            posX, posY, 
            itemOnStation != null ? itemOnStation.getName() : "empty");
    }
}