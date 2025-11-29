package model.chef;

import model.item.Item; 
import model.map.Position; 
public class Chef {
    private Position position; 
    private Item heldItem; 
    private boolean busy; 
    private String name;
    private ChefInventory inventory;
    private boolean isActive;

    public Chef(Position startPosition){
        this.position = new Position (startPosition); 
        this.heldItem = null; 
        this.name = name;
        this.inventory = new ChefInventory();
        this.isActive = true;
        this.busy = false;
    }

    public Chef(Item initialItem){
        this.heldItem = initialItem; 
    }

    public String getName() { 
        return name; 
    }
    
    //Posisi chef di map 
    public Position getPosition(){
        return position; 
    }
    public void setPosition(Position pos){
        this.position = new Position(pos); 
    }

    public void setPosition(int x, int y) {
        if (this.position == null) {
            this.position = new Position(x, y);
        } else {
            this.position.setX(x);
            this.position.setY(y);
        }
    }

    public Item getHeldItem() {
        return inventory.getHeldItem();
    }
    
    public void setHeldItem(Item item){
        this.heldItem = item; 
    }

    public boolean isActive() { 
        return isActive; 
    }

    public boolean isBusy(){
        return busy; 
    }

    public void setBusy(boolean busy){
        this.busy = busy; 
    }
    public boolean pickUpItem(Item item) {
        return inventory.addItem(item);
    }
    
    public Item dropItem() {
        return inventory.removeItem();
    }
    
    public boolean hasItem() {
        return inventory.hasItem();
    }

    @Override
    public String toString() {
        String itemStatus = hasItem() ? getHeldItem().toString() : "Empty Handed";
        return String.format("Chef %s at %s | Status: %s", name, position, itemStatus);
    }
}


