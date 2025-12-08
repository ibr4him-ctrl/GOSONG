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

    // ====== Helper untuk masa depan ScoreManager ======

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

    /**
     * Dipanggil saat Stage Over kalau kamu mau tutup game.
     * Game berhenti menerima order baru.
     */
    public static void stopGame() {
        model.manager.OrderManager.getInstance().stopAcceptingNewOrders();
        musicPlayer.stop();
        System.exit(0);
    }
}
