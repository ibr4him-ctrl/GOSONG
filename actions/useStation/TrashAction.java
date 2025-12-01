package actions.useStation;

import model.chef.Chef;
import model.station.Station;
import model.station.TrashStation;
import controller.Action;

public class TrashAction implements Action {
     //Fungsi: Membuang item di tangan, atau mengosongkan isi piring/alat masak.
    @Override
    public boolean execute(Chef chef, Station station) {
        if (station instanceof TrashStation) {
            TrashStation trashStation = (TrashStation) station;
            return trashStation.interact(chef);
        }
        return false;
    }
}