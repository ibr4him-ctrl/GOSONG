package model.station;

import java.util.Timer;
import java.util.TimerTask;
import model.chef.Chef;
import model.item.Dish;
import model.item.Item;
import model.item.utensils.Plate;
import model.manager.GameController;
import model.manager.OrderManager;
import model.manager.ScoreManager;
import util.SoundEffectPlayer;

public class ServingCounter extends Station {

    private static final long PLATE_RETURN_DELAY_MS = 10_000L; // 10 detik
    // ===== SFX =====
    private static final SoundEffectPlayer SFX = new SoundEffectPlayer();
    private static final String SFX_RIGHT =
            "/resources/game/sound_effect/right_order.wav";
    private static final String SFX_WRONG =
            "/resources/game/sound_effect/wrong_order.wav";
    private static final String SFX_PUT_PLATE =
            "/resources/game/sound_effect/putting_plates.wav";


    public ServingCounter(int x, int y) {
        super(x, y, "Serving");
    }

    @Override
    public String getSymbol() {
        return "S";
    }

    
    @Override
    public boolean interact(Chef chef) {
        if (!isAdjacentTo(chef)) return false;

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
            GameController.getInstance().onOrderFailed();
            SFX.playOnce(SFX_WRONG); 
        } else {
            // === KASUS: ada Dish, validasi ke OrderManager ===
            System.out.println("[Serving] Dish type: " + dish.getPizzaType());

            // validateDish() sudah handle scoring & GameController notification
            boolean success = OrderManager.getInstance().validateDish(dish);

            if (success) {
                // SUKSES - scoring dan notifikasi sudah ditangani oleh OrderManager
                System.out.println("[ServingCounter] SUKSES: Pesanan " + dish.getPizzaType().getDisplayName() + " berhasil disajikan!");
                SFX.playOnce(SFX_RIGHT);
            } else {
                // GAGAL - order tidak cocok
                // Scoring dan notifikasi kegagalan sudah ditangani oleh OrderManager
                System.out.println("[ServingCounter] GAGAL: Tidak ada pesanan untuk " + dish.getPizzaType().getDisplayName());
                SFX.playOnce(SFX_WRONG);
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
                SFX.playOnce(SFX_PUT_PLATE);
                System.out.println("[ServingCounter] Piring kotor kembali ke PlateStorage (delay 10 detik).");
            }
        }, PLATE_RETURN_DELAY_MS);
    }
}