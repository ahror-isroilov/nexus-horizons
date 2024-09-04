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
}
