package model.enums;

/**
 * Enum untuk merepresentasikan state permainan.
 * Digunakan oleh GameController dan semua subsistem untuk mengetahui status game.
 */
public enum GameState {
    PLAYING,      // Game sedang berjalan
    PAUSED,       // Game di-pause
    GAME_OVER,    // Game berakhir (bisa PASS atau FAIL)
    MENU          // Di menu utama
}
