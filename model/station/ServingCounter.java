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

        Item hand = chef.getHeldItem();
        Item onStation = getItemOnStation();

        System.out.println("[Serving] HAND = " +
                (hand == null ? "null" : hand.getName() + " (" + hand.getClass().getSimpleName() + ")"));
        System.out.println("[Serving] ON STATION = " +
                (onStation == null ? "null" : onStation.getName() + " (" + onStation.getClass().getSimpleName() + ")"));

        Plate plate = null;
        Dish dish = null;
        boolean plateFromHand = false;
        boolean itemFromHand = false;

        // ====== 1. Coba pakai PLATE berisi Dish dulu (prioritas spek) ======
        if (hand instanceof Plate p) {
            System.out.println("[Serving] Plate di tangan, contents size = " + p.getContents().size());
            if (!p.isEmpty()) {
                plate = p;
                dish = p.getDish();
                plateFromHand = true;
                itemFromHand = true;
                System.out.println("[Serving] → pakai plate dari TANGAN");
            } else {
                System.out.println("[Serving] Plate di tangan tapi kosong menurut kode.");
            }
        }

        if (plate == null && onStation instanceof Plate p2) {
            System.out.println("[Serving] Plate di station, contents size = " + p2.getContents().size());
            if (!p2.isEmpty()) {
                plate = p2;
                dish = p2.getDish();
                plateFromHand = false;
                itemFromHand = false;
                System.out.println("[Serving] → pakai plate dari STATION");
                setItemOnStation(null); // anggap diambil
            } else {
                System.out.println("[Serving] Plate di station tapi kosong menurut kode.");
            }
        }

        // ====== 2. Kalau belum ketemu Dish, coba DISH langsung (tanpa plate) ======
        if (dish == null && hand instanceof Dish d) {
            dish = d;
            itemFromHand = true;
            System.out.println("[Serving] → pakai DISH dari TANGAN tanpa plate: " + dish);
        } else if (dish == null && onStation instanceof Dish d2) {
            dish = d2;
            itemFromHand = false;
            System.out.println("[Serving] → pakai DISH dari STATION tanpa plate: " + dish);
            setItemOnStation(null);
        }

        // Masih ga ada dish juga? yaudah gagal
        if (dish == null) {
            System.out.println("[Serving] Tidak menemukan Dish yang bisa disajikan (plate/dish).");
            return false;
        }

        System.out.println("[Serving] Dish type: " + dish.getPizzaType());

        // ====== 3. Validasi ke OrderManager ======
        Order matchedOrder = OrderManager.getInstance().validateDish(dish);

        if (matchedOrder != null) {
            System.out.println("✅ SUKSES: " + matchedOrder.getPizzaType().getDisplayName());
            System.out.println("💰 Reward: +" + matchedOrder.getReward());
            // TODO: ScoreManager.add(matchedOrder.getReward());
        } else {
            System.out.println("❌ GAGAL: Tidak ada pesanan untuk "
                    + dish.getPizzaType().getDisplayName());
            System.out.println("Penalti -50 (contoh)");
            // TODO: ScoreManager.add(-50);
        }

        // ====== 4. Bersihin item yang sudah diserve ======
        if (plate != null) {
            // Kalau pakai plate → dish dihapus dari plate, plate jadi kotor, balik ke storage
            plate.removeDish();
            plate.markDirty();
            if (plateFromHand) {
                chef.setHeldItem(null);
            }
            returnToPlateStorage(plate);
        } else if (itemFromHand) {
            // Kalau cuma Dish langsung di tangan → anggap dimakan, tangan kosong
            chef.setHeldItem(null);
        }

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
