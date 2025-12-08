package util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import javazoom.jl.player.Player;

public class MusicPlayer {
    private Player player;
    private Thread playThread;

    public void playLoop(String resourcePath) {
        stop();
        playThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    InputStream in = MusicPlayer.class.getResourceAsStream(resourcePath);
                    if (in == null) {
                        break;
                    }
                    BufferedInputStream bis = new BufferedInputStream(in);
                    player = new Player(bis);
                    player.play();
                }
            } catch (Exception e) {
            }
        });
        playThread.setDaemon(true);
        playThread.start();
    }

    public void stop() {
        if (playThread != null) {
            playThread.interrupt();
            playThread = null;
        }
        if (player != null) {
            try {
                player.close();
            } catch (Exception e) {
            }
            player = null;
        }
    }
}
