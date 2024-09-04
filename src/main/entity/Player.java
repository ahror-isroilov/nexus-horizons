package main.entity;

import lombok.Getter;
import main.GameWindow;
import utils.AnimationUtils;
import utils.AudioUtils;
import utils.Const;
import utils.Position;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static utils.Planets.currentGravity;

public class Player extends Entity implements PlayerMovement {
    // Constants
    private static final float GRAVITY_SCALE = 1;
    private static final float VELOCITY_THRESHOLD = 0.1f;
    private static final float POSITION_THRESHOLD = 0.1f;
    private static final float BOUNCE_SOUND_THRESHOLD = 0.5f;
    private static final float EYE_MOVEMENT_SPEED = 0.2f;
    private static final float ACCELERATION = 0.4f;
    private static final float DECELERATION = 0.0000001f;
    private static final float APPROACH_SPEED = 0.2f;
    private static final float BULLET_SPEED = 90.0f;
    private static final float BULLET_SPREAD = 0.025f; // Angle in radians
    private static final int FLASH_DURATION = 100; // milliseconds

    @Getter private final Float diameter;
    @Getter private final float mass;
    @Getter private final float maxSpeed = 40.0f;

    private long flashStartTime;
    private boolean isFlashing;
    @Getter private List<Bullet> bullets;
    @Getter private int maxBullets = 50;
    @Getter private int currentBullets;
    private long lastFireTime;
    private static final long FIRE_COOLDOWN = 100; // milliseconds
    private boolean isFiring = false;

    private Point2D.Float velocity;
    private final Point2D.Float desiredVelocity;
    private float verticalVelocity = 0;
    @Getter private boolean gravityEnabled = false;
    private boolean isApproachingTarget = false;
    private Position targetPosition;

    private boolean justBounced = false;
    private boolean isAtRest = false;

    private final Point2D.Float currentEyeDirection;
    private final Point2D.Float targetEyeDirection;

    private Point mousePosition;
    private boolean isMouseInWindow = false;
    @Getter private boolean laserActive = false;
    private BufferedImage shadowImage;
    private Graphics2D shadowGraphics;

    public Player(float diameter, Color color, float mass) {
        super(new Position(), color);
        this.mousePosition = new Point(0, 0);
        this.bullets = new ArrayList<>();
        this.currentBullets = maxBullets;
        this.diameter = diameter;
        this.mass = mass;
        this.velocity = new Point2D.Float(0, 0);
        this.desiredVelocity = new Point2D.Float(0, 0);
        this.currentEyeDirection = new Point2D.Float(0, 0);
        this.targetEyeDirection = new Point2D.Float(0, 0);
        initializeShadowImage();
        locateDefaultPosition();
    }

    private void initializeShadowImage() {
        shadowImage = new BufferedImage((int) (diameter + 60), (int) (diameter + 60), BufferedImage.TYPE_INT_ARGB);
        shadowGraphics = shadowImage.createGraphics();
    }

    public void update() {
        if (isApproachingTarget) approachTarget();
        else if (gravityEnabled) {
            applyGravity();
            checkIfAtRest();
        } else {
            updateVelocity();
            movePlayer();
        }
        wrapPosition();
    }

    // Movement methods
    private void updateVelocity() {
        velocity.x = updateVelocityComponent(desiredVelocity.x, velocity.x);
        velocity.y = updateVelocityComponent(desiredVelocity.y, velocity.y);
        velocity = limitVector(velocity, maxSpeed);
    }

    private float updateVelocityComponent(float desiredComponent, float currentComponent) {
        if (desiredComponent != 0) {
            return currentComponent + Math.signum(desiredComponent) * ACCELERATION;
        } else {
            float newComponent = currentComponent * 0.95f;
            return Math.abs(newComponent) < DECELERATION ? 0 : newComponent;
        }
    }

    private void movePlayer() {
        position.incX(velocity.x);
        position.incY(velocity.y);
    }

    private void applyGravity() {
        float acceleration = currentGravity * GRAVITY_SCALE;
        verticalVelocity += acceleration;
        position.incY(verticalVelocity);

        handleBounce();
        updateHorizontalVelocity();
    }

    private void handleBounce() {
        if (position.y() + diameter > GameWindow.getScreenHeight()) {
            position.y(GameWindow.getScreenHeight() - diameter);
            float bounceCoefficient = 0.4f;
            verticalVelocity = -verticalVelocity * bounceCoefficient;

            if (!justBounced && Math.abs(verticalVelocity) > BOUNCE_SOUND_THRESHOLD && !isAtRest) {
                AudioUtils.play("drop.wav");
                justBounced = true;
            }
        } else {
            justBounced = false;
        }
    }

