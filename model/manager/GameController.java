package model.manager;

import model.enums.GameState;

/**
 * GameController adalah titik pusat orchestration untuk seluruh lifecycle game.
 * 
 * Tanggung jawab:
 * - Menyimpan dan mengelola GameState (PLAYING, PAUSED, GAME_OVER, MENU)
 * - Menghubungkan Timer, Score, OrderFailTracker
 * - Menentukan kondisi game ending (time up, fail threshold, semua order selesai)
 * - Membuat GameResult dan mengirimnya ke UI
 * - Menghentikan game loop saat game over
 */
public class GameController {
    private static GameController instance;

    private GameState currentState = GameState.MENU;
    private double elapsedTime = 0.0;
    private GameResult lastResult = null;
    private long startTimeNs = 0L;

    // Referensi ke subsistem
    private final OrderManager orderManager;
    private final ScoreManager scoreManager;
    private final OrderFailTracker failTracker;

    private static final double SESSION_LIMIT_SECONDS = 240.0;

    // Batas maksimal order gagal (total, bukan berurutan)
    private static final int MAX_TOTAL_FAILS = 2;

    private GameController() {
        this.orderManager = OrderManager.getInstance();
        this.scoreManager = ScoreManager.getInstance();
        this.failTracker = OrderFailTracker.getInstance();
    }

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    /**
     * Dipanggil saat awal game dimulai.
     */
    public void startGame() {
        // Cegah multiple start calls
        if (currentState == GameState.PLAYING) {
            System.out.println("[GameController] Game already started, ignoring duplicate call.");
            return;
        }
        
        System.out.println("[GameController] Game started.");
        elapsedTime = 0.0;
        startTimeNs = System.nanoTime();
        lastResult = null;
        currentState = GameState.PLAYING;
        
        // Reset semua subsistem
        scoreManager.resetScore();
        failTracker.resetTracker();
        orderManager.init();
    }

    /**
     * Update game state setiap frame dengan deltaSeconds.
     * Dipanggil dari GamePanel.update() atau main game loop.
     */
    public void update(double deltaSeconds) {
        if (currentState != GameState.PLAYING) {
            return;
        }

        // Hitung elapsed time secara akurat berdasarkan waktu mulai.
        elapsedTime = (System.nanoTime() - startTimeNs) / 1_000_000_000.0;
        // Di sini kita hanya perlu check kondisi ending

        // === CEK KONDISI GAME OVER ===

        // 1. Time limit tercapai
        if (elapsedTime >= SESSION_LIMIT_SECONDS) {
            handleSessionTimeUp();
            return;
        }
    }

    /**
     * Akhiri game dengan status tertentu.
     * Buat GameResult dan ubah state ke GAME_OVER.
     */
    private void endGame(boolean isPass, String reason) {
        if (currentState == GameState.GAME_OVER) {
            return; // sudah game over, jangan panggil berkali-kali
        }

        currentState = GameState.GAME_OVER;
        int finalScore = scoreManager.getScore();

        // Hitung elapsed time berdasarkan wall-clock dari awal game untuk akurasi
        double finalElapsed;
        if (startTimeNs > 0L) {
            finalElapsed = (System.nanoTime() - startTimeNs) / 1_000_000_000.0;
            // Jangan melebihi session limit
            if (finalElapsed > SESSION_LIMIT_SECONDS) finalElapsed = SESSION_LIMIT_SECONDS;
        } else {
            finalElapsed = elapsedTime;
        }

        lastResult = new GameResult(
            finalScore, 
            finalElapsed, 
            isPass, 
            reason, 
            scoreManager.getSuccessCount(), 
            scoreManager.getFailCount());
        System.out.println("[GameController] Game Over! " + lastResult);

        // Panggil Main untuk menampilkan UI game over dengan result
        // Bedakan antara menang (PASS) dan kalah (FAIL)
        if (isPass) {
            main.Main.showGameSummary(lastResult);
        } else {
            main.Main.showGameOver(lastResult);
        }
    }

    /**
     * Dipanggil oleh subsistem (OrderManager) ketika sesi waktu habis.
     */
    public void handleSessionTimeUp() {
        if (currentState != GameState.PLAYING) return;        
        endGame(false, "Time's up!");
    }

    /**
     * Dipanggil oleh subsistem ketika semua order selesai.
     */
    public void handleAllOrdersCompleted() {
        if (currentState != GameState.PLAYING) return;
        endGame(true, "All orders completed!");
    }

    /**
     * Dipanggil oleh OrderManager saat suatu order expired/gagal.
     */
    public void onOrderFailed() {
        scoreManager.recordFail();
        
        // Cek apakah total order gagal sudah mencapai batas
        if (scoreManager.getFailCount() >= MAX_TOTAL_FAILS) {
            endGame(false, "Too many failed orders!");
        }
    }

    /**
     * Dipanggil oleh ServingCounter/order system saat order berhasil di-serve.
     */
    public void onOrderSuccess() {
        scoreManager.recordSuccess();
        failTracker.resetStreak();
    }

    /**
     * Restart game ke state awal.
     */
    public void restart() {
        System.out.println("[GameController] Restarting game.");
        resetInstance();
        instance = null;
        startGame();
    }

    // === GETTER ===

    public GameState getCurrentState() {
        return currentState;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public GameResult getLastResult() {
        return lastResult;
    }

    public boolean isGameOver() {
        return currentState == GameState.GAME_OVER;
    }

    public boolean isGamePlaying() {
        return currentState == GameState.PLAYING;
    }
}