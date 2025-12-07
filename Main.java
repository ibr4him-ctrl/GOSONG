import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import view.GamePanel;

public class Main {

    private static int score = 0;
    private static JLabel scoreLabel;

    public static void main(String[] args) {

        JFrame window = new JFrame("GOSONG - Pizza Chef");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setLayout(new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));

        // Panel skor di atas
        JPanel scorePanel = new JPanel();
        scoreLabel = new JLabel("Score: 0");
        scorePanel.add(scoreLabel);
        window.add(scorePanel);

        // Game panel
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.startGameThread();
    }

    // Nanti kalau mau, bisa dipanggil dari mana pun di game untuk ubah skor
    public static void addScore(int delta) {
        score += delta;
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + score);
        }
    }
}
