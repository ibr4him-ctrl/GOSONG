package controller;

import src.GUI.KeyHandler;

public class SwitchChef{

    private boolean wasPressed = false;

    /**
     * Mengecek apakah tombol Switch (B) ditekan.
     * Mengembalikan true hanya pada frame pertama tombol ditekan.
     */
    public boolean execute(KeyHandler keyH) {
        // Cek tombol B (sesuai spesifikasi) atau TAB (opsional)
        boolean isPressed =  keyH.tabPressed;// || keyH.bPressed  keyH.tabPressed;

        if (isPressed && !wasPressed) {
            wasPressed = true;
            return true; // Trigger switch!
        }

        if (!isPressed) {
            wasPressed = false; // Reset saat tombol dilepas
        }

        return false;
    }
}
