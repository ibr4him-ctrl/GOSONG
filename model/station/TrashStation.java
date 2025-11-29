// model/station/TrashStation.java
package model.station;

import model.chef.Chef;
import model.enums.ItemType;
import model.interfaces.CookingDevice;
import model.item.Item;
import model.item.utensils.Oven;

public class TrashStation extends Station {

    public TrashStation(int x, int y) {
        super(x, y, "Trash");
    }

    @Override
    public String getSymbol() {
        return "T";
    }

    @Override
    public boolean interact(Chef chef) {
        Item hand = chef.getHeldItem();
        if (hand == null) return false;

        if (hand.getItemType() == ItemType.KITCHEN_UTENSIL) {
            if (hand instanceof Oven oven) {
                oven.clearContents(); // kamu sudah punya clearContents() di Oven
                // utensil tetap di tangan Chef
                return true;
            }
            // untuk utensil lain, nanti bisa dibuat method clear sendiri
            return false;
        }

        // Selain itu (ingredient / dish / dsb.) → buang item-nya
        chef.setHeldItem(null);
        return true;
    }
}