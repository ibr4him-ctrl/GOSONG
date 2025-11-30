package actions.useStation;

import model.chef.Chef;
import model.station.Station;
import model.station.WashingStation;
import actions.Action;

public class WashingAction implements Action {
     // Fungsi: 
     // 1. Menaruh piring kotor ke dalam sink.
     // 2. Mengambil piring bersih dari rak.
     // 3. Melanjutkan proses mencuci jika tertunda.
    @Override
    public boolean execute(Chef chef, Station station) {
        if (station instanceof WashingStation) {
            WashingStation washingStation = (WashingStation) station;
            return washingStation.interact(chef);
        }
        return false;
    }
}
