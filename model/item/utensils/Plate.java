package model.item.utensils;

import java.util.HashSet;
import java.util.Set;

import model.enums.ItemLocation;
import model.enums.ItemType;
import model.item.Item;
import model.item.Preparable;

public class Plate extends Item {
// plate dipakai kalau plating jika bersih, kalau ssetgelah serving plate akan menjadi kotor dan harus dicuci sebelum bisa dipakai 
    private Set<Preparable> contents;

    public Plate() {
        super("Plate", ItemType.PLATE, ItemLocation.KITCHEN_UTENSILS);
        this.contents = new HashSet<>();
        this.setClean(true);
    }

    public Set<Preparable> getContents() {
        return new HashSet<>(contents);
    }

    public boolean isEmpty() {
        return contents.isEmpty();
    }

    public boolean addIngredient(Preparable ingredient) {
        if (!isClean()) return false;
        if (ingredient == null || !ingredient.canBePlacedOnPlate()) return false;
        return contents.add(ingredient);
    }

    public boolean canAccept(Preparable ingredient) {
        return isClean() && ingredient != null && ingredient.canBePlacedOnPlate();
    }

    public void clear() {
        contents.clear();
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
        return "Plate(" + status + ", items=" + contents.size() + ")";
    }
}
