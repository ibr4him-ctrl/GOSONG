package model.station;

import java.util.ArrayDeque;
import java.util.Deque;

import model.chef.Chef;
import model.item.Item;

public class PlateStorage extends Station {

    private final Deque<Item> stack = new ArrayDeque<>();

    public PlateStorage(int x, int y) {
        super(x, y, "PlateStorage");
    }

    @Override
    public String getSymbol() {
        return "P";
    }

    public void pushInitialCleanPlate(Item plate) {
        plate.setClean(true);
        stack.push(plate);
    }

    public void pushDirtyPlate(Item plate) {
        plate.setClean(false);
        stack.push(plate);
    }

    @Override
    public boolean interact(Chef chef) {
        Item hand = chef.getHeldItem();

        if (hand != null) {
            return false;
        }

        if (stack.isEmpty()) return false;

        if (!stack.peek().isClean()) {
            Deque<Item> dirtyStack = new ArrayDeque<>();
            while (!stack.isEmpty() && !stack.peek().isClean()) {
                dirtyStack.push(stack.pop());
            }
            Item oneDirty = dirtyStack.pop();
            chef.setHeldItem(oneDirty);
            while (!dirtyStack.isEmpty()) stack.push(dirtyStack.pop());
            return true;
        }

        if (stack.peek().isClean()) {
            Item clean = stack.pop();
            chef.setHeldItem(clean);
            return true;
        }

        return false;
    }
}