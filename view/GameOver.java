package view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import util.MusicPlayer;

public class GameOver extends JFrame {
   private static final int WIDTH = 16 * 16 * 3;
   private static final int HEIGHT = 12 * 16 * 3;

   private Image backgroundImage;
   private MusicPlayer musicPlayer;

   public GameOver() {
      loadBackground();
      initComponents();
      setTitle("Game Over");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setResizable(false);
      pack();
      setLocationRelativeTo(null);

      musicPlayer = new MusicPlayer();
      musicPlayer.playLoop("/resources/game/music/GameOverMusic.wav");

      addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            if (musicPlayer != null) {
               musicPlayer.stop();
            }
         }
      });
   }

   private void loadBackground() {
      try {
         backgroundImage = ImageIO.read(getClass().getResource("/resources/game/GameOver.png"));
      } catch (IOException e) {
         backgroundImage = null;
      }
   }

   private void initComponents() {
      GameOverPanel panel = new GameOverPanel();
      panel.setPreferredSize(new java.awt.Dimension(WIDTH, HEIGHT));
      setContentPane(panel);
   }

   private class GameOverPanel extends JComponent {
      @Override
      protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2d.dispose();
         }
      }
   }
}
