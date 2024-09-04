package main;

import inputs.KeyboardInputs;
import inputs.MouseInputs;
import lombok.Getter;
import main.entity.Bullet;
import main.entity.Enemy;
import main.entity.Player;
import utils.AudioUtils;
import utils.CollisionUtils;
import utils.Const;
import utils.Planets;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * author: ahror
 * <p>
 * since: 8/29/24
 */
@Getter
public class GamePanel extends JPanel {
    private Player player;
    private final List<Enemy> enemies = new ArrayList<>();

    private JLabel bulletCount;
    private JLabel gravityLabel;
    private JLabel reloadAlertLabel;
    private float alertOpacity = 0.0f;
    private boolean fadeIn = true;

    private ImageIcon planetIcon;
    private ImageIcon bulletIcon;

    private static final int ENEMY_COUNT = 10;
    private static final int TIMER_DELAY = 32;

    public GamePanel() {
        initializePanelProperties();
        createUIComponents();
        setupInputListeners();
        createGameEntities();
        updateUIElements();
        initializeSounds();
    }

    private void initializePanelProperties() {
        setDoubleBuffered(true);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Const.background);
    }

    private void initializeSounds() {
        AudioUtils.preloadSound("shoot.wav", player.getMaxBullets());
        AudioUtils.preloadSound("die.wav", enemies.size());
    }

    private void createUIComponents() {
        planetIcon = new ImageIcon("images/planet.png");
        bulletIcon = new ImageIcon("images/bullet.png");
        planetIcon = new ImageIcon(planetIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        bulletIcon = new ImageIcon(bulletIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

        bulletCount = createLabel("0", 0, 10, 0, 10);
        gravityLabel = createLabel("Gravity: ", 10, 10, 10, 10);
        bulletCount.setIcon(bulletIcon);
        gravityLabel.setIcon(planetIcon);
        bulletCount.setHorizontalTextPosition(JLabel.RIGHT);
        gravityLabel.setHorizontalTextPosition(JLabel.RIGHT);

        reloadAlertLabel = new JLabel("Press R to reload!");
        reloadAlertLabel.setFont(Game.UIFont.deriveFont(24f));
        reloadAlertLabel.setForeground(new Color(223, 12, 12, 157));
        reloadAlertLabel.setHorizontalAlignment(SwingConstants.CENTER);
        reloadAlertLabel.setVerticalAlignment(SwingConstants.CENTER);
        reloadAlertLabel.setOpaque(false);
        add(reloadAlertLabel);
        add(gravityLabel);
        add(bulletCount);
    }

    private JLabel createLabel(String text, int top, int left, int bottom, int right) {
        JLabel label = new JLabel(text);
        label.setFont(Game.UIFont);
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setVerticalAlignment(SwingConstants.TOP);
        label.setBorder(new EmptyBorder(top, left, bottom, right));
        return label;
    }

    private void createGameEntities() {
        player = new Player(Const.diameter, Color.WHITE, 1);
        spawnEnemies();
    }

    private void spawnEnemies() {
        if (enemies.isEmpty()) for (int i = 0; i < ENEMY_COUNT; i++) {
            enemies.add(new Enemy());
        }
    }

    private void setupInputListeners() {
        addKeyListener(new KeyboardInputs(this));
        MouseInputs mouseInputs = MouseInputs.getInstance(this);
        addMouseMotionListener(mouseInputs);
        addMouseListener(mouseInputs);
    }

    private void updateUIElements() {
        new Timer(TIMER_DELAY, e -> {
            bulletCount.setText(String.format("⚡: %d", player.getCurrentBullets()));
            bulletCount.setIcon(bulletIcon);
            gravityLabel.setText(String.format("🪐: %s", Planets.getCurrentPlanet()));
            gravityLabel.setIcon(planetIcon);
            gravityLabel.setForeground(player.isGravityEnabled() ? Color.WHITE : new Color(83, 78, 78));
            checkForReloadLabel();
            setCursor(player.isLaserActive() ? new Cursor(Cursor.CROSSHAIR_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
        }).start();
    }

    private void checkForReloadLabel() {
        if (player.getCurrentBullets() < 10) {
            if (fadeIn) {
                alertOpacity += 0.1f;
                if (alertOpacity >= 1.0f) {
                    fadeIn = false;
                }
            } else {
                alertOpacity -= 0.1f;
                if (alertOpacity <= 0.0f) {
                    fadeIn = true;
                }
            }
        } else {
            alertOpacity = 0.0f;
        }
        reloadAlertLabel.setForeground(new Color(1f, 0f, 0f, Math.max(0.0f, Math.min(1.0f, alertOpacity))));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        player.spawn(g);
        enemies.forEach(e -> e.spawn(g));
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.spawn(g);

            if (enemy.isDead()) {
                enemyIterator.remove();
                continue;
            }

            // Check bullet collisions
            Iterator<Bullet> bulletIterator = player.getBullets().iterator();
            while (bulletIterator.hasNext()) {
                Bullet bullet = bulletIterator.next();
                if (CollisionUtils.rayTraceCollision(enemy, bullet)) {
                    enemy.takeDamage(bullet.getDAMAGE());
                    bulletIterator.remove();
                    break;  // Break after collision to avoid multiple hits from same bullet
                }
            }
        }
        reloadAlertLabel.setBounds((getWidth() - 400) / 2, getHeight() / 4, 400, 50);
    }

    @Override
    public void requestFocus() {
        spawnEnemies();
        super.requestFocus();
    }
}