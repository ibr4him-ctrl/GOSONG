package actions.useStation;

import model.chef.Chef;
import model.station.Station;
import model.station.TrashStation;
import actions.Action;

public class TrashAction implements Action {

    /**
     * Trigger: Pemain menekan tombol Interact/Drop di Trash Station.
     * Fungsi: Membuang item di tangan, atau mengosongkan isi piring/alat masak.
     */
    @Override
    public boolean execute(Chef chef, Station station) {
        // 1. Validasi: Pastikan interaksi dilakukan di Trash Station [T]
        if (station instanceof TrashStation) {
            TrashStation trashStation = (TrashStation) station;

            // 2. Delegasi: Panggil logika interact() di TrashStation
            return trashStation.interact(chef);
        }

        return false;
    }
}