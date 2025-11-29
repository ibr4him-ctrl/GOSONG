package model.chef;

import model.item.Item; 
public class Chef {
    private Item heldItem; 

    public Chef(){
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
}
