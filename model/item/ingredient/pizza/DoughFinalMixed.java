package model.item.ingredient.pizza;

import model.item.Item;
import model.item.Item.ItemLocation;
import model.item.Item.ItemType;

public class DoughFinalMixed extends Item {
    public enum DoughState {
        MIXED,      // Sudah dicampur
        KNEADED,    // Sudah diuleni
        RESTED,     // Sudah didiamkan
        READY_TO_BAKE // Siap dipanggang
    }

    private DoughState state;
    private int quality; // Kualitas adonan (1-100)
    private Dough dough;
    private Cheese cheese;
    private Chicken chicken;
    private Sausage sausage;
    private Tomato tomato;

    public DoughFinalMixed() {
        super("Mixed Dough", ItemType.INGREDIENT, ItemLocation.COUNTER);
        this.state = DoughState.MIXED;
        this.quality = 50; // Kualitas default
        this.setEdible(false); // Adonan mentah tidak bisa dimakan
        
// Initialize ingredients
        this.dough = new Dough();
        this.cheese = new Cheese();
        this.chicken = new Chicken();
        this.sausage = new Sausage();
        this.tomato = new Tomato();
    }

    // Method untuk menguleni adonan
    public void knead() {
        if (state == DoughState.MIXED) {
            state = DoughState.KNEADED;
            quality = Math.min(100, quality + 20); // Menguleni meningkatkan kualitas
        }
    }

    // Method untuk mendiamkan adonan
    public void rest() {
        if (state == DoughState.KNEADED) {
            state = DoughState.RESTED;
            quality = Math.min(100, quality + 10); // Mendiamkan meningkatkan kualitas
        }
    }

    // Method untuk menandai adonan siap dipanggang
    public void readyToBake() {
        if (state == DoughState.RESTED) {
            state = DoughState.READY_TO_BAKE;
        }
    }

    // Getters for ingredients
    public Dough getDough() {
        return dough;
    }

    public Cheese getCheese() {
        return cheese;
    }

    public Chicken getChicken() {
        return chicken;
    }

    public Sausage getSausage() {
        return sausage;
    }

    public Tomato getTomato() {
        return tomato;
    }

    // Getter dan Setter
    public DoughState getState() {
        return state;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = Math.max(0, Math.min(100, quality));
    }

    @Override
    public String toString() {
        return "Dough{" +
                "state=" + state +
                ", quality=" + quality +
                '}';
    }
}
