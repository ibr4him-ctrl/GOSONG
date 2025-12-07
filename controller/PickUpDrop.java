package controller;

import model.chef.Chef;
import model.item.Item;
import model.item.utensils.Plate;
import model.station.Station;
import model.station.WashingStation;

public class PickUpDrop implements Action {

    @Override
    public boolean execute(Chef chef, Station station) {
        if (station == null) return false;

        Item hand = chef.getHeldItem();

        // === RESTRIKSI KHUSUS WASHING STATION (sesuai spek) ===
        if (station instanceof WashingStation) {
            if (hand instanceof Plate plate && plate.isClean()) {
                System.out.println("[PickUpDrop] Gagal: Piring bersih tidak perlu dicuci!");
                return false;
            }
        }

        // Selain pengecualian di atas, semua logika
        // pick up / drop / plating diserahkan ke station.interact()
        return station.interact(chef);
    }
}
