package model.item; 

public interface Preparable{
    boolean canBeChopped(); 
    boolean canBeCooked(); 
    boolean canBePlacedOnPlate(); 


    void chop(); 
    void cook(); 
}