package util;

import java.awt.Rectangle;
import java.util.List;

/**
 * CollisionDetector bertugas memastikan Chef hanya bergerak ke area yang boleh diinjak.
 * Dipakai sebelum movement:
 * 
 * if (CollisionDetector.canMove(nextBox, forbiddenZones)) {
 *      chef.move();
 * }
 */
public class CollisionDetector {

    public static boolean isColliding(Rectangle a, Rectangle b) {
        if (a == null || b == null) return false;
        return a.intersects(b);
    }

    public static boolean collidesWithForbidden(Rectangle nextPos, List<Rectangle> forbiddenAreas) {
        if (nextPos == null || forbiddenAreas == null) return false;

        for (Rectangle blocked : forbiddenAreas) {
            if (nextPos.intersects(blocked)) {
                return true; // AREA TERLARANG → GERAKAN DICEGAH
            }
        }
        return false;
    }

    public static boolean canMove(Rectangle nextPos, List<Rectangle> forbiddenAreas) {
        return !collidesWithForbidden(nextPos, forbiddenAreas);
    }

    public static boolean inInteractionRange(Rectangle chef, Rectangle station, int range) {
        if (chef == null || station == null) return false;

        Rectangle expanded = new Rectangle(
            station.x - range,
            station.y - range,
            station.width + (range * 2),
            station.height + (range * 2)
        );

        return chef.intersects(expanded);
    }
}
