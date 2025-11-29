import model.item.dish.Order;
import view.GamePanel;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private static GamePanel gamePanel;
    private static JFrame window;
    private static int score = 0;
    private static JLabel scoreLabel;
    private static JTextArea orderLog;
    private static List<Order> activeOrders = new ArrayList<>();
    private static final int MAX_ORDERS = 4;
    private static final Random random = new Random();
    private static ScheduledExecutorService orderScheduler;

    public static void main(String[] args) {

        window = new JFrame("GOSONG - Pizza Chef");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setLayout(new BoxLayout(window.getContentPane(), BoxLayout.Y_AXIS));

        JPanel scorePanel = new JPanel();
        scoreLabel = new JLabel("Score: 0");
        scorePanel.add(scoreLabel);
        window.add(scorePanel);

        gamePanel = new GamePanel();
        window.add(gamePanel);

        orderLog = new JTextArea(5, 40);
        orderLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(orderLog);
        window.add(scrollPane);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.startGameThread();

        startOrderSystem();
    }

    private static void startOrderSystem() {
        orderScheduler = Executors.newScheduledThreadPool(1);
        orderScheduler.scheduleAtFixedRate(() -> {
            if (activeOrders.size() < MAX_ORDERS) {
                createNewOrder();
            }
        }, 0, 15, TimeUnit.SECONDS);

        new Thread(() -> {
            while (true) {
                updateOrders();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void createNewOrder() {
        Order.PizzaType[] types = Order.PizzaType.values();
        Order.PizzaType randomType = types[random.nextInt(types.length)];
        Order order = new Order(randomType);
        activeOrders.add(order);
        logOrder("New order: " + order);

        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    if (activeOrders.remove(order)) {
                        score += order.getPenalty();
                        updateScore();
                        logOrder("Order expired: " + order);
                    }
                }
            },
            order.getTimeLimit() * 1000L
        );
    }

    private static void updateOrders() {
        for (Order order : new ArrayList<>(activeOrders)) {
            if (order.decrementTime() <= 0) {
                activeOrders.remove(order);
            }
        }
    }

    public static void completeOrder(Order.PizzaType type) {
        for (Order order : new ArrayList<>(activeOrders)) {
            if (order.getPizzaType() == type) {
                score += order.getReward();
                activeOrders.remove(order);
                updateScore();
                logOrder("Order completed: " + order + " (+" + order.getReward() + " points)");
                return;
            }
        }
        score -= 20;
        updateScore();
        logOrder("Wrong order! (-20 points)");
    }

    private static void updateScore() {
        SwingUtilities.invokeLater(() -> 
            scoreLabel.setText("Score: " + score)
        );
    }

    private static void logOrder(String message) {
        SwingUtilities.invokeLater(() -> {
            orderLog.append(message + "\n");
            orderLog.setCaretPosition(orderLog.getDocument().getLength());
        });
    }

    public static void stopGame() {
        if (orderScheduler != null) {
            orderScheduler.shutdown();
        }
        System.exit(0);
    }
}