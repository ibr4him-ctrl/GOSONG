package model.station;

import java.util.ArrayDeque;
import java.util.Deque;

import model.chef.Chef;
import model.item.Item;

public class WashingStation extends Station {

    private final Deque<Item> dirtyPlates = new ArrayDeque<>();
    private final Deque<Item> cleanPlates = new ArrayDeque<>();

    private boolean washing;
    private double washProgressSeconds;
    private static final double WASH_TIME = 3.0; // 3 detik per plate

    public WashingStation(int x, int y) {
        super(x, y, "Washing");
    }

    @Override
    public String getSymbol() {
        return "W";
    }
    @Override
    public boolean interact(Chef chef) {
        Item hand = chef.getHeldItem();

        if (hand != null && !hand.isClean()) {
            dirtyPlates.addLast(hand);
            chef.setHeldItem(null);
            if (!washing) {
                washing = true;
                washProgressSeconds = 0.0;
            }
            return true;
        }

        if (hand == null && !cleanPlates.isEmpty()) {
            Item clean = cleanPlates.pollFirst();
            chef.setHeldItem(clean);
            return true;
        }

        return false;
    }
    public void update(double deltaSeconds) {
        if (!washing || dirtyPlates.isEmpty()) {
            washing = false;
            washProgressSeconds = 0.0;
            return;
        }

        washProgressSeconds += deltaSeconds;

        if (washProgressSeconds >= WASH_TIME) {
            Item plate = dirtyPlates.pollFirst();
            if (plate != null) {
                plate.setClean(true);
                cleanPlates.addLast(plate);
            }

            washProgressSeconds = 0.0;

            if (dirtyPlates.isEmpty()) {
                washing = false;
            }
        }
    }

    public boolean isWashing() {
        return washing;
    }

    public double getWashProgressSeconds() {
        return washProgressSeconds;
    }
}