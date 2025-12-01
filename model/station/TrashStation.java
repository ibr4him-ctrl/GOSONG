// model/station/TrashStation.java
package model.station;

import model.chef.Chef;
import model.enums.ItemType;
import model.interfaces.CookingDevice;
import model.item.Item;
import model.item.utensils.Oven;
import model.item.utensils.Plate;

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
            if (hand instanceof Plate) {
            Plate plate = (Plate) hand;
            
            // Jika piring sudah kosong, kita Return False (Gagal)
            // Karena kita tidak mau membuang piringnya.
            if (plate.isEmpty()) {
                System.out.println("Gagal: Tidak bisa membuang piring kosong!");
                return false; 
            }

            // Jika ada isinya, hapus isinya saja
            plate.clear(); // atau plate.removeDish() / plate.contents.clear()
            System.out.println("Isi piring dibuang. Piring tetap di tangan.");
            return true;

        }
        //     if (hand instanceof Oven oven) {
        //         oven.clearContents(); // kamu sudah punya clearContents() di Oven
        //         // utensil tetap di tangan Chef
        //         return true;
        //     }
        //     // untuk utensil lain, nanti bisa dibuat method clear sendiri
        //     return false;
        }

        // Selain itu (ingredient / dish / dsb.) → buang item-nya
        chef.setHeldItem(null);
        return true;
    }
}