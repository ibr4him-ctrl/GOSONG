package model.manager;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import model.item.Dish;
import model.item.dish.Order;
import model.item.dish.Order.PizzaType;

public class OrderManager {

    private static OrderManager instance;

    // Daftar order aktif (antrian)
    private final List<Order> activeOrders;

    private final Random random;

    // Untuk countdown per detik
    private double secondAccumulator = 0.0;

    // Spec: maksimal order yang ditampilkan (boleh 4 atau 5)
    private static final int MAX_ORDERS = 5;
    private static final int INITIAL_ORDERS = 3;

    // Untuk stage over: kalau false, tidak spawn order baru lagi
    private boolean acceptingNewOrders = true;

    private OrderManager() {
        this.activeOrders = new CopyOnWriteArrayList<>();
        this.random = new Random();
    }

    public static OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    /**
     * Dipanggil sekali di awal game (misal di GamePanel constructor).
     */
    public void init() {
        activeOrders.clear();
        for (int i = 0; i < INITIAL_ORDERS; i++) {
            spawnRandomOrder();
        }
    }

    /**
     * Dipanggil dari GamePanel.update(deltaSeconds).
     */
    public void update(double deltaSeconds) {

        // A. Countdown time limit (per detik)
        secondAccumulator += deltaSeconds;
        if (secondAccumulator >= 1.0) {
            for (Order order : activeOrders) {
                order.decrementTime();
            }
            secondAccumulator -= 1.0;
        }

        // B. Hapus order yang expired
        for (Order order : activeOrders) {
            if (order.isExpired()) {
                System.out.println("ORDER GAGAL (Waktu habis): "
                        + order.getPizzaType().getDisplayName());

                // penalti karena timeout
                // misal pakai penalty di order
                model.manager.ScoreManager.getInstance().add(order.getPenalty());

                activeOrders.remove(order);
                spawnOrderIfNeeded();
            }
        }
    }

    /**
     * Spawn order baru jika:
     * - masih menerima order baru
     * - belum mencapai batas MAX_ORDERS
     */
    private void spawnOrderIfNeeded() {
        if (!acceptingNewOrders) return;
        if (activeOrders.size() >= MAX_ORDERS) return;
        spawnRandomOrder();
    }

    /**
     * Order baru dengan jenis pizza random sesuai level.
     */
    private void spawnRandomOrder() {
        PizzaType[] types = PizzaType.values();
        PizzaType randomType = types[random.nextInt(types.length)];

        Order newOrder = new Order(randomType);
        activeOrders.add(newOrder);

        System.out.println("NEW ORDER: " + newOrder);
    }

    /**
     * Dipanggil oleh ServingCounter ketika chef menyajikan dish.
     * Mengembalikan Order yang cocok (kalau ada), atau null kalau salah.
     * 
     * Spek:
     * - Kalau ada 2 order sama, selesaikan yang paling awal masuk.
     *   → kita iterasi dari depan list (insertion order).
     */
    public Order validateDish(Dish dish) {
        if (dish == null) return null;

        for (Order order : activeOrders) {
            if (order.getPizzaType() == dish.getPizzaType()) {
                // Order ini yang dianggap selesai (FIFO)
                activeOrders.remove(order);

                // Setelah 1 order selesai → spawn order baru (kalau boleh)
                spawnOrderIfNeeded();
                return order;
            }
        }

        // Tidak ada order dengan jenis pizza ini
        return null;
    }

    public List<Order> getActiveOrders() {
        return activeOrders;
    }

    /**
     * Dipanggil saat Stage Over (Time's Up) → game berhenti menerima order baru.
     */
    public void stopAcceptingNewOrders() {
        acceptingNewOrders = false;
    }
}
