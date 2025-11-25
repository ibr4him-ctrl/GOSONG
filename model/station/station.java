package model.station;

public abstract class station {
    protected ;//Positionposition
    protected ;//Item placedItem;

    public abstract void interact (Chef chef);
    //public abstract void update(float deltaTime);

    public Position getPosition(){
        return position;
    }

    public Item getPlaced(){
        return placedItem;
    }

    public void setPlacedItem(Item item){
        this.placedItem = item;
    }
}
