package model.station;

import java.util.Timer;
import java.util.TimerTask;
import model.chef.Chef;
import model.item.Dish;
import model.item.Item;
import model.item.dish.Order;
import model.item.utensils.Plate;
import model.manager.OrderManager;
import model.manager.ScoreManager;

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
        // harus pegang Plate
        if (!(hand instanceof Plate plate)) {
            System.out.println("[Serving] Chef harus megang plate, bukan " +
                    (hand == null ? "tangan kosong" : hand.getClass().getSimpleName()));
            return false;
        }

        // plate harus bersih (dari PlateStorage / habis dicuci)
        if (!plate.isClean()) {
            System.out.println("[Serving] Plate kotor, gak boleh menyajikan.");
            return false;
        }


        // plate harus berisi Dish final
        Dish dish = plate.getDish();   // method yang sudah kamu buat di Plate
        
        if (dish == null) {
            System.out.println("[Serving] Plate belum berisi Dish final (pizza belum jadi).");
            System.out.println("Penalti -50 (serve pizza belum jadi).");
            ScoreManager.getInstance().add(-50);
        }else {
            // === KASUS: ada Dish, cek ke OrderManager ===
            System.out.println("[Serving] Dish type: " + dish.getPizzaType());

            Order matchedOrder = OrderManager.getInstance().validateDish(dish);

            if (matchedOrder != null) {
                System.out.println("SUKSES: " + matchedOrder.getPizzaType().getDisplayName());
                System.out.println("Reward: +" + matchedOrder.getReward());
                ScoreManager.getInstance().add(matchedOrder.getReward());
            } else {
                System.out.println("GAGAL: Tidak ada pesanan untuk "
                        + dish.getPizzaType().getDisplayName());
                System.out.println("Penalti -50 (pizza salah pesanan).");
                ScoreManager.getInstance().add(-50);
            }
        }

        // 4. DI SEMUA KASUS DI ATAS (selama pegang plate bersih) → plate diambil:
        plate.removeDish();   // kosongkan isi plate (kalau ada)
        plate.markDirty();    // status jadi kotor
        chef.setHeldItem(null);
        returnToPlateStorage(plate);   // delay 10 detik, balik ke PlateStorage

        return true; // interaksi BERHASIL (meski hasilnya penalti)
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
