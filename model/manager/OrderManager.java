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

    private double secondAccumulator = 0.0;
    private double sessionTimeElapsed = 0.0;
    private double timeSinceLastSpawn = 0.0;

    private static final int MAX_ORDERS = 5;
    private static final int INITIAL_ORDERS = 1;
    private static final int MAX_TOTAL_ORDERS = 3;
    private static final double SESSION_LIMIT_SECONDS = 180.0;
    private static final double SPAWN_INTERVAL_SECONDS = 30.0;

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

    public void init() {
        activeOrders.clear();
        totalSpawnedOrders = 0;
        acceptingNewOrders = true;
        sessionOver = false;
        sessionTimeElapsed = 0.0;
        timeSinceLastSpawn = 0.0;
        secondAccumulator = 0.0;
        for (int i = 0; i < INITIAL_ORDERS; i++) {
            spawnRandomOrder();
        }
    }

    public void update(double deltaSeconds) {
        if (sessionOver) {
            return;
        }

        secondAccumulator += deltaSeconds;
        sessionTimeElapsed += deltaSeconds;
        timeSinceLastSpawn += deltaSeconds;

        if (secondAccumulator >= 1.0) {
            for (Order order : activeOrders) {
                order.decrementTime();
            }
            secondAccumulator -= 1.0;
        }

        for (Order order : activeOrders) {
            if (order.isExpired()) {
                System.out.println("ORDER GAGAL (Waktu habis): "
                        + order.getPizzaType().getDisplayName());

                model.manager.ScoreManager.getInstance().add(order.getPenalty());

                activeOrders.remove(order);
                spawnOrderIfNeeded();
            }
        }

        if (!acceptingNewOrders && activeOrders.isEmpty() && totalSpawnedOrders >= MAX_TOTAL_ORDERS) {
            if (!sessionOver) {
                sessionOver = true;
                System.out.println("Semua order selesai! Game Over.");
                Main.showGameOver();
            }
            return;
        }

        if (sessionTimeElapsed >= SESSION_LIMIT_SECONDS) {
            sessionOver = true;
            acceptingNewOrders = false;
            Main.showGameOver();
        }
    }

    private void spawnOrderIfNeeded() {
        if (!acceptingNewOrders) return;
        if (activeOrders.size() >= MAX_ORDERS) return;
        if (timeSinceLastSpawn < SPAWN_INTERVAL_SECONDS) return;
        spawnRandomOrder();
    }

    private void spawnRandomOrder() {
        if (totalSpawnedOrders >= MAX_TOTAL_ORDERS) {
            acceptingNewOrders = false;
            return;
        }

        PizzaType[] types = PizzaType.values();
        PizzaType randomType = types[random.nextInt(types.length)];

        Order newOrder = new Order(randomType);
        activeOrders.add(newOrder);
        totalSpawnedOrders++;
        timeSinceLastSpawn = 0.0;

        System.out.println("NEW ORDER: " + newOrder);
    }

    public Order validateDish(Dish dish) {
        if (dish == null) return null;

        for (Order order : activeOrders) {
            if (order.getPizzaType() == dish.getPizzaType()) {
                activeOrders.remove(order);

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
}
