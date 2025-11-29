package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * MapLoader bertugas membaca file map (biasanya .txt) dan mengubahnya menjadi
 * grid 2D yang bisa dipakai game.
 *
 * Setiap karakter merepresentasikan object di world:
 *  - '#' = tembok
 *  - '.' = lantai kosong
 *  - 'P' = posisi pemain
 *  - 'C' = counter / meja
 *  - dll (tergantung spesifikasi game)
 */
public class MapLoader {

    public static char[][] loadMap(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            int rowCount = 0;
            String line;
            while ((line = br.readLine()) != null) {
                rowCount++;
            }

            BufferedReader br2 = new BufferedReader(new FileReader(filePath));

            char[][] grid = new char[rowCount][];

            int row = 0;
            while ((line = br2.readLine()) != null) {
                grid[row] = line.toCharArray();
                row++;
            }

            return grid;

        } catch (IOException e) {
            GameLogger.error("Gagal memuat map: " + e.getMessage());
            return null;
        }
    }

    public static void printMap(char[][] map) {
        if (map == null) return;

        for (char[] row : map) {
            System.out.println(new String(row));
        }
    }
}
