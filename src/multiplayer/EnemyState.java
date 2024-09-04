package multiplayer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import utils.Position;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.UUID;

/**
 * author: ahror
 * <p>
 * since: 9/4/24
 */
@Getter
@Setter
@AllArgsConstructor
public class EnemyState {
    private UUID id;
    private Position position;
    private float health;
    private boolean isDead;
    private Point2D.Float velocity;
    private Point2D.Float target;
    private float explosionTime;
    List<Point2D.Float> explosionParticles;
}
