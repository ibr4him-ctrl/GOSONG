package model.chef;

import model.item.Item;

public class ChefInventory {
    private Item heldItem;  // Hanya 1 item yang bisa dipegang
    
    public ChefInventory() {
        this.heldItem = null;
    }
    
    // Method untuk menambah item (override konsep add)
    public boolean addItem(Item item) {
        if (heldItem == null) {
            heldItem = item;
            return true;
        }
        return false;  // Gagal karena sudah ada item
    }
    
    // Method untuk mengambil dan menghapus item (override konsep remove)
    public Item removeItem() {
        Item temp = heldItem;
        heldItem = null;
        return temp;
    }
    
    // Method untuk cek apakah ada item
    public boolean hasItem() {
        return heldItem != null;
    }
    
    // Method untuk mendapatkan item tanpa menghapus
    public Item getHeldItem() {
        return heldItem;
    }
    
    public void setHeldItem(Item item) {
        this.heldItem = item;
    }
    // Method untuk clear inventory
    public void clear() {
        heldItem = null;
    }
    
    // Method untuk replace item (override konsep swap)
    public Item swapItem(Item newItem) {
        Item oldItem = heldItem;
        heldItem = newItem;
        return oldItem;
    }
    
    // Override toString untuk debugging
    @Override
    public String toString() {
        if (heldItem != null) {
            return "Holding: " + heldItem.getName();
        }
        return "Empty hands";
    }
    
    // Override equals untuk perbandingan
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ChefInventory other = (ChefInventory) obj;
        
        if (heldItem == null && other.heldItem == null) return true;
        if (heldItem == null || other.heldItem == null) return false;
        
        return heldItem.equals(other.heldItem);
    }
    
    // Override hashCode
    @Override
    public int hashCode() {
        return heldItem != null ? heldItem.hashCode() : 0;
    }
}
