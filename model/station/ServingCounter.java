package model.station;

import model.chef.Chef;
import model.item.Dish;
import model.item.Item;
import model.item.dish.Order;
import model.item.utensils.Plate;
import model.manager.OrderManager;

public class ServingCounter extends Station {

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

        if (!(hand instanceof Plate )) {
            return false;
        }
        Plate plate = (Plate) hand;

        if (plate.isEmpty()) {
            return false;
        }
        Dish dish = plate.getDish();

        if (dish == null) return false; 

        // 4. Validasi ke OrderManager
        // JIKA DISINI MERAH: Cek apakah OrderManager sudah di-import?
        // JIKA getPizzaType() MERAH: Cek Dish.java apakah methodnya getPizzaType()?
        Order matchedOrder = OrderManager.getInstance().validateDish(dish);

        if (matchedOrder != null) {
            // === SUKSES ===
            // Pastikan Order.java punya method getPizzaType()
            System.out.println("✅ SUKSES: " + matchedOrder.getPizzaType().getDisplayName());
            System.out.println("💰 Reward: +" + matchedOrder.getReward());
        } else {
            // === GAGAL ===
            // Pastikan Dish.java punya method getPizzaType()
            System.out.println(" GAGAL: Tidak ada pesanan untuk " + dish.getPizzaType().getDisplayName());
            System.out.println("Penalti -50");
        }

        // 5. Bersihkan
        chef.setHeldItem(null);
        plate.removeDish();
        plate.markDirty();
        
        returnToPlateStorage(plate);

        return true;
    }

    private void returnToPlateStorage(Plate dirtyPlate) {
        System.out.println("DEBUG: Piring kotor kembali ke storage.");
    }
}
