package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import util.MusicPlayer;

public class StageCleared extends JFrame {
    private static final int WIDTH = 16 * 16 * 3;
    private static final int HEIGHT = 12 * 16 * 3;

    private Image backgroundImage;
    private MusicPlayer musicPlayer;

    public StageCleared() {
        loadContent();
        initComponents();

        setTitle("Stage Cleared!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);

        // Mainkan musik kemenangan
        musicPlayer = new MusicPlayer();
        musicPlayer.playLoop("/resources/game/music/GameWin.wav");
    }

    private void loadContent() {
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/resources/game/StageCleared.png"));
        } catch (IOException e) {
            System.err.println("Error loading StageCleared.png: " + e.getMessage());
            backgroundImage = null;
        }
    }

    private void initComponents() {
        ClearedPanel panel = new ClearedPanel();
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setContentPane(panel);
    }

    private void onMainMenuClicked() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
        this.dispose();
        main.Main.showMainMenu();
    }

    private class ClearedPanel extends JComponent {
        // Area klik untuk tombol "Main Menu"
        private static final int MENU_X = 300;
        private static final int MENU_Y = 480;
        private static final int MENU_W = 180;
        private static final int MENU_H = 70;

        public ClearedPanel() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int x = e.getX();
                    int y = e.getY();
                    if (x >= MENU_X && x <= MENU_X + MENU_W && y >= MENU_Y && y <= MENU_Y + MENU_H) {
                        onMainMenuClicked();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(Color.BLUE);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.WHITE);
                g.drawString("Stage Cleared!", 100, 100);
            }
        }
    }
}