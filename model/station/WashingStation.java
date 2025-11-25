package model.station;

import java.util.List;

public class WashingStation extends Station{
    private List<Plate> dirtyPlates;
    private List<Plate> cleanPlates;
    private float washTimer;
    
    public void interact(Chef chef);
}
