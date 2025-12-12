package model.station;

import model.chef.Chef;
import model.item.Item;
import model.item.Preparable;
import model.item.ingredient.Ingredient;
import model.item.ingredient.pizza.Dough;
import model.item.utensils.Plate;
import model.logic.PlatingRules;
import util.SoundEffectPlayer;

public class CuttingStation extends Station {

    private Ingredient currentIngredient;
    private double cutProgressSeconds;
    private boolean cutting;
    private Chef currentChef;

    private static final double CUT_TIME = 3.0; // 3 detik

    // ===== SFX =====
    private static final SoundEffectPlayer SFX = new SoundEffectPlayer();
    private static final String SFX_CUTTING =
            "/resources/game/sound_effect/cutting_sound.wav";
    private static final String SFX_PICKUPDROP =
            "/resources/game/sound_effect/pickupdrop.wav";

    // key unik per station biar loop-nya bisa distop aman
    private final String cuttingLoopKey = "CUTTING_" + System.identityHashCode(this);

    private void startCuttingSound() {
        SFX.playLoop(cuttingLoopKey, SFX_CUTTING);
    }

    private void stopCuttingSound() {
        SFX.stopLoop(cuttingLoopKey);
    }

    private void playPickupDrop() {
        SFX.playOnce(SFX_PICKUPDROP);
    }
    public CuttingStation(int x, int y) {
        super(x, y, "Cutting");
        this.currentIngredient = null;
        this.cutProgressSeconds = 0.0;
        this.cutting = false;
        this.currentChef = null;
    }

    @Override
    public String getSymbol() {
        return "C";
    }

    /**
     * DIPANGGIL OLEH TOMBOL V (Use Station)
     * Fokus: mulai / lanjut proses CUTTING.
     * Kombinasi / plating dilakukan lewat C → handlePickUpDrop().
     */
    @Override
    public boolean interact(Chef chef) {
        if (!isAdjacentTo(chef)) return false;

        Item hand = chef.getHeldItem();
        Item top  = itemOnStation;

        // V khusus buat cutting → kalau tangan nggak kosong, tolak
        if (hand != null) {
            System.out.println("[Cutting] (V) Gunakan tombol C untuk taruh / ambil / gabung. " +
                               "V hanya untuk MEMULAI / MELANJUTKAN memotong.");
            return false;
        }

        // Pastikan ada ingredient di atas papan
        if (!(top instanceof Ingredient ingOnTop)) {
            System.out.println("[Cutting] (V) Tidak ada ingredient di station. " +
                               "Taruh ingredient RAW dulu dengan tombol C.");
            return false;
        }

        // Kalau ingredient tidak bisa di-chop
        if (!ingOnTop.canBeChopped()) {
            System.out.println("[Cutting] (V) Item di CuttingStation tidak bisa dipotong.");
            return false;
        }

        // Kalau lagi di-pause → lanjutkan
        if (!cutting && cutProgressSeconds > 0.0) {
            cutting = true;
            currentIngredient = ingOnTop;
            currentChef = chef;
            chef.setBusy(true);
            startCuttingSound();
            System.out.println("[Cutting] (V) Lanjut memotong " + ingOnTop.getName() +
                               ", progress = " + cutProgressSeconds + " detik");
            return true;
        }

        // Kalau belum mulai sama sekali → mulai motong
        if (!cutting && cutProgressSeconds == 0.0) {
            cutting = true;
            currentIngredient = ingOnTop;
            currentChef = chef;
            chef.setBusy(true);
            startCuttingSound();
            System.out.println("[Cutting] (V) Mulai memotong " + ingOnTop.getName() +
                               " di CuttingStation (" + posX + "," + posY + ")");
            return true;
        }

        // Kalau sudah cutting dan V dipencet lagi → abaikan
        return false;
    }

    // =====================================================
    //  HELPER: PLATING NORMAL (sama konsepnya dengan Assembly)
    // =====================================================
    private boolean performPlatingOnCutting(Chef chef,
                                            Plate plate,
                                            Preparable ingredient,
                                            boolean plateInHand) {

        boolean ok = PlatingRules.applyPlating(plate, ingredient, "[CuttingStation]");
        if (!ok) return false;

        if (plateInHand) {
            // Plate di tangan, ingredient di meja → ingredient hilang dari station
            itemOnStation = null;
            chef.setHeldItem(plate);
        } else {
            // Plate di meja, ingredient di tangan → tangan jadi kosong, plate tetap di station
            chef.setHeldItem(null);
            itemOnStation = plate;
        }

        playPickupDrop();

        // Kalau ingredient ini kebetulan adalah currentIngredient yang sedang / pernah di-cut
        if (ingredient == currentIngredient) {
            currentIngredient = null;
            cutProgressSeconds = 0.0;
            cutting = false;
            currentChef = null;
            stopCuttingSound();
        }

        return true;
    }

    // =====================================================
    //  UPDATE CUTTING PROGRESS (dipanggil tiap frame di GamePanel)
    // =====================================================
    public void update(double deltaSeconds) {
        if (!cutting || currentIngredient == null) return;

        // Kalau chef menjauh → pause
        if (currentChef != null && !isAdjacentTo(currentChef)) {
            System.out.println("[Cutting] Chef menjauh dari CuttingStation, pemotongan dipause.");
            pauseCutting();
            return;
        }

        cutProgressSeconds += deltaSeconds;

        if (cutProgressSeconds >= CUT_TIME) {
            currentIngredient.chop(); // Ubah state ke CHOPPED
            cutting = false;
            cutProgressSeconds = CUT_TIME;

            stopCuttingSound();

            System.out.println("[Cutting] Selesai memotong " + currentIngredient.getName() +
                               " → state = " + currentIngredient.getState());

            if (currentChef != null) {
                currentChef.setBusy(false);
                currentChef = null;
            }
        }
    }

