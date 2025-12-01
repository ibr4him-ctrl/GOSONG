package actions.useStation;

import model.chef.Chef;
import model.station.CookingStation;
import model.station.Station;
import controller.Action;

public class CookingAction implements Action {

    
     // "Pemain memasukkan suatu bahan yang dapat diterima ke alat masak"
    @Override
    public boolean execute(Chef chef, Station station) {
        
        // Cek apakah station target adalah CookingStation
        if (station instanceof CookingStation) {
            
            // Casting station ke tipe CookingStation
            CookingStation cookingStation = (CookingStation) station;
            return cookingStation.interact(chef);
        }
        return false;
    }
}