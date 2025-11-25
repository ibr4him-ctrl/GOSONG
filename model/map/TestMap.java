package model.map; 
public class TestMap {
    public static void main(String[] args) {
        PizzaMap map = new PizzaMap();
        map.printToConsole();
        System.out.println("Spawn points: " + map.getSpawnPoints());
    }
}
