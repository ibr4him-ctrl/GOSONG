package actions.useStation;

import model.chef.Chef;
import model.station.CookingStation;
import model.station.Station;

public class CookingAction {

    public boolean execute(Chef chef, Station station) {
        if (!(station instanceof CookingStation cs)) {
            return false;
        }
        return cs.interact(chef);   // <-- delegasi semua logika ke CookingStation
    }
}
