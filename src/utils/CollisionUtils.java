package utils;

import main.entity.Bullet;
import main.entity.Enemy;

/**
 * author: ahror
 * <p>
 * since: 9/3/24
 */
public final class CollisionUtils {

    public static boolean checkSweptAABBCollision(Enemy enemy, Bullet bullet) {
        float bulletWidth = bullet.getWIDTH();
        float bulletHeight = bullet.getLENGTH();
        float enemySize = enemy.getSize() / 2;

        float xInvEntry, yInvEntry;
        float xInvExit, yInvExit;

        if (bullet.getVelocityX() > 0.0f) {
            xInvEntry = enemy.getPosition().x() - (bullet.getPosition().x() + bulletWidth);
            xInvExit = (enemy.getPosition().x() + enemySize) - bullet.getPosition().x();
        } else {
            xInvEntry = (enemy.getPosition().x() + enemySize) - bullet.getPosition().x();
            xInvExit = enemy.getPosition().x() - (bullet.getPosition().x() + bulletWidth);
        }

        if (bullet.getVelocityY() > 0.0f) {
            yInvEntry = enemy.getPosition().y() - (bullet.getPosition().y() + bulletHeight);
            yInvExit = (enemy.getPosition().y() + enemySize) - bullet.getPosition().y();
        } else {
            yInvEntry = (enemy.getPosition().y() + enemySize) - bullet.getPosition().y();
            yInvExit = enemy.getPosition().y() - (bullet.getPosition().y() + bulletHeight);
        }

        // Find time of collision and time of leaving for each axis
        float xEntry, yEntry;
        float xExit, yExit;

        if (bullet.getVelocityX() == 0.0f) {
            xEntry = Float.NEGATIVE_INFINITY;
            xExit = Float.POSITIVE_INFINITY;
        } else {
            xEntry = xInvEntry / bullet.getVelocityX();
            xExit = xInvExit / bullet.getVelocityX();
        }

        if (bullet.getVelocityY() == 0.0f) {
            yEntry = Float.NEGATIVE_INFINITY;
            yExit = Float.POSITIVE_INFINITY;
        } else {
            yEntry = yInvEntry / bullet.getVelocityY();
            yExit = yInvExit / bullet.getVelocityY();
        }

        // Find the earliest/latest times of collision
        float entryTime = Math.max(xEntry, yEntry);
        float exitTime = Math.min(xExit, yExit);

        // If there's no collision
        return !(entryTime > exitTime) && (!(xEntry < 0.0f) || !(yEntry < 0.0f)) && !(xEntry > 1.0f) && !(yEntry > 1.0f);
    }

    public static boolean rayTraceCollision(Enemy enemy, Bullet bullet) {
        if (!bullet.isFired()) return false;
        // Start position of the bullet
        float x1 = bullet.getPreviousPosition().x();
        float y1 = bullet.getPreviousPosition().y();

        // End position of the bullet
        float x2 = bullet.getPosition().x();
        float y2 = bullet.getPosition().y();

        // Enemy circle
        float cx = enemy.getPosition().x() + enemy.getSize() / 2;
        float cy = enemy.getPosition().y() + enemy.getSize() / 2;
        float radius = enemy.getSize() / 2;

        // Vector representing bullet's direction
        float dx = x2 - x1;
        float dy = y2 - y1;

        // Normalize the direction vector
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length > 0) {
            dx /= length;
            dy /= length;
        }

        // Vector from bullet start to circle center
        float ex = cx - x1;
        float ey = cy - y1;

        // Project e onto d
        float t = ex * dx + ey * dy;

        // Find the closest point on the line to the circle center
        float closestX = x1 + t * dx;
        float closestY = y1 + t * dy;

        // Check if the closest point is within the circle
        float distanceX = closestX - cx;
        float distanceY = closestY - cy;
        float distanceSquared = distanceX * distanceX + distanceY * distanceY;

        if (distanceSquared <= radius * radius) {
            // Check if the collision point is "behind" the bullet's current position
            float bulletToCollisionX = closestX - x2;
            float bulletToCollisionY = closestY - y2;

            // If the dot product is positive, the collision point is in front of the bullet
            if (bulletToCollisionX * dx + bulletToCollisionY * dy >= 0) {
                return true;
            }
        }

        return false;
    }
}
