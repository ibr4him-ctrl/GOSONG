package util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.*;

public class SoundEffectPlayer {

    private final Map<String, Clip> loopClips = new HashMap<>();

    // untuk SFX sekali bunyi
    public void playOnce(String resourcePath) {
        try {
            URL url = SoundEffectPlayer.class.getResource(resourcePath);
            if (url == null) {
                System.out.println("SFX file not found: " + resourcePath);
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    try { ais.close(); } catch (Exception ignored) {}
                }
            });

            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // untuk SFX loop (mis: oven_working)
    public synchronized void playLoop(String key, String resourcePath) {
        Clip existing = loopClips.get(key);
        if (existing != null && existing.isOpen() && existing.isRunning()) return;

        stopLoop(key);

        try {
            URL url = SoundEffectPlayer.class.getResource(resourcePath);
            if (url == null) {
                System.out.println("Loop SFX file not found: " + resourcePath);
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);

            
            try { ais.close(); } catch (Exception ignored) {}

            clip.loop(Clip.LOOP_CONTINUOUSLY);

            loopClips.put(key, clip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void stopLoop(String key) {
        Clip clip = loopClips.remove(key);
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}
