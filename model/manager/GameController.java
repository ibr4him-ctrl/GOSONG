package model.manager;

import model.enums.GameState;

/**
 * GameController adalah titik pusat orchestration untuk seluruh lifecycle game.
 **/
public class GameController {
    private static GameController instance;

    private GameState currentState = GameState.MENU;
    private double elapsedTime = 0.0;
    private GameResult lastResult = null;
    private long startTimeNs = 0L;

    private final OrderManager orderManager;
    private final ScoreManager scoreManager;
    private final OrderFailTracker failTracker;
    private static final double SESSION_LIMIT_SECONDS = 240.0; //batas waktu timer
    private static final int PASS_SCORE_THRESHOLD = 240; //batas skor untuk PASS

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

        // Hitung elapsed time secara akurat berdasarkan waktu mulai
        elapsedTime = (System.nanoTime() - startTimeNs) / 1_000_000_000.0;

        // === CEK KONDISI GAME OVER ===

        // 1. Time limit tercapai
        if (elapsedTime >= SESSION_LIMIT_SECONDS) {
            handleSessionTimeUp();
            return;
        }

        // 2. Cek apakah semua order sudah selesai (immediate check)
        if (orderManager.getTotalSpawnedOrders() >= OrderManager.getMaxTotalOrders() 
            && orderManager.getActiveOrders().isEmpty()) {
            handleAllOrdersCompleted();
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
        System.out.println("[GameController] Reason: " + reason + " | Pass: " + isPass);

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

        int finalScore = scoreManager.getScore();
        boolean isPass = finalScore >= PASS_SCORE_THRESHOLD;

        if (isPass) {
            System.out.println("[GameController] Tme's up! Score: " + finalScore + ". Kamu PASS.");
            endGame(true, "Time's up! Kamu PASS!");
        } else {
            System.out.println("[GameController] Time's up! Score " + finalScore + ". Kamu FAIL.");
            endGame(false, "Time's up! Skor kamu dibawah " + PASS_SCORE_THRESHOLD);
        }
    }

    /**
     * Dipanggil oleh subsistem ketika semua order selesai.
     */
    public void handleAllOrdersCompleted() {
        if (currentState != GameState.PLAYING) return;

        int finalScore = scoreManager.getScore();
        boolean isPass = finalScore >= PASS_SCORE_THRESHOLD;

        if (isPass) {
            System.out.println("[GameController] Semua order selesai! Score " + finalScore + ". Kamu PASS.");
            endGame(true, "Semua order selesai!");
        } else {
            System.out.println("[GameController] Semua order selesai! Score " + finalScore + ". Kamu FAIL.");
            endGame(false, "Skormu tidak cukup!");
        }
    }

    /**
     * Dipanggil oleh OrderManager saat suatu order expired/gagal.
     */
    public void onOrderFailed() {
        scoreManager.recordFail();
        failTracker.recordFail(); // Catat kegagalan beruntun
        
        System.out.println("[GameController] Order failed. Total fails: " + scoreManager.getFailCount());
        
        // Cek apakah kegagalan beruntun sudah mencapai batas
        if (failTracker.isFailThresholdReached()) {
            if (elapsedTime >= SESSION_LIMIT_SECONDS) {
                System.out.println("[GameController] Kondisi FAIL terpenuhi, tapi waktu habis, pindah ke handleSessionTimeUp.");
                return;
            }

            System.out.println("[GameController] Terlalu banyak order yang salah, kamu FAIL.");
            endGame(false, "Terlalu banyak failed orders!");
        }
    }

    /**
     * Dipanggil oleh ServingCounter/order system saat order berhasil di-serve.
     */
    public void onOrderSuccess() {
        scoreManager.recordSuccess();
        failTracker.resetStreak();
        
        System.out.println("[GameController] Order success! Total success: " + scoreManager.getSuccessCount());
        if (orderManager.getTotalSpawnedOrders() >= OrderManager.getMaxTotalOrders() 
            && orderManager.getActiveOrders().isEmpty()) {
            System.out.println("[GameController] Last Order. Selesai game.");
            handleAllOrdersCompleted();
        }
    }

    /**
     * Restart game ke state awal.
     */
    public void restart() {
        System.out.println("[GameController] Restarting game.");
        currentState = GameState.MENU;
        elapsedTime = 0.0;
        lastResult = null;
        startTimeNs = 0L;
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

    public static double getSessionLimitSeconds() {
        return SESSION_LIMIT_SECONDS;
    }
}