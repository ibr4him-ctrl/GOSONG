package model.station;

import model.chef.Chef;
import model.enums.IngredientState;
import model.item.Item;
import model.item.ingredient.Ingredient;

public class IngredientStorage extends Station {

    private Class<? extends Ingredient> ingredientType;
    private String ingredientName;


    public IngredientStorage(int x, int y) {
        super(x, y, "IngredientStorage");

    }

    /**
     * Constructor utama yang dipakai dari GamePanel.initStationsFromMap()
     */
    public IngredientStorage(int x, int y,
                             Class<? extends Ingredient> ingredientType,
                             String ingredientName) {
        super(x, y, "IngredientStorage");
        this.ingredientType = ingredientType;
        this.ingredientName = ingredientName;
    }

    @Override
    public String getSymbol() {
        return "I";
    }

    @Override
    public boolean interact(Chef chef) {
        if (!isAdjacentTo(chef)) {
            System.out.println(" Chef terlalu jauh dari ingredient storage!");
            return false;
        }

        Item hand = chef.getHeldItem();

        // 1) Tangan kosong, tapi ada item di atas storage → ambil itemOnStation
        if (hand == null && itemOnStation != null) {
            chef.setHeldItem(itemOnStation);
            itemOnStation = null;
            return true;
        }

        // 2) Tangan kosong, storage kosong → spawn ingredient baru (infinite stock)
        if (hand == null && itemOnStation == null) {
            Ingredient newIngredient = createNewIngredient();

            if (newIngredient != null) {
                chef.setHeldItem(newIngredient);
                System.out.println("Chef mengambil " + newIngredient.getName() +
                                   " (RAW) dari " + ingredientName + " storage.");
                return true;
            } else {
                // createNewIngredient() gagal
                return false;
            }
        }

        // 3) Tangan pegang sesuatu
        if (hand != null) {

            // Kalau ada item di atas storage → gak bisa taruh lagi
            if (itemOnStation != null) {
                System.out.println("Sudah ada item di atas storage, tidak bisa menaruh lagi!");
                return false;
            }

            if (!isValidIngredient(hand)) {
                if (hand instanceof Ingredient ing &&
                    ingredientType != null &&
                    ingredientType.isInstance(ing) &&
                    ing.getState() != IngredientState.RAW) {

                    System.out.println("Tidak bisa menyimpan " + ingredientName +
                            " yang sudah " + ing.getState() + " ke storage (hanya RAW).");
                } else {
                    System.out.println("Item ini bukan " + ingredientName +
                            ", tidak bisa disimpan di " + ingredientName + " storage.");
                }
                return false;
            }
            

            itemOnStation = hand;
            chef.setHeldItem(null);
            System.out.println("Chef meletakkan " + itemOnStation.getName() +
                               " di atas " + ingredientName + " storage.");
            return true;
        }

        return false;
    }

    private Ingredient createNewIngredient() {
        if (ingredientType == null) {
            System.out.println("IngredientStorage di (" + posX + "," + posY +
                               ") belum di-set ingredientType-nya, tidak bisa spawn ingredient.");
            return null;
        }

        try {
            Ingredient newIng = ingredientType.getDeclaredConstructor().newInstance();
            if (newIng.getState() != IngredientState.RAW) {
                newIng.setState(IngredientState.RAW);
            }
            return newIng;
        } catch (Exception e) {
            System.out.println("Gagal membuat instance ingredient untuk storage " + ingredientName);
            e.printStackTrace();
            return null;
        }
    }

    public Item takeItemFromTop() {
        Item temp = itemOnStation;
        itemOnStation = null;
        return temp;
    }

    public boolean placeItemOnTop(Item item) {
        if (itemOnStation != null) {
            return false;
        }
        itemOnStation = item;
        return true;
    }

    public boolean hasItemOnTop() {
        return itemOnStation != null;
    }

    public Class<? extends Ingredient> getIngredientType() {
        return ingredientType;
    }
    
    public String getIngredientName() {
        return ingredientName;
    }

    public boolean isValidIngredient(Item item) {
        if (!(item instanceof Ingredient ing)) {
            return false;
        }
        if (ingredientType == null || !ingredientType.isInstance(ing)) {
            // beda jenis (Tomato masuk ke Cheese storage, dsb)
            return false;
        }
        // hanya boleh yang masih RAW
        return ing.getState() == IngredientState.RAW;
    }


    public String getTooltip() {
        StringBuilder sb = new StringBuilder();
        sb.append(ingredientName != null ? ingredientName : "Unknown")
          .append(" Storage\n");
        sb.append("Stock: Unlimited (RAW)\n");

        if (itemOnStation != null) {
            sb.append("Item on top: ").append(itemOnStation.getName());
            if (itemOnStation instanceof Ingredient ing) {
                sb.append(" (").append(ing.getState()).append(")");
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("IngredientStorage{ingredient=%s, pos=(%d,%d), itemOnTop=%s}",
            ingredientName,
            posX, posY,
            itemOnStation != null ? itemOnStation.getName() : "none");
    }
}
