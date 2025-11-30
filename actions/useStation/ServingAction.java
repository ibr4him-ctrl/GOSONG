package actions.useStation;
import model.chef.Chef;
import model.station.ServingCounter;
import model.station.Station;
import actions.Action;

public class ServingAction implements Action {

    /**
     * Trigger: Pemain menekan tombol Interact/Drop.
     * Fungsi: Memicu proses penyajian makanan di Serving Counter.
     */
    @Override
    public boolean execute(Chef chef, Station station) {
        // 1. Validasi: Pastikan interaksi dilakukan di Serving Counter [S]
        if (station instanceof ServingCounter) {
            ServingCounter servingCounter = (ServingCounter) station;

            // 2. Delegasi: Panggil logika interact() di ServingCounter
            // Logika validasi dish, skor, dan pengembalian piring ada di sana.
            return servingCounter.interact(chef);
        }

        return false;
    }
}
