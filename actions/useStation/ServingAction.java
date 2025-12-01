package actions.useStation;
import model.chef.Chef;
import model.station.ServingCounter;
import model.station.Station;
import actions.Action;

public class ServingAction implements Action {

    //Fungsi: Memicu proses penyajian makanan di Serving Counter.
    @Override
    public boolean execute(Chef chef, Station station) {
        if (station instanceof ServingCounter) {
            ServingCounter servingCounter = (ServingCounter) station;
            return servingCounter.interact(chef);
        }
        return false;
    }
}
