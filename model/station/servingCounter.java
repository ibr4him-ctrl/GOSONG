package model.station;

public class ServingCounter extends Station{
    private OrderManager orderManager;
    private ScoreManager scoreManager;
    
    public void interact(Chef chef);
}
