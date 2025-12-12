package model.item.utensils;

import java.util.HashSet;
import java.util.Set;
import model.enums.ItemLocation;
import model.enums.ItemType;
import model.item.Dish;
import model.item.Item;
import model.item.Preparable;

public class Plate extends Item {
    // Ingredients mentah/chopped
    private Set<Preparable> contents;
    
    // Dish final (pizza matang)
    private Dish finalDish;
    
    public Plate() {
        super("Plate", ItemType.PLATE, ItemLocation.KITCHEN_UTENSILS);
        this.contents = new HashSet<>();
        this.finalDish = null;
        this.setClean(true);
    }
    
    /**
     * Set dish final (dipanggil setelah keluar dari oven).
     */
    public void setDish(Dish dish) {
        this.finalDish = dish;
        // Kosongkan contents karena sudah jadi dish final
        this.contents.clear();
    }
    
    /**
     * Get dish final (untuk serving).
     */
    public Dish getDish() {
        return this.finalDish;
    }

    /**
     * Menghapus isi piring (Dish diambil/dimakan).
     */
    public void removeDish() {
        this.finalDish = null;
        this.contents.clear();
    }

    public Set<Preparable> getContents() {
        return new HashSet<>(contents);
    }

    public boolean isEmpty() {
        return contents.isEmpty() && finalDish == null;
    }

    public boolean addIngredient(Preparable ingredient) {
        if (!isClean()) return false;
        if (ingredient == null || !ingredient.canBePlacedOnPlate()) return false;
        
        // Jika sudah ada dish final, tidak bisa tambah ingredient lagi
        if (finalDish != null) return false;
        
        return contents.add(ingredient);
    }

    public boolean canAccept(Preparable ingredient) {
        // Jika sudah ada dish final, tidak bisa terima ingredient lagi
        if (finalDish != null) return false;
        return isClean() && ingredient != null && ingredient.canBePlacedOnPlate();
    }

    public void clear() {
        contents.clear();
        finalDish = null;
    }

    @Override
    public void wash() {
        super.wash();
        clear();
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    public String toString() {
        String status = isClean() ? "Clean" : "Dirty";
        if (finalDish != null) {
            return "Plate(" + status + ", dish=" + finalDish.getName() + ")";
        }
        return "Plate(" + status + ", items=" + contents.size() + ")";
    }
    
    public boolean removeIngredient(Preparable p) {
        return contents.remove(p);   
    }
}