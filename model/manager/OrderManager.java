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

    // Selalu target 3 order aktif dengan timeLimit berjenjang 60/120/180 detik
    private static final int MAX_ORDERS = 3;
    private static final int INITIAL_ORDERS = 3;
    private static final int MAX_TOTAL_ORDERS = 3;
    private static final double SESSION_LIMIT_SECONDS = 180.0;

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

        // Buat 3 order awal sekaligus dengan time limit berjenjang 60/120/180
        // Urutan jenis pizza tetap random, tidak ada pola deterministik
        for (int i = 0; i < INITIAL_ORDERS; i++) {
            int baseTimeSeconds = 60 * (i + 1); // 60, 120, 180
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

        // Cari slot index berikutnya berdasarkan jumlah total order yang sudah pernah di-spawn
        int index = totalSpawnedOrders; // 0,1,2
        int baseTimeSeconds = 60 * (index + 1); // 60, 120, 180
        spawnRandomOrderWithTimeLimit(baseTimeSeconds);
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

    public Order validateDish(Dish dish) {
        if (dish == null) return null;

        for (Order order : activeOrders) {
            if (order.getPizzaType() == dish.getPizzaType()) {
                // Tambahkan skor untuk order yang berhasil
                ScoreManager.getInstance().add(ScoreManager.POINTS_SUCCESS);

                activeOrders.remove(order);
                
                // Lapor ke GameController bahwa order berhasil
                GameController.getInstance().onOrderSuccess();

                spawnOrderIfNeeded();
                return order;
            }
        }

        return null;
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
