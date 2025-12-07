package model.manager;

public class ScoreManager {

    private static ScoreManager instance;

    private int score = 0;

    private ScoreManager() {}

    public static ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }
        return instance;
    }

    /** Reset skor ke 0 (dipanggil di awal game / saat restart). */
    public void reset() {
        score = 0;
    }

    /** Tambah / kurangi skor. Boleh negatif. */
    public void add(int delta) {
        score += delta;
        System.out.println("[Score] delta=" + delta + " | total=" + score);
    }

    /** Ambil skor sekarang. */
    public int getScore() {
        return score;
    }
}
