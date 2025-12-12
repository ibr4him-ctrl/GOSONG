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

public class UnifiedSettingsDialog extends JDialog {
    private JSlider volumeSlider;
    private JLabel valueLabel;
    private JButton replayButton;
    private JButton exitButton;
    private static MusicPlayer musicPlayer;
    private static int volumeLevel = 50;
    private boolean isInGame; // KEY: bedain main menu vs in-game

    // Constructor 1: Dari MAIN MENU
    public UnifiedSettingsDialog(JFrame owner) {
        this(owner, false); // false = bukan in-game
    }

    // Constructor 2: Dari IN-GAME
    public UnifiedSettingsDialog(JFrame owner, boolean isInGame) {
        super(owner, true);
        this.isInGame = isInGame;
        initComponents();
        setTitle("Settings");
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ===== VOLUME SECTION (ALWAYS SHOWN) =====
        JPanel center = new JPanel(new BorderLayout(10, 10));
        JLabel label = new JLabel("Volume", SwingConstants.CENTER);
        center.add(label, BorderLayout.NORTH);

        volumeSlider = new JSlider(0, 100, volumeLevel);
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        center.add(volumeSlider, BorderLayout.CENTER);

        valueLabel = new JLabel(String.valueOf(volumeLevel), SwingConstants.CENTER);
        center.add(valueLabel, BorderLayout.SOUTH);

        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = volumeSlider.getValue();
                volumeLevel = value;
                valueLabel.setText(String.valueOf(value));
                setVolume(value);
            }
        });

        content.add(center, BorderLayout.CENTER);

        // ===== BUTTON SECTION (ONLY IN-GAME) =====
        if (isInGame) {
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

            replayButton = new JButton("Replay Game");
            replayButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    Main.restartGame();
                }
            });
            buttonPanel.add(replayButton);

            exitButton = new JButton("Exit Game");
            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    Main.stopGame();
                }
            });
            buttonPanel.add(exitButton);

            content.add(buttonPanel, BorderLayout.SOUTH);
            content.setPreferredSize(new Dimension(350, 200));
        } else {
            content.setPreferredSize(new Dimension(300, 150));
        }

        setLayout(new BorderLayout());
        add(content, BorderLayout.CENTER);
    }

    private void setVolume(int volume) {
        if (musicPlayer != null && musicPlayer.getClip() != null) {
            try {
                javax.sound.sampled.FloatControl gainControl = 
                    (javax.sound.sampled.FloatControl) musicPlayer.getClip()
                        .getControl(javax.sound.sampled.FloatControl.Type.MASTER_GAIN);
                
                float range = gainControl.getMaximum() - gainControl.getMinimum();
                float gain = (range * volume / 100.0f) + gainControl.getMinimum();
                gainControl.setValue(gain);
            } catch (Exception e) {
                System.out.println("Volume control not supported: " + e.getMessage());
            }
        }
    }

    public static void setMusicPlayer(MusicPlayer mp) {
        musicPlayer = mp;
    }

    public static int getVolumeLevel() {
        return volumeLevel;
    }
}