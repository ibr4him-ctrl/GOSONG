package util;

import model.item.Dish;

/**
 * Menghitung skor untuk setiap dish / aksi dalam game.
 * Bisa dikembangkan sesuai kebutuhan (time bonus, penalty, dsb).
 */
public class ScoreCalculator {

    private int baseDishScore = 100;    
    private int wrongDishPenalty = -50;  
    private int burnedPenalty = -30;    
    private int timeBonusMultiplier = 2; 

    public ScoreCalculator() {}

    public int calculateScore(Dish dish, boolean isValidDish, int timeRemaining) {
        int score = 0;

        if (isValidDish) {
            score += baseDishScore;
        } else {
            score += wrongDishPenalty;
        }

        // Cek apakah ada ingredient burned
        if (dish != null && dish.getComponents() != null) {
            boolean hasBurned = dish.getComponents().stream()
                .anyMatch(p -> p.toString().contains("BURNED"));

            if (hasBurned) {
                score += burnedPenalty;
            }
        }

        if (timeRemaining > 0) {
            score += timeRemaining * timeBonusMultiplier;
        }

        return score;
    }

    public void setBaseDishScore(int score) { this.baseDishScore = score; }
    public void setWrongDishPenalty(int penalty) { this.wrongDishPenalty = penalty; }
    public void setBurnedPenalty(int penalty) { this.burnedPenalty = penalty; }
    public void setTimeBonusMultiplier(int mult) { this.timeBonusMultiplier = mult; }
}