    private void updateHorizontalVelocity() {
        if (desiredVelocity.x != 0) {
            velocity.x += Math.signum(desiredVelocity.x) * ACCELERATION;
        } else {
            velocity.x *= 0.95f;
            if (Math.abs(velocity.x) < DECELERATION) velocity.x = 0;
        }
        velocity = limitVector(velocity, maxSpeed);
        position.incX(velocity.x);
    }

    private void checkIfAtRest() {
        boolean isCloseToGround = Math.abs((position.y() + diameter) - GameWindow.getScreenHeight()) < POSITION_THRESHOLD;
        boolean hasLowVelocity = Math.abs(verticalVelocity) < VELOCITY_THRESHOLD;
        isAtRest = isCloseToGround && hasLowVelocity;

        if (isAtRest) {
            verticalVelocity = 0;
            position.y(GameWindow.getScreenHeight() - diameter);
        }
    }

    private void wrapPosition() {
        position.x((position.x() + GameWindow.getScreenWidth()) % GameWindow.getScreenWidth());
        position.y((position.y() + GameWindow.getScreenHeight()) % GameWindow.getScreenHeight());
    }

    private void approachTarget() {
        float dx = targetPosition.x() - position.x();
        float dy = targetPosition.y() - position.y();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 1.0f) {
            position.incX(dx * APPROACH_SPEED);
            position.incY(dy * APPROACH_SPEED);
        } else {
            reachTarget();
        }
    }

    private void reachTarget() {
        position.setCoordinates(targetPosition.x(), targetPosition.y());
        isApproachingTarget = false;
        velocity.x = 0;
        velocity.y = 0;
        desiredVelocity.x = 0;
        desiredVelocity.y = 0;
        targetPosition = null;
        targetEyeDirection.x = 0;
        targetEyeDirection.y = 0;
    }

    // Rendering methods
    public void spawn(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawBallWithShadowAndEye(g2d, position.x(), position.y(), color);
        drawWrappedInstances(g2d);
        drawLaser(g2d);

        if (isFlashing) {
            AnimationUtils.drawMuzzleFlash(g2d, diameter, position, currentEyeDirection, flashStartTime, FLASH_DURATION);
        }
        drawBullets(g2d);
        update();
    }

    private void drawWrappedInstances(Graphics2D g2d) {
        if (position.x() + diameter > GameWindow.getScreenWidth()) {
            drawBallWithShadowAndEye(g2d, position.x() - GameWindow.getScreenWidth(), position.y(), color);
        }
        if (position.x() < 0) {
            drawBallWithShadowAndEye(g2d, position.x() + GameWindow.getScreenWidth(), position.y(), color);
        }
        if (position.y() + diameter > GameWindow.getScreenHeight()) {
            drawBallWithShadowAndEye(g2d, position.x(), position.y() - GameWindow.getScreenHeight(), color);
        }
        if (position.y() < 0) {
            drawBallWithShadowAndEye(g2d, position.x(), position.y() + GameWindow.getScreenHeight(), color);
        }
    }

    private void drawBallWithShadowAndEye(Graphics2D g2d, float x, float y, Color color) {
        drawShadow(g2d, x, y);
        drawMainBody(g2d, x, y, color);
        drawEye(g2d, x, y);
    }

    private void drawShadow(Graphics2D g2d, float x, float y) {
        shadowGraphics.fill(new RoundRectangle2D.Double(30, 30, diameter, diameter, 80, 80));
        float[] matrix = new float[900];
        Arrays.fill(matrix, 0.1f / matrix.length);
        ConvolveOp op = new ConvolveOp(new Kernel(30, 30, matrix), ConvolveOp.EDGE_NO_OP, null);
        BufferedImage tempShadow = op.filter(shadowImage, null);
        g2d.drawImage(tempShadow, (int) x - 30, (int) y - 30, null);
    }

    private void drawMainBody(Graphics2D g2d, float x, float y, Color color) {
        g2d.setColor(color);
        g2d.fillOval((int) x, (int) y, diameter.intValue(), diameter.intValue());
    }

    private void drawEye(Graphics2D g2d, float x, float y) {
        float eyeSize = diameter / 4;
        float ballCenterX = x + diameter / 2;
        float ballCenterY = y + diameter / 2;
        updateEyeDirection(ballCenterX, ballCenterY);
        float eyeX = ballCenterX + currentEyeDirection.x * (diameter / 2);
        float eyeY = ballCenterY + currentEyeDirection.y * (diameter / 2);

        g2d.setColor(new Color(0, 235, 255, 237));
        g2d.fillOval((int) (eyeX - eyeSize / 2), (int) (eyeY - eyeSize / 2), (int) eyeSize, (int) eyeSize);
    }

    private void updateEyeDirection(float ballCenterX, float ballCenterY) {
        if (isMouseInWindow && mousePosition != null) {
            updateEyeDirectionForMouse(ballCenterX, ballCenterY);
        } else if (isApproachingTarget) {
            updateEyeDirectionForTarget(ballCenterX, ballCenterY);
        } else if (gravityEnabled) {
            targetEyeDirection.x = velocity.x;
            targetEyeDirection.y = verticalVelocity;
        }

        currentEyeDirection.x += (targetEyeDirection.x - currentEyeDirection.x) * EYE_MOVEMENT_SPEED;
        currentEyeDirection.y += (targetEyeDirection.y - currentEyeDirection.y) * EYE_MOVEMENT_SPEED;

        normalizeEyeDirection();
    }

    private void updateEyeDirectionForTarget(float ballCenterX, float ballCenterY) {
        float dx = targetPosition.x() - ballCenterX;
        float dy = targetPosition.y() - ballCenterY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 1.0f) {
            targetEyeDirection.x = dx;
            targetEyeDirection.y = dy;
            normalizeVector(targetEyeDirection);
        } else {
            targetEyeDirection.x = 0;
            targetEyeDirection.y = 0;
            isApproachingTarget = false;
        }
    }

    private void updateEyeDirectionForMouse(float ballCenterX, float ballCenterY) {
        float dx = mousePosition.x - ballCenterX;
        float dy = mousePosition.y - ballCenterY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 1.0f) {
            targetEyeDirection.x = dx / distance;
            targetEyeDirection.y = dy / distance;
        } else {
            targetEyeDirection.x = 0;
            targetEyeDirection.y = 0;
        }
    }

    public void drawLaser(Graphics2D g2d) {
        if (laserActive) {
            g2d.setColor(new Color(255, 0, 0, 132));
            g2d.setStroke(new BasicStroke(1));
            float eyeX = position.x() + diameter / 2 + currentEyeDirection.x * (diameter / 2);
            float eyeY = position.y() + diameter / 2 + currentEyeDirection.y * (diameter / 2);
            g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{10f, 20f}, 0f));
            g2d.drawLine((int) eyeX, (int) eyeY, mousePosition.x, mousePosition.y);
        }
    }

    public void drawBullets(Graphics2D g2d) {
        if (isFiring) {
            fire();
        }

        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.update();
            bullet.draw(g2d);

            if (bullet.isOutOfBounds(GameWindow.getScreenWidth(), GameWindow.getScreenHeight())) {
                iterator.remove();
            }
        }
    }

    // Utility methods
    private void normalizeEyeDirection() {
        if (targetEyeDirection.x != 0 || targetEyeDirection.y != 0) {
            normalizeVector(currentEyeDirection);
        }
    }

    private void normalizeVector(Point2D.Float vector) {
        float length = (float) Math.sqrt(vector.x * vector.x + vector.y * vector.y);
        if (length > 0) {
            vector.x /= length;
            vector.y /= length;
        }
    }

    private Point2D.Float limitVector(Point2D.Float vector, float max) {
        float magnitudeSquared = vector.x * vector.x + vector.y * vector.y;
        if (magnitudeSquared > max * max) {
            float magnitude = (float) Math.sqrt(magnitudeSquared);
            vector.x = vector.x / magnitude * max;
            vector.y = vector.y / magnitude * max;
        }
        return vector;
    }

    public void toggleLaser() {
        laserActive = !laserActive;
    }

    public void fire() {
        if (currentBullets > 0 && System.currentTimeMillis() - lastFireTime > FIRE_COOLDOWN) {
            AudioUtils.play("shoot.wav");
            float bulletX = position.x() + diameter / 2;
            float bulletY = position.y() + diameter / 2;

            // Calculate direction vector from player to mouse
            float dirX = mousePosition.x - bulletX;
            float dirY = mousePosition.y - bulletY;

            // Normalize the direction vector
            float length = (float) Math.sqrt(dirX * dirX + dirY * dirY);
            if (length != 0) {
                dirX /= length;
                dirY /= length;
            }

            // Add slight randomness to bullet direction
            float angle = (Const.rand.nextFloat() - 0.5f) * BULLET_SPREAD;
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            float newDirX = dirX * cos - dirY * sin;
            float newDirY = dirX * sin + dirY * cos;

            // Set bullet velocity
            float bulletVelocityX = newDirX * BULLET_SPEED;
            float bulletVelocityY = newDirY * BULLET_SPEED;

            Bullet bullet = new Bullet((int) bulletX, (int) bulletY, bulletVelocityX, bulletVelocityY);
            bullets.add(bullet);
            currentBullets--;
            lastFireTime = System.currentTimeMillis();

            // Start flash animation
            startFlash();
        }
    }

    private void startFlash() {
        isFlashing = true;
        flashStartTime = System.currentTimeMillis();
    }

    private void updateFlash() {
        if (isFlashing && System.currentTimeMillis() - flashStartTime > FLASH_DURATION) {
            isFlashing = false;
        }
    }

    public void reload() {
        currentBullets = maxBullets;
    }

    // Input handling methods
    @Override
    public void keyPressed(KeyEvent e) {
        updateDesiredVelocityAndEyeDirection(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        updateDesiredVelocityAndEyeDirection(e.getKeyCode(), false);
    }

    private void updateDesiredVelocityAndEyeDirection(int keyCode, boolean isPressed) {
        float speed = isPressed ? maxSpeed : 0;
        switch (keyCode) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> {
                desiredVelocity.y = -speed;
                updateEyeDirectionOnKeyPress(0, -1, isPressed);
            }
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> {
                desiredVelocity.y = speed;
                updateEyeDirectionOnKeyPress(0, 1, isPressed);
            }
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> {
                desiredVelocity.x = -speed;
                updateEyeDirectionOnKeyPress(-1, 0, isPressed);
            }
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> {
                desiredVelocity.x = speed;
                updateEyeDirectionOnKeyPress(1, 0, isPressed);
            }
            case KeyEvent.VK_L -> {
                if (isPressed) toggleLaser();
            }
            case KeyEvent.VK_R -> reload();
            case KeyEvent.VK_G -> {
                if (isPressed) {
                    gravityEnabled = !gravityEnabled;
                    if (!gravityEnabled) {
                        locateNewPosition(mousePosition.x, mousePosition.y);
                    } else {
                        verticalVelocity = 0;
                    }
                }
            }
        }
    }

    private void updateEyeDirectionOnKeyPress(float x, float y, boolean isPressed) {
        isMouseInWindow = false;
        if (isPressed) {
            targetEyeDirection.x = x;
            targetEyeDirection.y = y;
        } else {
            // Check if any other movement keys are still pressed
            if (desiredVelocity.x == 0 && desiredVelocity.y == 0) {
                targetEyeDirection.x = 0;
                targetEyeDirection.y = 0;
            }
        }
        normalizeVector(targetEyeDirection);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePosition = e.getPoint();
        isMouseInWindow = true;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        isMouseInWindow = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        isMouseInWindow = true;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            isFiring = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            isFiring = false;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            //checks for left mouse button clicked
//            if (e.getClickCount() == 2) {
//                locateNewPosition(e.getX(), e.getY());
//            }

        } else if (e.getButton() == MouseEvent.BUTTON3) {
            if (!isFiring) toggleLaser();
        }

    }

    // Public methods
    public void locateDefaultPosition() {
        locateNewPosition((int) (GameWindow.getScreenWidth() / 2f - diameter / 2), (int) (GameWindow.getScreenHeight() * 0.3f - diameter / 2));
    }

    public void locateNewPosition(int x, int y) {
        targetPosition = new Position(x, y);
        isApproachingTarget = true;
        gravityEnabled = false;
    }

    public void setPosition(int x, int y) {
        this.position.setCoordinates(x, y);
    }

    public float getCurrentSpeed() {
        if (isApproachingTarget) {
            float dx = targetPosition.x() - position.x();
            float dy = targetPosition.y() - position.y();
            float moveX = dx * APPROACH_SPEED;
            float moveY = dy * APPROACH_SPEED;
            return (float) Math.sqrt(moveX * moveX + moveY * moveY);
        } else if (gravityEnabled) {
            return (float) Math.sqrt(velocity.x * velocity.x + verticalVelocity * verticalVelocity);
        } else {
            return (float) Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y);
        }
    }
}