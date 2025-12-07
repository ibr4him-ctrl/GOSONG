package model.station;

import java.util.Timer;
import java.util.TimerTask;
import model.chef.Chef;
import model.item.Dish;
import model.item.Item;
import model.item.dish.Order;
import model.item.utensils.Plate;
import model.manager.OrderManager;

public class ServingCounter extends Station {

    private static final long PLATE_RETURN_DELAY_MS = 10_000L; // 10 detik

    public ServingCounter(int x, int y) {
        super(x, y, "Serving");
    }

    @Override
    public String getSymbol() {
        return "S";
    }

    
    @Override
    public boolean interact(Chef chef) {

        // DEBUG: cek dulu apa yang lagi dipegang & ada di station
        Item hand = chef.getHeldItem();
        Item onStationBefore = getItemOnStation();
        System.out.println("[Serving] HAND = " +
                (hand == null ? "null" : hand.getName() + " (" + hand.getClass().getSimpleName() + ")"));
        System.out.println("[Serving] ON STATION = " +
                (onStationBefore == null ? "null" : onStationBefore.getName() + " (" + onStationBefore.getClass().getSimpleName() + ")"));

        // 1. Cari plate dengan isi: prioritas dari tangan, kalau ga ada baru dari station
        Plate plate = null;
        boolean cameFromHand = false;

        if (hand instanceof Plate p) {
            System.out.println("[Serving] Plate di tangan, contents size = " + p.getContents().size());
            if (!p.isEmpty()) { // isEmpty() = contents.isEmpty()
                plate = p;
                cameFromHand = true;
                System.out.println("[Serving] → pakai plate dari TANGAN");
            } else {
                System.out.println("[Serving] Plate di tangan tapi kosong menurut kode.");
            }
        }

        if (plate == null && onStationBefore instanceof Plate p2) {
            System.out.println("[Serving] Plate di station, contents size = " + p2.getContents().size());
            if (!p2.isEmpty()) {
                plate = p2;
                cameFromHand = false;
                System.out.println("[Serving] → pakai plate dari STATION");
                setItemOnStation(null); // anggap diambil
            } else {
                System.out.println("[Serving] Plate di station tapi kosong menurut kode.");
            }
        }

        if (plate == null) {
            System.out.println("[Serving] Tidak menemukan plate berisi apa pun (tangan/station).");
            return false;
        }

        Dish dish = plate.getDish();
        System.out.println("[Serving] getDish() = " + dish);

        if (dish == null) {
            System.out.println("[Serving] Plate tidak berisi Dish final (mungkin cuma bahan mentah).");
            return false;
        }

        System.out.println("[Serving] Dish type: " + dish.getPizzaType());

        // 2. Validasi ke OrderManager
        Order matchedOrder = OrderManager.getInstance().validateDish(dish);

        if (matchedOrder != null) {
            System.out.println("✅ SUKSES: " + matchedOrder.getPizzaType().getDisplayName());
            System.out.println("💰 Reward: +" + matchedOrder.getReward());
        } else {
            System.out.println("❌ GAGAL: Tidak ada pesanan untuk "
                    + dish.getPizzaType().getDisplayName());
            System.out.println("Penalti -50 (contoh)");
        }

        // 3. Bersihkan plate & balikin ke storage
        plate.removeDish();
        plate.markDirty();

        if (cameFromHand) {
            chef.setHeldItem(null);
        }
        returnToPlateStorage(plate);

        return true;
    }


    /**
     * Mengembalikan plate ke PlateStorage terdekat 10 detik setelah serve,
     * dalam kondisi kotor (sesuai kitchen loop & serving action).
     */
    private void returnToPlateStorage(Plate dirtyPlate) {
        PlateStorage target = PlateStorage.getInstance();
        if (target == null) {
            System.out.println("WARN: Tidak ada PlateStorage untuk menerima piring kotor.");
            return;
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                target.pushDirtyPlate(dirtyPlate);
                System.out.println("DEBUG: Piring kotor kembali ke PlateStorage (delay 10 detik).");
            }
        }, PLATE_RETURN_DELAY_MS);
    }
}
