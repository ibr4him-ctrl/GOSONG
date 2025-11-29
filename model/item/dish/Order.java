package model.item.dish;

import java.util.concurrent.atomic.AtomicInteger;

public class Order {
    public enum PizzaType {
        MARGHERITA("Pizza Margherita", 3, 120, 90),
        SOSIS("Pizza Sosis", 4, 120, 105),
        AYAM("Pizza Ayam", 4, 120, 105);
        
        private final String displayName;
        private final int ingredientCount;
        private final int baseReward;
        private final int baseTime;
        
        PizzaType(String displayName, int ingredientCount, int baseReward, int baseTime) {
            this.displayName = displayName;
            this.ingredientCount = ingredientCount;
            this.baseReward = baseReward;
            this.baseTime = baseTime;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public int getIngredientCount() {
            return ingredientCount;
        }
        
        public int getBaseReward() {
            return baseReward;
        }
        
        public int getBaseTime() {
            return baseTime;
        }
    }
    
    private static final AtomicInteger orderCounter = new AtomicInteger(0);
    
    private final int id;
    private final PizzaType pizzaType;
    private final int reward;
    private final int penalty;
    private final int timeLimit;
    private volatile int timeRemaining;
    private final long createdAt;
    
    public Order(PizzaType pizzaType) {
        this.id = orderCounter.incrementAndGet();
        this.pizzaType = pizzaType;
        this.reward = pizzaType.getBaseReward();
        this.penalty = -50;
        this.timeLimit = pizzaType.getBaseTime();
        this.timeRemaining = this.timeLimit;
        this.createdAt = System.currentTimeMillis();
    }
    
    public int getId() { 
        return id; 
    }
    
    public PizzaType getPizzaType() { 
        return pizzaType; 
    }
    
    public int getReward() { 
        return reward; 
    }
    
    public int getPenalty() { 
        return penalty; 
    }
    
    public int getTimeLimit() { 
        return timeLimit; 
    }
    
    public int getTimeRemaining() { 
        return timeRemaining; 
    }
    
    public long getCreatedAt() { 
        return createdAt; 
    }
    
    public synchronized int decrementTime() {
        if (timeRemaining > 0) {
            timeRemaining--;
        }
        return timeRemaining;
    }
    
    public boolean isExpired() {
        return timeRemaining <= 0;
    }
    
    @Override
    public String toString() {
        return String.format("Order #%d: %s [%d/%ds] (Reward: +%d, Penalty: %d)", 
            id, pizzaType.getDisplayName(), timeRemaining, timeLimit, reward, Math.abs(penalty));
    }
    
    public int compareByCreationTime(Order other) {
        return Long.compare(this.createdAt, other.createdAt);
    }
}