package model.station;

import java.util.ArrayDeque;
import java.util.Deque;
import model.chef.Chef;
import model.enums.ItemType;
import model.item.Item;
import util.SoundEffectPlayer;

public class PlateStorage extends Station {

    // Satu-satunya PlateStorage di map
    private static PlateStorage instance;

    private final Deque<Item> platestack;
    // ===== SFX =====
    private static final SoundEffectPlayer SFX = new SoundEffectPlayer();
    private static final String SFX_PLATE =
            "/resources/game/sound_effect/putting_plates.wav";
    public PlateStorage(int x, int y) {
        super(x, y, "PlateStorage");
        this.platestack = new ArrayDeque<>();
        instance = this; // daftarkan diri sebagai instance global
    }

    public static PlateStorage getInstance() {
        return instance;
    }

    @Override
    public String getSymbol() {
        return "P";
    }

    public synchronized void pushInitialCleanPlate(Item plate) {
        if (plate == null) return;
        if (plate.getItemType() != ItemType.PLATE) return;
        plate.setClean(true);
        platestack.push(plate);
    }

    public synchronized void pushDirtyPlate(Item plate) {
        if (plate == null) return;
        if (plate.getItemType() != ItemType.PLATE) return;
        plate.setClean(false);
        platestack.push(plate);
    }

    @Override
    public synchronized boolean interact(Chef chef) {
        if (!isAdjacentTo(chef)) return false;

        Item hand = chef.getHeldItem();

        if (chef.getHeldItem() != null) {
            return false;
        }
        // ============================
        // 1. Chef lagi megang sesuatu
        // ============================
        if (hand != null) {
            // cuma boleh naro plate
            if (hand.getItemType() != ItemType.PLATE) {
                return false;
            }

            // plate kotor nggak boleh dimasukin manual ke PlateStorage
            if (!hand.isClean()) {
                System.out.println("[PlateStorage] Plate kotor gak boleh langsung disimpan di PlateStorage.");
                return false;
            }

            // plate bersih → taruh di atas stack
            platestack.push(hand);
            chef.setHeldItem(null);
            System.out.println("[PlateStorage] Plate bersih dikembalikan ke PlateStorage (top of stack).");
            return true;
        }

        // ============================
        // 2. Tangan kosong → ambil plate
        // ============================
        if (platestack.isEmpty()) return false;

        // ambil plate paling atas (bisa clean / dirty, tergantung kondisi stack)
        Item top = platestack.pop();
        chef.setHeldItem(top);
        SFX.playOnce(SFX_PLATE);
        System.out.println("[PlateStorage] Chef mengambil plate dari PlateStorage ("
                + (top.isClean() ? "clean" : "dirty") + ").");
        return true;
    }

    public synchronized int getPlateCount() {
        return platestack.size();
    }
}
