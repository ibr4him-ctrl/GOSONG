package model.station;

import java.util.ArrayDeque;
import java.util.Deque;
import model.chef.Chef;
import model.enums.ItemType;
import model.item.Item;

public class PlateStorage extends Station {

    // Satu-satunya PlateStorage di map
    private static PlateStorage instance;

    private final Deque<Item> platestack;

    public PlateStorage(int x, int y) {
        super(x, y, "PlateStorage");
        this.platestack = new ArrayDeque<>();
        instance = this; // daftarkan diri sebagai instance global
    }

    public static PlateStorage getInstance() {
        return instance;
    }

    @Override
    public String getSymbol() {
        return "P";
    }

    public synchronized void pushInitialCleanPlate(Item plate) {
        if (plate == null) return;
        if (plate.getItemType() != ItemType.PLATE) return;
        plate.setClean(true);
        platestack.push(plate);
    }

    public synchronized void pushDirtyPlate(Item plate) {
        if (plate == null) return;
        if (plate.getItemType() != ItemType.PLATE) return;
        plate.setClean(false);
        platestack.push(plate);
    }

    @Override
    public synchronized boolean interact(Chef chef) {
        if (!isAdjacentTo(chef)) return false;

        // gak boleh drop apa pun di sini
        if (chef.getHeldItem() != null) return false;

        if (platestack.isEmpty()) return false;

        if (!platestack.peek().isClean()) {
            // ada piring kotor di atas → ambil 1 piring kotor
            Item dirty = platestack.pop();
            chef.setHeldItem(dirty);
            return true;
        }

        // top clean → ambil 1 piring bersih
        Item clean = platestack.pop();
        chef.setHeldItem(clean);
        return true;
    }

    public synchronized int getPlateCount() {
        return platestack.size();
    }
}
