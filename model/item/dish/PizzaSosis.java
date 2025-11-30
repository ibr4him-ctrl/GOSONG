package model.item.dish; 

import java.util.List;
import model.enums.ItemLocation;
import model.item.Dish; 
import model.item.Preparable; 

public class PizzaSosis extends Dish{
    public PizzaSosis(ItemLocation location){
        super(model.item.dish.Order.PizzaType.SOSIS, location); 
    }

    public PizzaSosis(ItemLocation location, List<Preparable> components){
        this(location); 
        if (components != null){
            for (Preparable p : components){
                addComponent(p);
            }
        }
    }
}