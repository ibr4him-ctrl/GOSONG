package actions.useStation;

import model.chef.Chef;
import model.station.CookingStation;
import model.station.Station;
import actions.Action;

public class CookingAction implements Action {

    /**
     * Method ini menangani Trigger:
     * "Pemain memasukkan suatu bahan yang dapat diterima ke alat masak"
     */
    @Override
    public boolean execute(Chef chef, Station station) {
        
        // 1. Cek apakah station target adalah CookingStation
        if (station instanceof CookingStation) {
            
            // 2. Casting station ke tipe CookingStation
            CookingStation cookingStation = (CookingStation) station;

            // 3. Panggil fungsi interact()
            // Di baris inilah logika "memasukkan bahan" dijalankan.
            // (Karena kamu sudah menulis logika if(hand instanceof Ingredient) di dalam CookingStation)
            return cookingStation.interact(chef);
        }

        // Jika station bukan CookingStation, aksi gagal
        return false;
    }
}