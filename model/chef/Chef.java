package model.chef;

import model.item.Item; 
import model.map.Position; 
public class Chef {
    private Position position; 
    private Item heldItem; 
    private boolean busy; 
    
    public Chef(Position startPosition){
        this.position = new Position (startPosition); 
        this.heldItem = null; 
    }

    public Chef(Item initialItem){
        this.heldItem = initialItem; 
    }

    public Item getHeldItem(){
        return heldItem; 
    }

    public void setHeldItem(Item item){
        this.heldItem = item; 
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

    public boolean isBusy(){
        return busy; 
    }

    public void setBusy(){
        this.busy = busy; 
    }
}
