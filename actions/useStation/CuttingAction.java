package actions.useStation;

import model.chef.Chef;
import model.station.CuttingStation;
import model.station.Station;
import actions.Action;

public class CuttingAction implements Action {

    @Override
    public boolean execute(Chef chef, Station station) {
        // Validasi: Pastikan target adalah CuttingStation
        if (station instanceof CuttingStation) {
            CuttingStation cuttingStation = (CuttingStation) station;
            return cuttingStation.interact(chef);
        }
        return false;
    }
}