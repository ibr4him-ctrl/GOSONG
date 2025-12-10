package main;
import javax.swing.*;
import view.GamePanel;
import view.MainMenu;
import util.MusicPlayer;

public class Main {

    private static GamePanel gamePanel;
    private static JFrame window;
    private static MusicPlayer musicPlayer = new MusicPlayer();

    private static int score = 0;
    private static JLabel scoreLabel;
    private static JTextArea orderLog;

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
        if (window != null) {
            window.setVisible(false);
            window.dispose();
        }
        musicPlayer.stop();
        
        SwingUtilities.invokeLater(() -> {
            view.GameOver gameOver = new view.GameOver();
            gameOver.setVisible(true);
        });
    }


    public static void restartGame() {
        if (window != null) {
            window.setVisible(false);
            window.dispose();
        }
        musicPlayer.stop();
        model.manager.OrderManager.resetInstance();
        model.manager.ScoreManager.getInstance().reset();
        model.item.dish.Order.resetOrderCounter();
        
        SwingUtilities.invokeLater(() -> {
            startGame();
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
}
