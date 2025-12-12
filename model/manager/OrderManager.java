package model.manager;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import main.Main;
import model.item.Dish;
import model.item.dish.Order;
import model.item.dish.Order.PizzaType;

public class OrderManager {

    private static OrderManager instance;

    private final List<Order> activeOrders;

    private final Random random;

    private double sessionTimeElapsed = 0.0;

    // Selalu target 3 order aktif dengan timeLimit 80/160/240 detik sesuai urutan
    private static final int MAX_ORDERS = 3;
    private static final int INITIAL_ORDERS = 3;
    private static final int MAX_TOTAL_ORDERS = 3;
    // Total waktu sesi 4 menit (240 detik)
    private static final double SESSION_LIMIT_SECONDS = 240.0;

    private boolean acceptingNewOrders = true;
    private boolean sessionOver = false;
    private int totalSpawnedOrders = 0;

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
    
    public static void resetInstance() {
        instance = null;
    }

    public void init() {
        activeOrders.clear();
        totalSpawnedOrders = 0;
        acceptingNewOrders = true;
        sessionOver = false;
        sessionTimeElapsed = 0.0;

        // Buat 3 order awal sekaligus dengan time limit 80, 160, 240 detik
        // berdasarkan urutan spawn
        for (int i = 0; i < INITIAL_ORDERS; i++) {
            int baseTimeSeconds = 80 * (i + 1); // 80, 160, 240
            spawnRandomOrderWithTimeLimit(baseTimeSeconds);
        }
    }

    public void update(double deltaSeconds) {
        if (sessionOver) {
            return;
        }

        sessionTimeElapsed += deltaSeconds;

        // Recalculate timeRemaining semua order berbasis waktu absolut
        for (Order order : activeOrders) {
            order.recalculateTimeRemaining();
        }

        for (Order order : activeOrders) {
            if (order.isExpired()) {
                System.out.println("ORDER GAGAL (Waktu habis): "
                        + order.getPizzaType().getDisplayName());

                ScoreManager.getInstance().add(ScoreManager.PENALTY_FAIL);
                
                // Lapor ke GameController bahwa order gagal
                GameController.getInstance().onOrderFailed();

                activeOrders.remove(order);
                spawnOrderIfNeeded();
            }
        }

        if (!acceptingNewOrders && activeOrders.isEmpty() && totalSpawnedOrders >= MAX_TOTAL_ORDERS) {
            if (!sessionOver) {
                sessionOver = true;
                System.out.println("Semua order selesai! Game Over.");
                // Delegate to GameController for centralized handling
                model.manager.GameController.getInstance().handleAllOrdersCompleted();
            }
            return;
        }

        if (sessionTimeElapsed >= SESSION_LIMIT_SECONDS) {
            sessionOver = true;
            acceptingNewOrders = false;
            // Delegate to GameController for centralized handling
            model.manager.GameController.getInstance().handleSessionTimeUp();
        }
    }

    private void spawnOrderIfNeeded() {
        if (!acceptingNewOrders) return;
        if (activeOrders.size() >= MAX_ORDERS) return;
        if (totalSpawnedOrders >= MAX_TOTAL_ORDERS) return;

        // Tidak spawn order baru di luar 3 order pertama
    }

    private void spawnRandomOrderWithTimeLimit(int timeLimitSeconds) {
        if (totalSpawnedOrders >= MAX_TOTAL_ORDERS) {
            acceptingNewOrders = false;
            return;
        }

        PizzaType[] types = PizzaType.values();
        PizzaType randomType = types[random.nextInt(types.length)];

        Order newOrder = new Order(randomType, timeLimitSeconds);
        activeOrders.add(newOrder);
        totalSpawnedOrders++;

        System.out.println("NEW ORDER: " + newOrder);
    }

    public boolean validateDish(Dish dish) {
        if (dish == null) return false;
        for (Order order : activeOrders) {
            if (order.getPizzaType() == dish.getPizzaType()) {
                // --- KASUS SUKSES ---
                // Tambahkan skor untuk order yang berhasil
                ScoreManager.getInstance().add(ScoreManager.POINTS_SUCCESS);
                activeOrders.remove(order);
                
                // Lapor ke GameController bahwa order berhasil
                GameController.getInstance().onOrderSuccess();
                spawnOrderIfNeeded();
                return true; // Berhasil
            }
        }

        // --- KASUS GAGAL ---
        // Jika loop selesai dan tidak ada order yang cocok
        ScoreManager.getInstance().add(ScoreManager.PENALTY_FAIL);
        GameController.getInstance().onOrderFailed();
        return false; // Gagal
    }

    public List<Order> getActiveOrders() {
        return activeOrders;
    }

    public void stopAcceptingNewOrders() {
        acceptingNewOrders = false;
    }

    public double getSessionTimeElapsed() {
        return sessionTimeElapsed;
    }

    public static double getSessionLimitSeconds() {
        return SESSION_LIMIT_SECONDS;
    }

    // === GETTER TAMBAHAN UNTUK GAMECTROLLER ===
    public boolean isAcceptingNewOrders() {
        return acceptingNewOrders;
    }

    public int getTotalSpawnedOrders() {
        return totalSpawnedOrders;
    }

    public static int getMaxTotalOrders() {
        return MAX_TOTAL_ORDERS;
    }
}
