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
        float enemySize = enemy.getSize();

        float x1 = bullet.getPreviousPosition().x();
        float y1 = bullet.getPreviousPosition().y();
        float x2 = bullet.getPosition().x();
        float y2 = bullet.getPosition().y();

        float ex = enemy.getPosition().x();
        float ey = enemy.getPosition().y();

        // Compute the minimum and maximum points of the bullet's path
        float minX = Math.min(x1, x2);
        float maxX = Math.max(x1, x2);
        float minY = Math.min(y1, y2);
        float maxY = Math.max(y1, y2);

        // Check if the bullet's path overlaps with the enemy's bounding box
        if (maxX < ex || minX > ex + enemySize || maxY < ey || minY > ey + enemySize) {
            return false;
        }

        float dx = x2 - x1;
        float dy = y2 - y1;

        // Parameterize the enemy's sides
        float[] txs = new float[2];
        float[] tys = new float[2];

        // Check X axis
        if (dx != 0) {
            txs[0] = (ex - x1 - bulletWidth) / dx;
            txs[1] = (ex + enemySize - x1) / dx;
            if (txs[0] > txs[1]) {
                float temp = txs[0];
                txs[0] = txs[1];
                txs[1] = temp;
            }
        } else {
            txs[0] = Float.NEGATIVE_INFINITY;
            txs[1] = Float.POSITIVE_INFINITY;
        }

        // Check Y axis
        if (dy != 0) {
            tys[0] = (ey - y1 - bulletHeight) / dy;
            tys[1] = (ey + enemySize - y1) / dy;
            if (tys[0] > tys[1]) {
                float temp = tys[0];
                tys[0] = tys[1];
                tys[1] = temp;
            }
        } else {
            tys[0] = Float.NEGATIVE_INFINITY;
            tys[1] = Float.POSITIVE_INFINITY;
        }

        // Find the intersection of these ranges
        float tMin = Math.max(txs[0], tys[0]);
        float tMax = Math.min(txs[1], tys[1]);

        // Check if this intersection exists and is within [0, 1]
        return tMax >= tMin && tMin <= 1 && tMax >= 0;
    }
}
