package model.manager;

/**
 * Data holder untuk membawa hasil akhir game dari GameController ke UI GameOver.
 * Immutable object yang berisi final score, elapsed time, dan status PASS/FAIL.
 */
public class GameResult {
    private final int finalScore;
    private final double elapsedTime;
    private final boolean isPass;
    private final String failReason; // optional: alasan FAIL jika ada
    private int orderSuccessCount;
    private int orderFailCount;

    public GameResult(int finalScore, double elapsedTime, boolean isPass) {
        this(finalScore, elapsedTime, isPass, null, 0, 0);
    }

    public GameResult(int finalScore, double elapsedTime, boolean isPass, String failReason, int successCount, int failCount) {
        this.finalScore = finalScore;
        this.elapsedTime = elapsedTime;
        this.isPass = isPass;
        this.failReason = failReason;
        this.orderSuccessCount = successCount;
        this.orderFailCount = failCount;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public boolean isPass() {
        return isPass;
    }

    public String getFailReason() {
        return failReason;
    }

    public int getOrderSuccessCount() {
        return orderSuccessCount;
    }

    public int getOrderFailCount() {
        return orderFailCount;
    }
    @Override
    public String toString() {
            return String.format("GameResult(score=%d, time=%.1fs, success=%d, fail=%d, %s%s)",
                finalScore,
                elapsedTime,
                orderSuccessCount, orderFailCount,
                isPass ? "PASS" : "FAIL",
                failReason != null ? " - " + failReason : "");
    }
}
