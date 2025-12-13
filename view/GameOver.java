package view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import util.MusicPlayer;
import model.manager.GameResult;

public class GameOver extends JFrame {
   private static final int WIDTH = 16 * 16 * 3;
   private static final int HEIGHT = 12 * 16 * 3;

   private Image backgroundImage;
   private MusicPlayer musicPlayer;
   private GameResult gameResult;

   /**
    * Constructor dengan GameResult.
    * Digunakan saat GameController trigger game over.
    */
   public GameOver(GameResult result) {
      this.gameResult = result;
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

   /**
    * Constructor default (backward compatibility).
    * Jika dipanggil tanpa parameter.
    */
   public GameOver() {
      this(null);
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

   private void onRetryClicked() {
      if (musicPlayer != null) {
         musicPlayer.stop();
      }
      this.dispose();
      main.Main.restartGame();
   }

   private void onExitClicked() {
      if (musicPlayer != null) {
         musicPlayer.stop();
      }
      System.exit(0);
   }

   private void onMainMenuClicked() {
      if (musicPlayer != null) {
         musicPlayer.stop();
      }
      this.dispose();
      main.Main.showMainMenu();
   }

   private class GameOverPanel extends JComponent {
      // Region buttons
      private static final int RETRY_X = 200;
      private static final int RETRY_Y = 480;
      private static final int RETRY_W = 150;
      private static final int RETRY_H = 60;

      private static final int MAIN_MENU_X = 385;
      private static final int MAIN_MENU_Y = 480;
      private static final int MAIN_MENU_W = 150;
      private static final int MAIN_MENU_H = 60;

      private static final int EXIT_X = 570;
      private static final int EXIT_Y = 480;
      private static final int EXIT_W = 150;
      private static final int EXIT_H = 60;

      // Variable timer untuk mencegah close tidak sengaja
      private long openTime;

      public GameOverPanel() {
         // Catat waktu saat panel dibuat
         this.openTime = System.currentTimeMillis();

         addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
               // --- LOGIKA PENGAMAN (COOLDOWN) ---
               // Abaikan klik mouse di 1 detik pertama.
               // Ini mencegah window langsung tertutup sendiri saat baru muncul.
               if (System.currentTimeMillis() - openTime < 1000) {
                   return; 
               }
               onPanelClicked(e.getX(), e.getY());
            }
         });
      }

      private void onPanelClicked(int x, int y) {
         if (x >= RETRY_X && x <= RETRY_X + RETRY_W &&
             y >= RETRY_Y && y <= RETRY_Y + RETRY_H) {
            GameOver.this.onRetryClicked();
            return;
         }

         if (x >= MAIN_MENU_X && x <= MAIN_MENU_X + MAIN_MENU_W &&
             y >= MAIN_MENU_Y && y <= MAIN_MENU_Y + MAIN_MENU_H) {
            GameOver.this.onMainMenuClicked();
            return;
         }

         if (x >= EXIT_X && x <= EXIT_X + EXIT_W &&
             y >= EXIT_Y && y <= EXIT_Y + EXIT_H) {
            GameOver.this.onExitClicked();
            return;
         }
      }

      @Override
      protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         Graphics2D g2d = (Graphics2D) g.create();

         // Draw background image
         if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
         } else {
            // Fallback: Jika background image gagal load, gambar warna hitam.
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
         }

         // Draw game result info
         if (gameResult != null) {
            drawGameResultInfo(g2d);
         }
         
         g2d.dispose();
      }

      private void drawGameResultInfo(Graphics2D g2d) {
         int centerX = getWidth() / 2;
         int startY = 180;
         int lineHeight = 48;
         java.awt.FontMetrics fm;

         // Final Score
         g2d.setColor(Color.YELLOW);
         g2d.setFont(new Font("Lucida Console", Font.BOLD, 32));
         String scoreText = "Score: " + gameResult.getFinalScore();
         fm = g2d.getFontMetrics();
         g2d.drawString(scoreText, centerX - fm.stringWidth(scoreText) / 2, startY);

         // Elapsed Time
         g2d.setColor(Color.CYAN);
         g2d.setFont(new Font("Lucida Console", Font.PLAIN, 24));
         String timeText = String.format("Time: %.1f / 240.0 sec", gameResult.getElapsedTime());
         fm = g2d.getFontMetrics();
         g2d.drawString(timeText, centerX - fm.stringWidth(timeText) / 2, startY + lineHeight);

         // Fail Reason
         if (gameResult.getFailReason() != null && !gameResult.getFailReason().isEmpty()) {
            g2d.setColor(new Color(255, 200, 0));
            g2d.setFont(new Font("Lucida Console", Font.ITALIC, 18));
            String reasonText = gameResult.getFailReason();
            fm = g2d.getFontMetrics();
            g2d.drawString(reasonText, centerX - fm.stringWidth(reasonText) / 2, startY + lineHeight + 70);
         }

         // Order Success/Fail
         int success = gameResult.getOrderSuccessCount();
         int fail = gameResult.getOrderFailCount();
         String orderText = String.format("Order Success: %d  Order Fail: %d", success, fail);
         g2d.setFont(new Font("Lucida Console", Font.PLAIN, 22));
         g2d.setColor(Color.WHITE);
         fm = g2d.getFontMetrics();
         g2d.drawString(orderText, centerX - fm.stringWidth(orderText) / 2, startY + lineHeight + 40);
      }
   }
}