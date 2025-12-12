package view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame; 
import java.awt.Dimension;
import main.Main;
import view.Pengaturan;

public class MainMenu extends JFrame {
   private Image backgroundImage;
   private JButton mulaiButton;
   private JButton pengaturanButton;
   private static final int WIDTH = 16 * 16 * 3;  
   private static final int HEIGHT = 12 * 16 * 3; 

   public MainMenu() {
      loadBackground();
      initComponents();
      setTitle("GOSONG");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setResizable(false);
      pack();
      setLocationRelativeTo(null);
   }

   private void loadBackground() {
      try {
         backgroundImage = ImageIO.read(getClass().getResource("/resources/game/MainMenu.png"));
      } catch (IOException e) {
         backgroundImage = null;
         e.printStackTrace();
      }
   }

   private void initComponents() {
      BackgroundPanel backgroundPanel = new BackgroundPanel();
      backgroundPanel.setLayout(null);
      
      backgroundPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

      ImageIcon mulaiIconOriginal = new ImageIcon(getClass().getResource("/resources/game/Mulai.png"));
      int targetButtonWidth = WIDTH / 5;
      int targetButtonHeight = (int) (targetButtonWidth * ((double) mulaiIconOriginal.getIconHeight() / mulaiIconOriginal.getIconWidth()));
      Image mulaiScaled = mulaiIconOriginal.getImage().getScaledInstance(targetButtonWidth, targetButtonHeight, Image.SCALE_SMOOTH);
      ImageIcon mulaiIcon = new ImageIcon(mulaiScaled);
      mulaiButton = new JButton(mulaiIcon);
      mulaiButton.setBorderPainted(false);
      mulaiButton.setContentAreaFilled(false);
      mulaiButton.setOpaque(false);
      mulaiButton.setFocusPainted(false);

      ImageIcon pengaturanIconOriginal = new ImageIcon(getClass().getResource("/resources/game/Pengaturan.png"));
      int pengaturanTargetWidth = targetButtonWidth;
      int pengaturanTargetHeight = (int) (pengaturanTargetWidth * ((double) pengaturanIconOriginal.getIconHeight() / pengaturanIconOriginal.getIconWidth()));
      Image pengaturanScaled = pengaturanIconOriginal.getImage().getScaledInstance(pengaturanTargetWidth, pengaturanTargetHeight, Image.SCALE_SMOOTH);
      ImageIcon pengaturanIcon = new ImageIcon(pengaturanScaled);
      pengaturanButton = new JButton(pengaturanIcon);
      pengaturanButton.setBorderPainted(false);
      pengaturanButton.setContentAreaFilled(false);
      pengaturanButton.setOpaque(false);
      pengaturanButton.setFocusPainted(false);

      int buttonWidthMulai = mulaiIcon.getIconWidth();
      int buttonHeightMulai = mulaiIcon.getIconHeight();
      int buttonWidthPengaturan = pengaturanIcon.getIconWidth();
      int buttonHeightPengaturan = pengaturanIcon.getIconHeight();

      // Posisi Mulai
      int mulaiX = (WIDTH - buttonWidthMulai) / 2; 
      int mulaiY = (int) (HEIGHT * 0.25); 
      
      // Posisi Pengaturan
      int pengaturanX = (WIDTH - buttonWidthPengaturan) / 2; 
      int pengaturanY = mulaiY + buttonHeightMulai + 20; 

      mulaiButton.setBounds(mulaiX, mulaiY, buttonWidthMulai, buttonHeightMulai);
      pengaturanButton.setBounds(pengaturanX, pengaturanY, buttonWidthPengaturan, buttonHeightPengaturan);

      backgroundPanel.add(mulaiButton);
      backgroundPanel.add(pengaturanButton);

      mulaiButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            mulaiGame();
         }
      });

      pengaturanButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            bukaPengaturan();
         }
      });

      setContentPane(backgroundPanel);
   }

   private void mulaiGame() {
      setVisible(false);
      Main.startGame();
   }

private void bukaPengaturan() {
    UnifiedSettingsDialog settings = new UnifiedSettingsDialog(this, false); // false = main menu
    settings.setVisible(true);
}

   private class BackgroundPanel extends JComponent {
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