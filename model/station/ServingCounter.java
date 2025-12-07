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

        // Spek: harus menyerahkan plate (dengan dish)
        if (!(hand instanceof Plate)) {
            return false;
        }

        Plate plate = (Plate) hand;

        if (plate.isEmpty()) {
            // tidak ada dish di atas plate
            return false;
        }

        Dish dish = plate.getDish();
        if (dish == null) return false;

        // Validasi ke OrderManager
        Order matchedOrder = OrderManager.getInstance().validateDish(dish);

        if (matchedOrder != null) {
            // ====== SERVE BERHASIL ======
            System.out.println("✅ SUKSES: " + matchedOrder.getPizzaType().getDisplayName());
            System.out.println("💰 Reward: +" + matchedOrder.getReward());

            // TODO: aplikasi skor lewat ScoreManager.
        } else {
            // ====== SERVE GAGAL ======
            System.out.println("❌ GAGAL: Tidak ada pesanan untuk "
                    + dish.getPizzaType().getDisplayName());
            System.out.println("Penalti -50 (contoh)");

            // TODO: aplikasi penalti skor di sini.
        }

        // Dish dimakan / dihapus; piring jadi kotor
        plate.removeDish();
        plate.markDirty();

        // Chef tidak lagi memegang apa-apa
        chef.setHeldItem(null);

        // Piring otomatis kembali ke Plate Storage sebagai kotor setelah 10 detik
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
