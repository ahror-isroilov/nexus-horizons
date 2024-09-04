package multiplayer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import utils.Position;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * author: ahror
 * <p>
 * since: 9/4/24
 */
@Getter
@Setter
@AllArgsConstructor
public class PlayerState {
    private Position position;
    private int currentBullets;
    private boolean isGravityEnabled;
    private boolean isLaserActive;
    private Point2D.Float currentEyeDirection;
    private Point2D.Float targetEyeDirection;
    private Point2D.Float velocity;
    private float verticalVelocity;
    private List<BulletState> bullets;
}
