package model.item.utensils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.enums.IngredientState; 
import model.enums.ItemLocation;
import model.enums.ItemType; 
import model.interfaces.CookingDevice;
import model.item.Item;
import model.item.Preparable; 
import model.item.ingredient.Ingredient;
import model.item.ingredient.pizza.Dough;
import util.SoundEffectPlayer;

public class Oven extends Item implements CookingDevice{
    //kapaitas maksimum ingredient yang bisa dipanggang 
    
    private final int maxCapacity; 
    

    // Isi oven sekarang (bisa ingredient pizza)
    private final List<Preparable> contents; 

    //Status masak 
    private boolean cooking; 
    private double cookTimeSeconds; 
    private boolean burned; 


    //waktu referensi 
    private static final double COOK_TIME_DONE = 12.0; 
    private static final double COOK_TIME_BURNED = 24.0; 


    // ===== SFX =====
    private static final SoundEffectPlayer SFX = new SoundEffectPlayer();

    private static final String SFX_OVEN_3S =
            "/resources/game/sound_effect/oven_timer_3secondsleft.wav";
    private static final String SFX_OVEN_WORKING =
            "/resources/game/sound_effect/oven_working.wav";
    private static final String SFX_OVEN_BURN =
            "/resources/game/sound_effect/warning_sound.wav";

    // 3 detik sebelum DONE (DONE=12, jadi 9)
    private static final double WARN_3S_AT = COOK_TIME_DONE - 3.0;

    private boolean played3sWarning = false;
    private boolean playedBurnWarning = false;

    // key unik buat loop oven ini
    private final String workingLoopKey = "OVEN_WORKING_" + System.identityHashCode(this);

    public Oven(){
        this(ItemLocation.KITCHEN_UTENSILS, 4); 
    }

    public Oven(ItemLocation location, int maxCapacity){
        super("Oven", ItemType.KITCHEN_UTENSIL,location); 
        this.maxCapacity = maxCapacity; 
        this.contents = new ArrayList<>(); 
        this.cooking = false;             //lagi masak atau tidak 
        this.cookTimeSeconds = 0.0; 
        this.burned = false; 

        setClean(true); 
        setEdible(false); 
    }


    //Cooking Device 
    @Override
    public boolean isPortable(){
        return false; 
    }

    @Override
    public int capacity(){
        return maxCapacity; 
    }

    @Override
    //Ini lebih ke validasi apa yang boleh masuk 
    public boolean canAccept(Preparable ingredient){
        if (ingredient == null){return false;}

        if(!(ingredient instanceof Ingredient)) return false; 

        if (cooking) return false; 

        if (contents.size() >= maxCapacity) return false; 

        Ingredient ing = (Ingredient) ingredient; 
        IngredientState state = ing.getState();
        
        if (state == IngredientState.RAW) {
            System.out.println("[Oven] Menolak ingredient RAW: " + ing.getName());
            return false;
        } 

        return ing.canBeCooked(); 
    }

    @Override
    // Masuk ke ingredient ke oven 
    public void addIngredient(Preparable ingredient){
        if (!canAccept(ingredient)){
            throw new IllegalStateException("Oven tidak dapat menerima ingredient ini");
        }

        if (ingredient instanceof Dough) {
            // optional: cegah 2 dough
            for (Preparable p : contents) {
                if (p instanceof Dough) {
                    throw new IllegalStateException("Sudah ada Dough di oven");
                }
            }
            contents.add(0, ingredient); // Dough selalu di depan
        } else {
            contents.add(ingredient);
        }
    }

    @Override
    // Ini mulai masak 
    public void startCooking(){
        if (contents.isEmpty()){
            throw new IllegalStateException("Oven kosong, tidak bisa mulai memasak"); 
        }

        cooking = true; 
        cookTimeSeconds = 0.0; 
        burned = false; 

        SFX.playLoop(workingLoopKey, SFX_OVEN_WORKING);
    }

