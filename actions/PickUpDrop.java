package actions;

import model.chef.Chef;
import model.item.Item;
import model.item.utensils.Plate;
import model.station.CookingStation;
import model.station.Station;
import model.station.WashingStation;
// import model.station.IngredientStorage; // Uncomment jika sudah ada class ini

public class PickUpDrop implements Action {

    @Override
    public boolean execute(Chef chef, Station station) {
        if (station == null) return false;

        Item hand = chef.getHeldItem();

        // --- CEK KEGAGALAN (RESTRICTIONS) ---

        // Cek Washing Station: Tidak boleh menaruh piring bersih
        if (station instanceof WashingStation) {
            if (hand instanceof Plate && ((Plate) hand).isClean()) {
                System.out.println("Gagal: Piring bersih tidak perlu dicuci!");
                return false;
            }
        }

        // Cek Cooking Station (Oven)
        // Dalam konteks Oven kamu: Piring bersih tidak bisa dimasukkan ke Oven (hanya Pizza mentah yang bisa).
        if (station instanceof CookingStation) {
            if (hand instanceof Plate) {
                 CookingStation cs = (CookingStation) station;
                 boolean isFoodReady = !cs.getOven().isCooking() && !cs.getOven().getContents().isEmpty();
                 
                 if (!isFoodReady) {
                     System.out.println("Gagal: Tidak bisa memasukkan piring ke Oven (Kosong/Sedang Masak)!");
                     return false;
                 }
            }
        }
        
        return station.interact(chef);
    }
}