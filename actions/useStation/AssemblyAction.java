package actions.useStation;

import model.chef.Chef;
import model.station.AssemblyStation;
import model.station.Station;
import controller.Action;

public class AssemblyAction implements Action {

    
     //Fungsi: Melakukan Plating (Menaruh Pizza ke Piring atau sebaliknya).
    @Override
    public boolean execute(Chef chef, Station station) {
        // 1. Pastikan ini Assembly Station
        if (station instanceof AssemblyStation) {
            AssemblyStation assemblyStation = (AssemblyStation) station;
            return assemblyStation.interact(chef);
        }
        return false;
    }
}