    public void update(double deltaSeconds){
        if (!cooking) return;

        double prevTime = cookTimeSeconds;
        cookTimeSeconds += deltaSeconds;

        // bunyi pas masuk 3 detik terakhir sebelum COOKED (melewati 9 detik)
        if (!played3sWarning && prevTime < WARN_3S_AT && cookTimeSeconds >= WARN_3S_AT) {
            SFX.playOnce(SFX_OVEN_3S);
            played3sWarning = true;
        }

        // burned (melewati 24 detik)
        if (prevTime < COOK_TIME_BURNED && cookTimeSeconds >= COOK_TIME_BURNED){
            for (Preparable p : contents){
                if (p instanceof Ingredient){
                    ((Ingredient) p).burn();
                }
            }
            burned = true;
            cooking = false;

            // stop working loop + bunyi warning burned sekali
            SFX.stopLoop(workingLoopKey);
            if (!playedBurnWarning) {
                SFX.playOnce(SFX_OVEN_BURN);
                playedBurnWarning = true;
            }
            return;
        }

        // cooked (>=12 detik). NOTE: kamu memang biarin tetap "cooking"
        // supaya bisa lanjut sampai 24 detik dan burn kalau kelamaan.
        if (cookTimeSeconds >= COOK_TIME_DONE){
            for (Preparable p : contents){
                if (p instanceof Ingredient){
                    Ingredient ing = (Ingredient) p;
                    if (ing.getState() != IngredientState.BURNED && ing.getState() != IngredientState.COOKED){
                        ing.cook();
                    }
                }
            }
        }
    }

    //More helper
    public boolean isCooking() {
        return cooking;
    }

    public boolean isBurned() {
        return burned;
    }

    public double getCookTimeSeconds() {
        return cookTimeSeconds;
    }

    public boolean isEmpty() {
        return contents.isEmpty();
    }
    
    public boolean isReadyToTakeOut() {
        return !contents.isEmpty() && cookTimeSeconds >= COOK_TIME_DONE;
    }


    public List<Preparable> getContents() {
        return Collections.unmodifiableList(contents);
    }

    // Mengeluarkan seluruh isi oven setelah proses memakai selesai 
    // Harus dipanggil ketika oven TIDAK sedang memasak

    public List<Preparable> takeOutAll(){
        if (cooking && cookTimeSeconds < COOK_TIME_DONE){
            throw new IllegalStateException("Oven masih memasak"); 
        }

        List<Preparable> result = new ArrayList<>(contents); 
        contents.clear(); 
        cooking = false; 
        burned = false; 
        cookTimeSeconds = 0.0; 
        SFX.stopLoop(workingLoopKey);
        played3sWarning = false;
        playedBurnWarning = false;

        return result;
    }

    public void clearContents(){
        contents.clear(); 
        cooking = false; 
        burned = false; 
        cookTimeSeconds = 0.0; 

        SFX.stopLoop(workingLoopKey);
        played3sWarning = false;
        playedBurnWarning = false;
    }

    // walaupun udah 12 detik, nilai tetap 1.0 meski bisa gosong di 24s 

    public double getProgressRatio(){
        if (!cooking && cookTimeSeconds == 0.0) return 0.0; 
        double ratio = cookTimeSeconds / COOK_TIME_DONE; 
        return Math.min(1.0, ratio); 
    }

    public int getProgressPercent(){
        return (int) Math.round(getProgressRatio() * 100.0); 
    }
    public String getProgressBar(int width){
        double ratio = getProgressRatio(); 
        int filled = (int) Math.round(ratio*width); 
        if (filled > width) filled = width; 

        StringBuilder sb = new StringBuilder();

        sb.append('['); 
        for (int i = 0; i < width; i++){
            sb.append(i < filled ? '#' : '-'); 
        }
        sb.append("] ").append(getProgressPercent()).append('%'); 
        return sb.toString(); 
    }

    @Override
    public String toString(){
        return "Oven(" + 
        "contents=" + contents.size() + 
        ", cooking=" + cooking + 
        ", burned=" + burned + 
        ", cookTime=" + cookTimeSeconds + ")";
    }
}