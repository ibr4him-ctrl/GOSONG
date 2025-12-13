package model.manager;

/**
 * OrderFailTracker mencatat jumlah order yang gagal secara berturut-turut.
 * Digunakan oleh GameController untuk menentukan apakah game harus berakhir FAIL.
 * 
 * Logika:
 * - Setiap kali order expired atau serve gagal → recordFail()
 * - Setiap kali order berhasil di-serve → resetStreak()
 * - Jika streak >= threshold (misal 3) → trigger game over FAIL
 */
public class OrderFailTracker {
    private static OrderFailTracker instance;

    private int currentFailStreak = 0;
    private static final int FAIL_THRESHOLD = 2; // game over FAIL jika 2 order gagal berturut-turut

    private OrderFailTracker() {}

    public static OrderFailTracker getInstance() {
        if (instance == null) {
            instance = new OrderFailTracker();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    /** Reset tracker ke state awal (dipanggil di awal game / restart) */
    public void resetTracker() {
        currentFailStreak = 0;
    }

    /** Catat fail order → increment streak */
    public void recordFail() {
        currentFailStreak++;
        System.out.println("[OrderFailTracker] Fail order. Current streak: " + currentFailStreak);
    }

    /** Reset streak ke 0 (dipanggil saat order berhasil) */
    public void resetStreak() {
        if (currentFailStreak > 0) {
            System.out.println("[OrderFailTracker] Order berhasil! Streak direset dari " + currentFailStreak + " ke 0.");
        }
        currentFailStreak = 0;
    }

    /** Cek apakah sudah mencapai fail threshold */
    public boolean isFailThresholdReached() {
        return currentFailStreak >= FAIL_THRESHOLD;
    }

    public int getCurrentFailStreak() {
        return currentFailStreak;
    }

    public static int getFailThreshold() {
        return FAIL_THRESHOLD;
    }
}
