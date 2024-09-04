package main.entity;

import lombok.Getter;
import utils.Position;

import java.awt.*;

/**
 * author: ahror
 * <p>
 * since: 9/2/24
 */
public class Bullet extends Entity {
    @Getter private float velocityX, velocityY;
    @Getter private final float LENGTH = 20.0f;
    @Getter private final float WIDTH = 1.5f;
    private final float DRAG_COEFFICIENT = 0.99f;
    @Getter private final float DAMAGE = 25.0f;
    @Getter private boolean isFired = false;
    @Getter private Position previousPosition;

    public Bullet(int x, int y, float velocityX, float velocityY) {
        super(new Position(x, y), Color.GREEN);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public void update() {
        this.previousPosition = new Position(position.x(), position.y());
        position.incX(velocityX);
        position.incY(velocityY);
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
}