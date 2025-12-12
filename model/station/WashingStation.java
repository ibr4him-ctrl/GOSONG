package model.station;

import java.util.ArrayDeque;
import java.util.Deque;
import model.chef.Chef;
import model.item.Item;
import model.item.utensils.Plate;
import util.SoundEffectPlayer;

public class WashingStation extends Station {
    // plate kotor yang sudah ditaruh di sink
    private final Deque<Plate> dirtyPlates = new ArrayDeque<>();
    // plate bersih yang sudah dicuci (rak)
    private final Deque<Plate> cleanPlates = new ArrayDeque<>();

    // state
    private boolean washing = false;
    private double washProgressSeconds;
    private static final double WASH_TIME = 3.0;

    private Chef currentChef;

    // ===== SFX =====
    private static final SoundEffectPlayer SFX = new SoundEffectPlayer();
    private static final String SFX_WASHING =
            "/resources/game/sound_effect/washing_dishes.wav";
    private static final String SFX_PUT_PLATE =
        "/resources/game/sound_effect/putting_plates.wav";
    private final String washingLoopKey = "WASHING_" + System.identityHashCode(this);

    private void startWashingSound() {
        SFX.playLoop(washingLoopKey, SFX_WASHING);
    }

    private void stopWashingSound() {
        SFX.stopLoop(washingLoopKey);
    }

    public WashingStation(int x, int y) {
        super(x, y, "Washing");
    }

    @Override
    public String getSymbol() {
        return "W";
    }

    @Override
    public boolean interact(Chef chef) {
        Item hand = chef.getHeldItem();

        // 1) Kalau tangan TIDAK kosong → V TIDAK dipakai buat naruh plate.
        if (hand != null) {
            System.out.println("[Washing] (V) Gunakan tombol C untuk menaruh/mengambil plate. " +
                    "V hanya untuk MEMULAI / MELANJUTKAN cuci.");
            return false;
        }

        // 2) Tangan kosong dan masih ada plate kotor → mulai / lanjut cuci
        if (!dirtyPlates.isEmpty()) {
            if (!washing) {
                washing = true;
                currentChef = chef;
                currentChef.setBusy(true);

                startWashingSound(); // start loop SFX

                System.out.println("[Washing] (V) Mulai / lanjut mencuci. Progress saat ini = "
                        + washProgressSeconds + " detik");
            }
            return true;
        }

        // 3) Tidak ada plate kotor → tidak ada yang bisa dicuci
        System.out.println("[Washing] (V) Tidak ada plate kotor di sink. " +
                "Taruh plate kotor dulu dengan tombol C.");
        return false;
    }

    public void update(double deltaSeconds) {
        if (!washing || dirtyPlates.isEmpty()) return;

        // Kalau chef sudah tidak di dekat sink → pause washing (progress disimpan)
        if (currentChef == null || !isAdjacentTo(currentChef)) {
            pauseWashing();
            return;
        }

        washProgressSeconds += deltaSeconds;

        if (washProgressSeconds >= WASH_TIME) {
            Plate plate = dirtyPlates.pollFirst();
            plate.setClean(true);
            cleanPlates.addLast(plate);
            System.out.println("[Washing] Selesai mencuci 1 plate. Sisa kotor = " + dirtyPlates.size());

            washProgressSeconds = 0.0;

            if (dirtyPlates.isEmpty()) {
                // Tidak ada lagi yang dicuci
                washing = false;

                stopWashingSound(); // stop loop SFX

                if (currentChef != null) {
                    currentChef.setBusy(false);
                    currentChef = null;
                }
            }
        }
    }

    public void pauseWashing() {
        washing = false;

        stopWashingSound(); // stop loop SFX

        if (currentChef != null) {
            currentChef.setBusy(false);
            currentChef = null;
        }
        // debug: washProgressSeconds TIDAK di-reset → lanjut lagi nanti
        System.out.println("[Washing] Proses mencuci dipause. Progress tersimpan = "
                + washProgressSeconds + " detik");
    }

    public double getProgressRatio() {
        if (WASH_TIME <= 0) return 0.0;
        return Math.min(1.0, washProgressSeconds / WASH_TIME);
    }

    public int getProgressPercent() {
        return (int) Math.round(getProgressRatio() * 100.0);
    }

    public String getProgressBar(int width) {
        double ratio = getProgressRatio();
        int filled = (int) Math.round(ratio * width);
        if (filled > width) filled = width;

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < width; i++) {
            sb.append(i < filled ? '#' : '-');
        }
        sb.append("] ").append(getProgressPercent()).append('%');
        return sb.toString();
    }

    public boolean isWashing() {
        return washing;
    }

    public double getWashProgressSeconds() {
        return washProgressSeconds;
    }

    public boolean handlePickUpDrop(Chef chef) {
        Item hand = chef.getHeldItem();

        // 1) Chef bawa plate kotor → store ke dirtyPlates
        if (hand instanceof Plate plate && !plate.isClean()) {
            dirtyPlates.addLast(plate);
            chef.setHeldItem(null);
            SFX.playOnce(SFX_PUT_PLATE); 
            System.out.println("[Washing] (C) Menaruh plate kotor ke sink. Total kotor = " + dirtyPlates.size());
            return true;
        }

        // 2) Chef tangan kosong, ada plate bersih di rak → ambil
        if (hand == null && !cleanPlates.isEmpty()) {
            Plate clean = cleanPlates.pollFirst();
            chef.setHeldItem(clean);
            SFX.playOnce(SFX_PUT_PLATE);
            System.out.println("[Washing] (C) Mengambil plate bersih dari rak. Sisa bersih = " + cleanPlates.size());
            return true;
        }

        // 3) Kasus lain → tidak valid
        System.out.println("[Washing] (C) Tidak ada aksi valid. " +
                "Butuh plate kotor di tangan atau plate bersih di rak.");
        return false;
    }
}
