package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import model.manager.GameResult;
import util.MusicPlayer;

public class GameSummary extends JFrame {
    private static final int WIDTH = 16 * 16 * 3;
    private static final int HEIGHT = 12 * 16 * 3;

    private Image backgroundImage;
    private GameResult gameResult;
    private MusicPlayer musicPlayer;

    public GameSummary(GameResult result) {
        this.gameResult = result;
        loadContent();
        initComponents();

        setTitle("Game Summary");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);

        // Mainkan musik yang sama dengan Game Over
        musicPlayer = new MusicPlayer();
        musicPlayer.playLoop("/resources/game/music/GameOverMusic.wav");
    }

    private void loadContent() {
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/resources/game/GameSummary.png"));
        } catch (IOException e) {
            System.err.println("Error loading GameSummary.png: " + e.getMessage());
            backgroundImage = null;
        }
    }

    private void initComponents() {
        SummaryPanel panel = new SummaryPanel();
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setContentPane(panel);
    }

    private void onNextClicked() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
        this.dispose();
        main.Main.showStageCleared();
    }

    private class SummaryPanel extends JComponent {
        // Area klik untuk tombol "Selanjutnya"
        private static final int NEXT_X = 300;
        private static final int NEXT_Y = 480;
        private static final int NEXT_W = 180;
        private static final int NEXT_H = 70;

        public SummaryPanel() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int x = e.getX();
                    int y = e.getY();
                    if (x >= NEXT_X && x <= NEXT_X + NEXT_W && y >= NEXT_Y && y <= NEXT_Y + NEXT_H) {
                        onNextClicked();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();

            if (backgroundImage != null) {
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g2d.setColor(Color.DARK_GRAY);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(Color.WHITE);
                g2d.drawString("Game Summary", 100, 100);
            }

            if (gameResult != null) {
                drawGameResultInfo(g2d);
            }

            g2d.dispose();
        }

        private void drawGameResultInfo(Graphics2D g2d) {
            int centerX = getWidth() / 2;
            int startY = 200;
            int lineHeight = 55;
            java.awt.FontMetrics fm;

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Lucida Console", Font.BOLD, 36));

            // Final Score
            String scoreText = "Score: " + gameResult.getFinalScore();
            fm = g2d.getFontMetrics();
            g2d.drawString(scoreText, centerX - fm.stringWidth(scoreText) / 2, startY);

            // Elapsed Time
            String timeText = String.format("Time: %.1f s", gameResult.getElapsedTime());
            fm = g2d.getFontMetrics();
            g2d.drawString(timeText, centerX - fm.stringWidth(timeText) / 2, startY + lineHeight);

            // Order Success
            String successText = "Order Success: " + gameResult.getOrderSuccessCount();
            fm = g2d.getFontMetrics();
            g2d.drawString(successText, centerX - fm.stringWidth(successText) / 2, startY + lineHeight * 2);

            // Order Fail
            String failText = "Order Fail: " + gameResult.getOrderFailCount();
            fm = g2d.getFontMetrics();
            g2d.drawString(failText, centerX - fm.stringWidth(failText) / 2, startY + lineHeight * 3);
        }
    }
}