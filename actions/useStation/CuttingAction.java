package actions.useStation;

import model.chef.Chef;
import model.station.CuttingStation;
import model.station.Station;
import actions.Action;

public class CuttingAction implements Action {

    /**
     * Menangani Trigger: Pemain menekan tombol Interact (V) di Cutting Station.
     */
    @Override
    public boolean execute(Chef chef, Station station) {
        // 1. Validasi: Pastikan target adalah CuttingStation
        if (station instanceof CuttingStation) {
            CuttingStation cuttingStation = (CuttingStation) station;

            // 2. Delegasi ke method interact() milik station
            // Method ini akan menangani:
            // - Cek apakah input valid (Ingredient RAW)
            // - Mengubah status Chef menjadi BUSY (chef.setBusy(true))
            // - Memulai proses pemotongan (cutting = true)
            // - Resume progress bar jika sebelumnya terhenti
            return cuttingStation.interact(chef);
        }

        // Jika station salah atau interaksi gagal (misal bahan bukan RAW)
        return false;
    }
}