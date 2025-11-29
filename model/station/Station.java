package model.station;

import model.item.Item;
import model.chef.Chef;

public abstract class Station {
    protected int posX;
    protected int posY;
    protected String stationType;
    protected Item itemOnStation;
    
    public Station(int x, int y, String type) {
        this.posX = x;
        this.posY = y;
        this.stationType = type;
        this.itemOnStation = null;
    }

    //ini buat ngecek kalo chef udah deket sama station ato blom
    // public boolean isAdjacentTo(Chef chef) {
       //int chefX = chef.getPosition().getX();
        //int chefY = chef.getPosition().getY();
        //int deltaX = Math.abs(chefX - posX); //cek arah
        //int deltaY = Math.abs(chefY - posY);
        //return (deltaX == 1 && deltaY == 0) || (deltaX == 0 && deltaY == 1);}
    
    public abstract boolean interact(Chef chef);
    public abstract String getSymbol();

    public boolean placeItem(Item item) {
        if (itemOnStation != null) {
            return false; 
        }
        itemOnStation = item;
        return true;
    }

    public Item takeItem() {
        Item temp = itemOnStation;
        itemOnStation = null;
        return temp;
    }
    
    public boolean isEmpty() {
        return itemOnStation == null;
    }
    
    public String getStatusDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(stationType.toUpperCase()).append(" ===\n");
        sb.append("Position: (").append(posX).append(", ").append(posY).append(")\n");
        sb.append("Status: ").append(isEmpty() ? "KOSONG" : "TERISI").append("\n");
        
        if (!isEmpty()) {
            sb.append("Item: ").append(itemOnStation.getName()).append("\n");
        }
        
        return sb.toString();
    }
    
    //public boolean canBeUsed(Chef chef) {
    //   return isAdjacentTo(chef);
    //}
    
    //public int getPosX() {
        //return posX;}
    
    //public int getPosY() {
      //  return posY;}
    
    public String getStationType() {
        return stationType;
    }
    
    public Item getItemOnStation() {
        return itemOnStation;
    }
    
    public void setItemOnStation(Item item) {
        this.itemOnStation = item;
    }

    public boolean isWalkable() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s di (%d, %d) - %s", 
            stationType, posX, posY, 
            isEmpty() ? "Kosong" : "punya " + itemOnStation.getName());
    }
}