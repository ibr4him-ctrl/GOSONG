package model.station;

import java.util.ArrayDeque;
import java.util.Deque;

import model.chef.Chef;
import model.item.Item;
import model.enums.ItemType;

public class PlateStorage extends Station {

    private final Deque<Item> platestack; 

    public PlateStorage(int x, int y) {
        super(x, y, "PlateStorage");
        this.platestack = new ArrayDeque<>(); 
    }

    @Override
    public String getSymbol() {
        return "P";
    }

    public void pushInitialCleanPlate(Item plate) {
        if (plate == null) return; 
        if (plate.getItemType() != ItemType.PLATE) return; 
        plate.setClean(true);
        platestack.push(plate);
    }

    public void pushDirtyPlate(Item plate) {
        if (plate == null) return; 
        if (plate.getItemType() != ItemType.PLATE) return; 
        plate.setClean(false);
        platestack.push(plate);
    }

    @Override
    public boolean interact(Chef chef) {

        if (!isAdjacentTo(chef)) return false; 

        Item hand = chef.getHeldItem();

        if (hand != null) {
            return false;
        }

        if (platestack.isEmpty()) return false;

        if (!platestack.peek().isClean()) {
            Deque<Item> dirtyStack = new ArrayDeque<>();
            while (!platestack.isEmpty() && !platestack.peek().isClean()) {
                dirtyStack.push(platestack.pop());
            }
            Item oneDirty = dirtyStack.pop();
            chef.setHeldItem(oneDirty);
            while (!dirtyStack.isEmpty()) platestack.push(dirtyStack.pop());
            return true;
        }
        // Kalau top bersih → ambil satu piring bersih
        Item clean = platestack.pop();
        chef.setHeldItem(clean);
        return true;
    }
}