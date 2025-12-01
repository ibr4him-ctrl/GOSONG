package model.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList; // List aman untuk game loop

import model.item.Dish;
import model.item.dish.Order;
import model.item.dish.Order.PizzaType;

public class OrderManager {

    // 1. Singleton Instance
    private static OrderManager instance;

    // 2. Daftar Order Aktif
    // Menggunakan CopyOnWriteArrayList agar aman dihapus saat di-looping
    private List<Order> activeOrders;

    // 3. Variabel Timer & Spawning
    private Random random;
    private float spawnTimer;
    private static final float SPAWN_INTERVAL = 15.0f; // Order baru muncul tiap 15 detik
    private static final int MAX_ORDERS = 5; // Maksimal order di layar
    
    // Akumulator waktu (karena Order.decrementTime() menggunakan Integer/Detik)
    private double secondAccumulator = 0.0; 

    // --- Constructor Private (Singleton) ---
    private OrderManager() {
        activeOrders = new CopyOnWriteArrayList<>();
        random = new Random();
        spawnTimer = 5.0f; // Order pertama muncul setelah 5 detik game berjalan
    }

    // --- Akses Global ---
    public static OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    /**
     * Method ini WAJIB dipanggil di dalam GamePanel.update()
     * @param deltaSeconds Waktu yang berlalu antar frame (misal: 0.016 detik)
     */
    public void update(double deltaSeconds) {
        // A. Logic Spawn Order Baru
        spawnTimer -= deltaSeconds;
        
        // Jika waktu spawn habis DAN slot order masih ada
        if (spawnTimer <= 0 && activeOrders.size() < MAX_ORDERS) {
            spawnRandomOrder();
            spawnTimer = SPAWN_INTERVAL; // Reset timer spawn
        }

        // B. Logic Countdown (Pengurangan Waktu)
        secondAccumulator += deltaSeconds;
        
        // Jika sudah terkumpul 1 detik
        if (secondAccumulator >= 1.0) {
            for (Order order : activeOrders) {
                order.decrementTime(); // Kurangi 1 detik di Order.java
            }
            secondAccumulator -= 1.0; // Kurangi 1 detik dari akumulator
        }

        // C. Logic Hapus Order Expired (Kadaluarsa)
        for (Order order : activeOrders) {
            if (order.isExpired()) {
                System.out.println("ORDER GAGAL (Waktu Habis): " + order.getPizzaType().getDisplayName());
                
                // TODO: Panggil ScoreManager untuk memberi penalti di sini jika perlu
                
                activeOrders.remove(order); // Hapus dari daftar
            }
        }
    }

    /**
     * Membuat order acak dari daftar PizzaType yang ada.
     */
    private void spawnRandomOrder() {
        PizzaType[] types = PizzaType.values(); // Ambil semua tipe pizza (Margherita, Sosis, Ayam)
        PizzaType randomType = types[random.nextInt(types.length)]; // Pilih satu acak
        
        Order newOrder = new Order(randomType);
        activeOrders.add(newOrder);
        
        System.out.println("NEW ORDER: " + newOrder);
    }

    /**
     * Validasi masakan yang dibawa Chef.
     * Dipanggil oleh ServingCounter.interact().
     * * @param dish Masakan dari piring Chef
     * @return Order yang cocok (jika ada), atau null (jika salah masak)
     */
    public Order validateDish(Dish dish) {
        if (dish == null) return null;

        for (Order order : activeOrders) {
            // Bandingkan Tipe Pizza di Order dengan Tipe Pizza di Dish
            if (order.getPizzaType() == dish.getPizzaType()) {
                activeOrders.remove(order); // Hapus order (Tiket disobek/selesai)
                return order; // Kembalikan order untuk diambil score-nya
            }
        }
        
        return null; // Tidak ada order yang cocok
    }

    // Untuk keperluan menggambar UI di GamePanel (Daftar tiket di pojok layar)
    public List<Order> getActiveOrders() {
        return activeOrders;
    }
}