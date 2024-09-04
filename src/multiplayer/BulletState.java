package multiplayer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import utils.Position;

import java.awt.*;

/**
 * author: ahror
 * <p>
 * since: 9/4/24
 */
@Getter
@Setter
public class BulletState {
    private Position position;
    private Velocity velocity;

    // Constructor
    public BulletState(Position position, Velocity velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    @AllArgsConstructor
    @Getter
    public static class Velocity {
        private float x;
        private float y;
    }
}
