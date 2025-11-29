package util;

public final class Constants {

    public static final int TILE_SIZE = 32;  
    public static final float COLLISION_EPSILON = 0.1f;
    public static final float PLAYER_SPEED = 2.5f;  

    public static final float UP_X = 0f;
    public static final float UP_Y = -1f;

    public static final float DOWN_X = 0f;
    public static final float DOWN_Y = 1f;

    public static final float LEFT_X = -1f;
    public static final float LEFT_Y = 0f;

    public static final float RIGHT_X = 1f;
    public static final float RIGHT_Y = 0f;

    // Batas game world 
    public static final int WORLD_WIDTH = 25;   
    public static final int WORLD_HEIGHT = 20;  

    // Prevent instantiation
    private Constants() {}
}
