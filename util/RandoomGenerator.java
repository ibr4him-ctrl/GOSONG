package util;

import java.util.List;
import java.util.Random;

/**
 * RandoomGenerator
 * Utility class untuk semua kebutuhan angka dan pemilihan random dalam game.
 * 
 * Bisa digunakan untuk:
 *  - Random memilih recipe
 *  - Random menentukan waktu order
 *  - Random menentukan posisi spawn (kalau perlu)
 *  - Random integer dalam range
 */
public class RandoomGenerator {

    private static final Random random = new Random();

    public static int nextInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min tidak boleh lebih besar dari max");
        }
        return random.nextInt(max - min + 1) + min;
    }

    public static <T> T chooseRandom(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        int index = random.nextInt(list.size());
        return list.get(index);
    }

    public static boolean randomBoolean() {
        return random.nextBoolean();
    }

    public static double randomProbability() {
        return random.nextDouble();
    }

    public static void setSeed(long seed) {
        random.setSeed(seed);
    }
}
