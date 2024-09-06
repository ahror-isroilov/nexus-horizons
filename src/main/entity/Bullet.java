package main.entity;

import lombok.Getter;
import multiplayer.BulletState;
import utils.Position;

import java.awt.*;

/**
 * author: ahror
 * <p>
 * since: 9/2/24
 */
@Getter
public class Bullet extends Entity {
    private float velocityX, velocityY;
    private final float LENGTH = 10.0f;
    private final float WIDTH = 1.0f;
    private final float DAMAGE = 15.0f;

    public static final float BULLET_SPEED = 180.0f;
    public static final float BULLET_SPREAD = 0.025f;

    private boolean isFired = false;
    private Position previousPosition;

    public Bullet(int x, int y, float velocityX, float velocityY) {
        super(new Position(x, y), Color.GREEN);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public void update() {
        this.previousPosition = new Position(position.x(), position.y());
        position.incX(velocityX);
        position.incY(velocityY);

        float DRAG_COEFFICIENT = 0.99f;
        velocityX *= DRAG_COEFFICIENT;
        velocityY *= DRAG_COEFFICIENT;
    }

    public void draw(Graphics2D g2d) {
        isFired = true;
        g2d.setColor(color);

        // Calculate the angle of the bullet's trajectory
        double angle = Math.atan2(velocityY, velocityX);

        // Calculate end point of the line
        float endX = position.x() - LENGTH * (float) Math.cos(angle);
        float endY = position.y() - LENGTH * (float) Math.sin(angle);

        // Set the stroke for a bold line
        g2d.setStroke(new BasicStroke(WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Draw the line
        g2d.drawLine((int) position.x(), (int) position.y(), (int) endX, (int) endY);

        // Reset the stroke to default
        g2d.setStroke(new BasicStroke());
    }

    public boolean isOutOfBounds(int screenWidth, int screenHeight) {
        return position.x() < -LENGTH || position.x() > screenWidth + LENGTH || position.y() < -LENGTH || position.y() > screenHeight + LENGTH;
    }

    public BulletState createState() {
        return new BulletState(position, new BulletState.Velocity(velocityX, velocityY));
    }
}