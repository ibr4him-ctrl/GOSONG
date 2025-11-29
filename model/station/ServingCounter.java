package model.station;

import model.chef.Chef;
import model.item.Item;
import model.item.utensils.Plate;

public class ServingCounter extends Station {

    public ServingCounter(int x, int y) {
        super(x, y, "Serving");
    }

    @Override
    public String getSymbol() {
        return "S";
    }

    @Override
    public boolean interact(Chef chef) {
        Item hand = chef.getHeldItem();

        if (!(hand instanceof Plate plate)) {
            return false;
        }

        if (plate.isEmpty()) {
            return false;
        }

        plate.markDirty();
        chef.setHeldItem(null);

        return true;
    }
}