package main;
import javax.swing.*;
import view.GamePanel;
import view.MainMenu;
import view.GameSummary;
import view.StageCleared;
import util.MusicPlayer;

public class Main {

    private static GamePanel gamePanel;
    private static JFrame window;
    private static MusicPlayer musicPlayer = new MusicPlayer();

    private static int score = 0;
    private static JLabel scoreLabel;
    private static JTextArea orderLog;
    
    // Static reference ke GameResult untuk akses dari GameOver UI
    private static model.manager.GameResult lastGameResult = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainMenu mainMenu = new MainMenu();
                mainMenu.setVisible(true);
            }
        });
    }

    public static void startGame() {
        // Pastikan semua singleton direset sebelum memulai game baru
        model.manager.GameController.resetInstance();
        model.manager.OrderManager.resetInstance();
        model.manager.ScoreManager.getInstance().resetScore();
        model.manager.OrderFailTracker.resetInstance();
        model.item.dish.Order.resetOrderCounter();
        
        window = new JFrame("GOSONG - Pizza Chef");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setLayout(new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));

        JPanel scorePanel = new JPanel();
        scoreLabel = new JLabel("Score: 0");
        scorePanel.add(scoreLabel);
        window.add(scorePanel);
        gamePanel = new GamePanel();
        window.add(gamePanel);

        orderLog = new JTextArea(5, 40);
        orderLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(orderLog);
        window.add(scrollPane);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.startGameThread();

        musicPlayer.playLoop("/resources/game/music/GameMusic.wav");
    }

    public static void addScore(int delta) {
        score += delta;
        SwingUtilities.invokeLater(() ->
                scoreLabel.setText("Score: " + score));
    }

    public static void logOrder(String message) {
        SwingUtilities.invokeLater(() -> {
            orderLog.append(message + "\n");
            orderLog.setCaretPosition(orderLog.getDocument().getLength());
        });
    }

    public static void showGameOver() {
        // Stop game thread terlebih dahulu
        if (gamePanel != null) {
            try {
                gamePanel.stopGameThread();
            } catch (Exception e) {
                System.err.println("Error stopping game thread: " + e.getMessage());
            }
        }
        
        if (window != null) {
            if (gamePanel != null) {
                gamePanel.stopGameThread();
            }
            window.setVisible(false);
            window.dispose();
        }
        musicPlayer.stop();
        
        SwingUtilities.invokeLater(() -> {
            view.GameOver gameOver = new view.GameOver(lastGameResult);
            gameOver.setVisible(true);
        });
    }

    /**
     * Overload: showGameOver dengan GameResult.
     * Disebut dari GameController saat game berakhir.
     */
    public static void showGameOver(model.manager.GameResult result) {
        lastGameResult = result;
        showGameOver();
    }

    /**
     * Menampilkan layar Game Summary (saat menang).
     */
    public static void showGameSummary(model.manager.GameResult result) {
        lastGameResult = result;

        // Stop game thread terlebih dahulu
        if (gamePanel != null) {
            try {
                gamePanel.stopGameThread();
            } catch (Exception e) {
                System.err.println("Error stopping game thread: " + e.getMessage());
            }
        }
        
        if (window != null) {
            window.setVisible(false);
            window.dispose();
        }
        musicPlayer.stop();
        
        SwingUtilities.invokeLater(() -> {
            GameSummary summary = new GameSummary(lastGameResult);
            summary.setVisible(true);
        });
    }

    /**
     * Menampilkan layar Stage Cleared.
     */
    public static void showStageCleared() {
        // Tidak perlu stop apa-apa karena sudah di-handle oleh GameSummary
        
        SwingUtilities.invokeLater(() -> {
            StageCleared cleared = new StageCleared();
            cleared.setVisible(true);
        });
    }


    public static void restartGame() {
        // Stop game thread dan window terlebih dahulu
        if (gamePanel != null) {
            try {
                gamePanel.stopGameThread();
            } catch (Exception e) {
                System.err.println("Error stopping game thread: " + e.getMessage());
            }
        }
        if (window != null) {
            if (gamePanel != null) {
                gamePanel.stopGameThread();
            }
            window.setVisible(false);
            window.dispose();
        }
        musicPlayer.stop();
        
        // Reset semua singleton dan state
        model.manager.GameController.resetInstance();
        model.manager.OrderManager.resetInstance();
        model.manager.ScoreManager.getInstance().resetScore();
        model.manager.OrderFailTracker.resetInstance();
        model.item.dish.Order.resetOrderCounter();
        
        // Reset UI score dan lastGameResult
        score = 0;
        lastGameResult = null;
        
        // Beri sedikit delay sebelum start game baru untuk memastikan cleanup selesai
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            startGame();
        });
    }

    public static void showMainMenu() {
        if (window != null) {
            window.setVisible(false);
            window.dispose();
        }
        musicPlayer.stop();

        SwingUtilities.invokeLater(() -> {
            MainMenu mainMenu = new MainMenu();
            mainMenu.setVisible(true);
        });
    }

    public static void stopGame() {
        model.manager.OrderManager.getInstance().stopAcceptingNewOrders();
        musicPlayer.stop();
        System.exit(0);
    }

    public static MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }

    public static model.manager.GameResult getLastGameResult() {
        return lastGameResult;
    }
}