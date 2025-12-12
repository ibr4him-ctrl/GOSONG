package model.manager;

public class ScoreManager {

    private static ScoreManager instance;

    //Konstanta Skor
    public static final int POINTS_SUCCESS = 120;
    public static final int PENALTY_FAIL = -50;

    //State
    private int score = 0;
    private int successCount = 0;
    private int failCount = 0;

    private ScoreManager() {}

    public static ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }
        return instance;
    }

    public static void resetInstance() {
        if (instance != null) {
            instance.score = 0;
            instance.successCount = 0;
            instance.failCount = 0;
        }
        instance = null;
    }

    /** Reset skor ke 0 (dipanggil di awal game / saat restart). */
    public void resetScore() {
        score = 0;
        successCount = 0;
        failCount = 0;
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

    // === Metode Statistik ===

    /** Catat satu order berhasil. */
    public void recordSuccess() {
        this.successCount++;
    }

    /** Catat satu order gagal. */
    public void recordFail() {
        this.failCount++;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailCount() {
        return failCount;
    }
}