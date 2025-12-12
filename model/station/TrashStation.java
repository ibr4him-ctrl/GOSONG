// model/station/TrashStation.java
package model.station;

import model.chef.Chef;
import model.enums.ItemType;
import model.item.Item;
import model.item.utensils.Plate;
import util.SoundEffectPlayer;

public class TrashStation extends Station {

    private boolean hasTrash = false;

    // ===== SFX =====
    private static final SoundEffectPlayer SFX = new SoundEffectPlayer();
    private static final String SFX_TRASH =
            "/resources/game/sound_effect/trashcan_sound.wav";

    public TrashStation(int x, int y) {
        super(x, y, "Trash");
    }

    @Override
    public String getSymbol() {
        return "T";
    }

    // === DIPAKAI DI GamePanel ===
    public boolean isFull() {
        return hasTrash;
    }

    // optional kalau nanti mau dikosongin
    public void empty() {
        hasTrash = false;
    }

    @Override
    public boolean interact(Chef chef) {
        if (!isAdjacentTo(chef)) return false;
        
        Item hand = chef.getHeldItem();
        if (hand == null) return false;
        
        //prioritas plate dulu 
        if (hand instanceof Plate plate){
            if (plate.isEmpty()){
                System.out.println("Gagal: Tidak bisa membuang piring kosong!");
                return false;
            }
            plate.clear();
            plate.setClean(false);
            hasTrash = true;
            SFX.playOnce(SFX_TRASH); 
            System.out.println("Isi piring dibuang. Piring tetap di tangan (kotor).");
            return true; 
        }

        if (hand.getItemType() == ItemType.KITCHEN_UTENSIL) {
            System.out.println("Gagal: tidak dapat membuang kitchen utensil");
            return false;
        }


        //     if (hand instanceof Oven oven) {
        //         oven.clearContents(); // kamu sudah punya clearContents() di Oven
        //         // utensil tetap di tangan Chef
        //         return true;
        //     }
        //     // untuk utensil lain, nanti bisa dibuat method clear sendiri
        //     return false;
            //Utensil lain sementara : gagal dibuang 

        // ===== BUKAN kitchen utensil → item-nya dibuang =====
        chef.setHeldItem(null);
        hasTrash = true;
        SFX.playOnce(SFX_TRASH);
        System.out.println(hand.getName() + " dibuang ke tempat sampah.");
        return true;
    }
}