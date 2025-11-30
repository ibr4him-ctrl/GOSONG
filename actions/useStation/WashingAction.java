package actions.useStation;

import model.chef.Chef;
import model.station.Station;
import model.station.WashingStation;
import actions.Action;

public class WashingAction implements Action {

    /**
     * Trigger: Pemain menekan tombol Interact (V) di Washing Station.
     * Fungsi: 
     * 1. Menaruh piring kotor ke dalam sink.
     * 2. Mengambil piring bersih dari rak.
     * 3. Melanjutkan proses mencuci jika tertunda.
     */
    @Override
    public boolean execute(Chef chef, Station station) {
        // 1. Validasi: Pastikan interaksi dilakukan di Washing Station [W]
        if (station instanceof WashingStation) {
            WashingStation washingStation = (WashingStation) station;

            // 2. Delegasi: Panggil logika interact() di WashingStation
            return washingStation.interact(chef);
        }

        return false;
    }
}
