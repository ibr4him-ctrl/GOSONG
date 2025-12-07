package model.station;

import java.util.ArrayDeque;
import java.util.Deque;
import model.chef.Chef;
import model.item.Item;
import model.item.utensils.Plate;

public class WashingStation extends Station {
    //plate ktoor yang sudah ditaruh di sink
    private final Deque<Plate> dirtyPlates = new ArrayDeque<>();
    //plate bersih yang sudah dicuci (rak)
    private final Deque<Plate> cleanPlates = new ArrayDeque<>();

    //state 
    private boolean washing = false; 
    private double washProgressSeconds;
    private static final double WASH_TIME = 3.0;

    private Chef currentChef;

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

        //Chef bawa PLATE kotor --> taruh di sink (stack dirty)
        if (hand instanceof Plate plate && !plate.isClean()) {
            dirtyPlates.addLast(plate);
            chef.setHeldItem(null);
            System.out.println("[Washing] Menaruh plate kotor. Total kotor = " + dirtyPlates.size());
            // NOTE: hanya store, belum mulai cuci (cuci pakai V)
            return true;
        }        

        //Chef tangan kosong dan masih aada plate kotor -> mulai / lanjut cuci (V)

        if (hand == null && !dirtyPlates.isEmpty()) {
            //Kalau belum washing, set chef busy n mulai
            if (!washing) {
                washing = true;
                currentChef = chef;
                currentChef.setBusy(true);
                System.out.println("[Washing] Mulai / lanjut mencuci. Progress saat ini = "
                        + washProgressSeconds + " detik");
            }
            return true;
        }

        // 3) Chef tangan kosong, tidak ada plate kotor, tapi ada plate bersih di rak → ambil
        if (hand == null && dirtyPlates.isEmpty() && !cleanPlates.isEmpty()) {
            Plate clean = cleanPlates.pollFirst();
            chef.setHeldItem(clean);
            System.out.println("[Washing] Mengambil plate bersih dari rak. Sisa bersih = "
                    + cleanPlates.size());
            return true;
        }
        System.out.println("[Washing] Interaksi gagal (bukan plate kotor / tidak ada plate bersih).");

        //Inputnya bukan pte, plate berih mau dimasukkan, tidak ada yang bisa dicuci / diambil
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
                //Tidk ada lagi yang dicuci 
                washing = false;
                if (currentChef != null) {
                    currentChef.setBusy(false);
                    currentChef = null;
                }
            }
        }
    }

    public void pauseWashing() {
        washing = false;
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
}