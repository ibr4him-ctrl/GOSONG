package model.station;

import java.util.List;

public class washingStation extends station{
    private List<Plate> dirtyPlates;
    private List<Plate> cleanPlates;
    private float washTimer;
    
    public void interact(Chef chef);
}