package main.entity;

import lombok.Getter;
import main.GameWindow;
import utils.Position;

import java.awt.*;
import java.util.UUID;

import static utils.Const.rand;

/**
 * author: ahror
 * <p>
 * since: 8/30/24
 */
public class Entity {
    @Getter private UUID id = UUID.randomUUID();
    @Getter protected Position position;
    protected Color color;

    public Entity(Position position, Color color) {
        this.id = UUID.randomUUID();
        this.position = position;
        this.color = color;
    }

    public Entity(UUID id) {
        this.id = UUID.randomUUID();
        this.position = new Position((int) (rand.nextFloat() * GameWindow.getScreenWidth()), (int) (rand.nextFloat() * GameWindow.getScreenHeight()));
        this.color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    public Entity(Position position) {
        this.id = UUID.randomUUID();
        this.position = position;
        this.color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256), 129);
    }
}
