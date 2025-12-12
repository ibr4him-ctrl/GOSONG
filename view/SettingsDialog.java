package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import main.Main;
import util.MusicPlayer;

public class SettingsDialog extends JDialog {
    private JSlider volumeSlider;
    private JLabel volumeLabel;
    private JLabel valueLabel;
    private JButton replayButton;
    private JButton exitButton;
    
    private static MusicPlayer musicPlayer;
    private static int volumeLevel = 50;
    
    private final boolean isInGame;

    public SettingsDialog(JFrame owner) {
        this(owner, false);
    }

    public SettingsDialog(JFrame owner, boolean isInGame) {
        super(owner, "Settings", true);
        this.isInGame = isInGame;
        initComponents();
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initComponents() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel volumePanel = createVolumePanel();
        contentPanel.add(volumePanel, BorderLayout.CENTER);

        if (isInGame) {
            JPanel buttonPanel = createButtonPanel();
            contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        }

        setContentPane(contentPanel);
        int height = isInGame ? 220 : 170;
        contentPanel.setPreferredSize(new Dimension(380, height));
    }

    private JPanel createVolumePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        volumeLabel = new JLabel("Volume", SwingConstants.CENTER);
        volumeLabel.setFont(volumeLabel.getFont().deriveFont(16f));
        panel.add(volumeLabel, BorderLayout.NORTH);

        volumeSlider = new JSlider(0, 100, volumeLevel);
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);

        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = volumeSlider.getValue();
                volumeLevel = value;
                valueLabel.setText(value + "%");
                applyVolume(value);
            }
        });

        panel.add(volumeSlider, BorderLayout.CENTER);

        valueLabel = new JLabel(volumeLevel + "%", SwingConstants.CENTER);
        valueLabel.setFont(valueLabel.getFont().deriveFont(14f));
        panel.add(valueLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));

        replayButton = new JButton("Replay Game");
        replayButton.setPreferredSize(new Dimension(140, 35));
        replayButton.setFocusPainted(false);
        replayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleReplay();
            }
        });

        exitButton = new JButton("Exit Game");
        exitButton.setPreferredSize(new Dimension(140, 35));
        exitButton.setFocusPainted(false);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleExit();
            }
        });

        panel.add(replayButton);
        panel.add(exitButton);

        return panel;
    }

    private void handleReplay() {
        System.out.println("[Settings] Restarting game...");
        dispose();
        Main.restartGame();
    }

    private void handleExit() {
        System.out.println("[Settings] Exiting game...");
        dispose();
        Main.stopGame();
    }

    private void applyVolume(int volume) {
        if (musicPlayer == null || musicPlayer.getClip() == null) {
            return;
        }

        try {
            javax.sound.sampled.FloatControl gainControl = 
                (javax.sound.sampled.FloatControl) musicPlayer.getClip()
                    .getControl(javax.sound.sampled.FloatControl.Type.MASTER_GAIN);

            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain = (range * volume / 100.0f) + gainControl.getMinimum();

            gainControl.setValue(gain);

            System.out.println("[Settings] Volume set to: " + volume + "% (Gain: " + gain + " dB)");

        } catch (IllegalArgumentException e) {
            System.err.println("[Settings] Volume control not supported: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[Settings] Error setting volume: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void setMusicPlayer(MusicPlayer mp) {
        musicPlayer = mp;
        System.out.println("[Settings] Music player registered");
    }

    public static int getVolumeLevel() {
        return volumeLevel;
    }

    public static void setVolumeLevel(int level) {
        if (level < 0) level = 0;
        if (level > 100) level = 100;
        volumeLevel = level;
        System.out.println("[Settings] Volume level set to: " + level);
    }
}
