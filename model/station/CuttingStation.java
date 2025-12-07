package model.station;

import model.chef.Chef;
import model.enums.IngredientState;
import model.item.Item;
import model.item.ingredient.Ingredient;

public class CuttingStation extends Station {

    private Ingredient currentIngredient;
    private double cutProgressSeconds;
    private boolean cutting;
    private Chef currentChef; 

    private static final double CUT_TIME = 3.0; //3 detik

    public CuttingStation(int x, int y) {
        super(x, y,"Cutting");
        this.currentIngredient = null;
        this.cutProgressSeconds = 0.0;
        this.cutting = false;
        this.currentChef = null; 
    }

    @Override
    public String getSymbol() {
        return "C";
    }

    public boolean interact(Chef chef) {
        if (!isAdjacentTo(chef)) return false;

        Item hand = chef.getHeldItem();

        // 1) Kalau sudah selesai dipotong (CHOPPED) dan tangan kosong → pickup
        if (currentIngredient != null &&
            currentIngredient.getState() == IngredientState.CHOPPED &&
            !cutting && hand == null) {

            chef.setHeldItem(currentIngredient);      // chef pegang hasil potongan
            System.out.println("Chef mengambil " + currentIngredient.getName()
                            + " (CHOPPED) dari CuttingStation.");

            // kosongkan station
            itemOnStation = null;
            currentIngredient = null;
            cutProgressSeconds = 0.0;
            currentChef = null;
            return true;
        }

        // 2) Kalau belum ada ingredient yang lagi di-manage → coba mulai potong
        if (currentIngredient == null) {
            Ingredient target = null;

            // from hand: RAW ingredient di tangan → taruh ke station
            if (hand instanceof Ingredient ing &&
                ing.canBeChopped()) {

                target = ing;
                chef.setHeldItem(null);

            // from station: ada RAW ingredient di atas station, tangan kosong
            } else if (hand == null && itemOnStation instanceof Ingredient ing2 &&
                    ing2.canBeChopped()) {

                target = ing2;

            } else {
                // bukan RAW ingredient / kondisi nggak valid
                System.out.println("Item tidak bisa dipotong di CuttingStation.");
                return false;
            }

            itemOnStation = target;
            currentIngredient = target;
            cutProgressSeconds = 0.0;   // mulai dari 0 lagi
            cutting = true;
            currentChef = chef;
            chef.setBusy(true);

            System.out.println("Mulai memotong " + target.getName()
                            + " di CuttingStation (" + posX + "," + posY + ")");
            return true;
        }

        // 3) Sudah ada ingredient RAW, tapi lagi di-pause → lanjutkan motong
        if (currentIngredient != null &&
            currentIngredient.canBeChopped() &&
            !cutting) {

            cutting = true;
            currentChef = chef;
            chef.setBusy(true);

            System.out.println("Lanjut memotong " + currentIngredient.getName());
            return true;
        }

        // 4) Selain kondisi-kondisi di atas → nggak ada aksi yang valid
        return false;
    }


    public void update(double deltaSeconds) {
        if (!cutting || currentIngredient == null) return;

        // Kalau chef sudah tidak adjacent lagi ke station → pause
        if (currentChef != null && !isAdjacentTo(currentChef)) {
            System.out.println("Chef menjauh dari CuttingStation, pemotongan dipause.");
            pauseCutting();
            return;
        }
        cutProgressSeconds += deltaSeconds;

        if (cutProgressSeconds >= CUT_TIME) {
            currentIngredient.chop();     //UBAH STATE KE CHOPPED 
            cutting = false;
            cutProgressSeconds = CUT_TIME;
            
            System.out.println("Selesai memotong " + currentIngredient.getName() +
                       " → state = " + currentIngredient.getState());

            if (currentChef != null){
                currentChef.setBusy(false);
                currentChef = null; 
            }
        }
    }

    public void pauseCutting() {
        cutting = false;
        if (currentChef != null){
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
    
   // ===== helper untuk progress bar di CLI =====

    /** 0.0 .. 1.0 */
    public double getProgressRatio() {
        if (CUT_TIME <= 0) return 0.0;
        return Math.min(1.0, cutProgressSeconds / CUT_TIME);
    }

    /** 0 .. 100 % */
    public int getProgressPercent() {
        return (int) Math.round(getProgressRatio() * 100.0);
    }

    /** e.g. "[##########----------] 50%" */
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
}