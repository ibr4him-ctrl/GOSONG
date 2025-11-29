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
    private static final double WASH_TIME = 3.0;

    private Chef currentChef;

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
                currentChef = chef;
                currentChef.setBusy(true);
            }
            return true;
        }

        if (hand == null && !cleanPlates.isEmpty()) {
            chef.setHeldItem(cleanPlates.pollFirst());
            return true;
        }

        return false;
    }

    public void update(double deltaSeconds) {
        if (!washing || dirtyPlates.isEmpty()) return;

        washProgressSeconds += deltaSeconds;

        if (washProgressSeconds >= WASH_TIME) {
            Item plate = dirtyPlates.pollFirst();
            plate.setClean(true);
            cleanPlates.addLast(plate);

            washProgressSeconds = 0.0;

            if (dirtyPlates.isEmpty()) {
                washing = false;
                if (currentChef != null) {
                    currentChef.setBusy(false);
                    currentChef = null;
                }
            }
        }
    }

    public void pauseWashing() {
        washing = false;
        if (currentChef != null) {
            currentChef.setBusy(false);
            currentChef = null;
        }
    }

    public double getProgressRatio() {
        if (WASH_TIME <= 0) return 0.0;
        return Math.min(1.0, washProgressSeconds / WASH_TIME);
    }

    public int getProgressPercent() {
        return (int) Math.round(getProgressRatio() * 100.0);
    }

    public String getProgressBar(int width) {
        double ratio = getProgressRatio();
        int filled = (int) Math.round(ratio * width);
        if (filled > width) filled = width;

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < width; i++) {
            sb.append(i < filled ? '#' : '-');
        }
        sb.append("] ").append(getProgressPercent()).append('%');
        return sb.toString();
    }

    public boolean isWashing() {
        return washing;
    }

    public double getWashProgressSeconds() {
        return washProgressSeconds;
    }
}