    public void pauseCutting() {
        cutting = false;

        stopCuttingSound();


        if (currentChef != null) {
            currentChef.setBusy(false);
            currentChef = null;
        }
    }

    public boolean isCutting() {
        return cutting;
    }

    public double getCutProgressSeconds() {
        return cutProgressSeconds;
    }

    /** 0.0 .. 1.0 */
    public double getProgressRatio() {
        if (CUT_TIME <= 0) return 0.0;
        return Math.min(1.0, cutProgressSeconds / CUT_TIME);
    }

    /** 0 .. 100 % */
    public int getProgressPercent() {
        return (int) Math.round(getProgressRatio() * 100.0);
    }

    public String getProgressBar(int width) {
        double ratio = getProgressRatio();
        int filled = (int) Math.round(ratio * width);
        if (filled > width) filled = width;

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < width; i++) {
            sb.append(i < filled ? '#' : '-');
        }
        sb.append("] ").append(getProgressPercent()).append('%');
        return sb.toString();
    }

    public boolean handlePickUpDrop(Chef chef) {
        Item hand = chef.getHeldItem();
        Item top  = itemOnStation;

        // =====================================================
        // BLOCK: pizza base (dough chopped + topping) TIDAK boleh diambil tangan kosong
        // =====================================================
        if (!cutting && hand == null && top instanceof Dough dough) {
            boolean hasTopping = dough.getToppings() != null && !dough.getToppings().isEmpty();
            if (dough.isChopped() && hasTopping) {
                System.out.println("[Cutting] Pizza (dough + topping) tidak boleh diambil tangan kosong. " +
                                "Gunakan plate bersih untuk mengambilnya.");
                return false;
            }
        }

        // 0) PRIORITAS: PLATE + PREPARABLE (PLATING) kalau tidak sedang cutting
        if (!cutting) {
            // Plate di tangan, makanan di papan
            if (hand instanceof Plate plateInHand && top instanceof Preparable prepOnBoard) {
                return performPlatingOnCutting(chef, plateInHand, prepOnBoard, true);
            }

            // Makanan di tangan, plate di papan
            if (top instanceof Plate plateOnBoard && hand instanceof Preparable prepInHand) {
                return performPlatingOnCutting(chef, plateOnBoard, prepInHand, false);
            }

            // 0b) Dough + topping chopped TANPA plate (gabung makanan di papan)
            // Dough di papan, topping di tangan
            if (top instanceof Dough doughOnBoard && hand instanceof Ingredient ingInHand) {
                if (!doughOnBoard.isChopped()) {
                    System.out.println("[Cutting] Dough harus CHOPPED dulu sebelum ditambah topping.");
                    return false;
                }
                if (!ingInHand.isChopped()) {
                    System.out.println("[Cutting] Topping harus CHOPPED kalau mau digabung ke dough.");
                    return false;
                }

                boolean ok = doughOnBoard.addTopping(ingInHand);
                if (!ok) return false;

                chef.setHeldItem(null);
                playPickupDrop();
                System.out.println("[Cutting] " + ingInHand.getName() +
                                " ditambahkan ke Dough di CuttingStation.");
                return true;
            }

            // Dough di tangan, topping di papan
            if (hand instanceof Dough doughInHand && top instanceof Ingredient ingOnBoard) {
                if (!doughInHand.isChopped()) {
                    System.out.println("[Cutting] Dough harus CHOPPED dulu sebelum ditambah topping.");
                    return false;
                }
                if (!ingOnBoard.isChopped()) {
                    System.out.println("[Cutting] Topping harus CHOPPED kalau mau digabung ke dough.");
                    return false;
                }

                boolean ok = doughInHand.addTopping(ingOnBoard);
                if (!ok) return false;

                itemOnStation = null;
                playPickupDrop();
                System.out.println("[Cutting] " + ingOnBoard.getName() +
                                " ditambahkan ke Dough yang dipegang chef.");
                return true;
            }
        }

        // 1) TARUH INGREDIENT RAW KE STATION UNTUK DIPOTONG
        if (hand instanceof Ingredient ing && ing.canBeChopped()) {

            if (currentIngredient != null || itemOnStation != null || cutting) {
                System.out.println("[Cutting] (C) Station sudah terisi / sedang memotong.");
                return false;
            }

            currentIngredient = ing;
            itemOnStation = ing;
            cutProgressSeconds = 0.0;
            chef.setHeldItem(null);
            playPickupDrop();

            System.out.println("[Cutting] (C) Menaruh " + ing.getName() +
                            " (RAW) di CuttingStation (" + posX + "," + posY + ")");
            return true;
        }

        // 2) AMBIL INGREDIENT DARI STATION (RAW / CHOPPED) SAAT TIDAK CUTTING
        if (hand == null && itemOnStation instanceof Ingredient ing2 && !cutting) {
            chef.setHeldItem(ing2);
            itemOnStation = null;

            if (ing2 == currentIngredient) {
                currentIngredient = null;
                cutProgressSeconds = 0.0;
                currentChef = null;
            }
            playPickupDrop();

            System.out.println("[Cutting] (C) Mengambil " + ing2.getName() +
                            " (" + ing2.getState() + ") dari CuttingStation.");
            return true;
        }

        System.out.println("[Cutting] (C) Tidak ada aksi valid di CuttingStation.");
        return false;
    }

}
