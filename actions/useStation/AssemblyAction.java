package actions.useStation;

import model.chef.Chef;
import model.station.AssemblyStation;
import model.station.Station;
import actions.Action;

public class AssemblyAction implements Action {

    /**
     * Trigger: Pemain menekan tombol Interact di Assembly Station.
     * Fungsi: Melakukan Plating (Menaruh Pizza ke Piring atau sebaliknya).
     */
    @Override
    public boolean execute(Chef chef, Station station) {
        // 1. Pastikan ini Assembly Station
        if (station instanceof AssemblyStation) {
            AssemblyStation assemblyStation = (AssemblyStation) station;

            // 2. Panggil interact()
            // Di dalam interact() AssemblyStation, kamu HANYA perlu menangani:
            // - Plate
            // - Ingredient / Dish (Pizza)
            // - (TIDAK PERLU menangani Oven/Panci)
            return assemblyStation.interact(chef);
        }
        return false;
    }
}
