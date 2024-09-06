package main;

import components.GameEndDialog;
import components.ShowPlanets;
import inputs.KeyboardInputs;
import inputs.MouseInputs;
import lombok.Getter;
import lombok.Setter;
import main.entity.Bullet;
import main.entity.Enemy;
import main.entity.Player;
import multiplayer.EnemyState;
import multiplayer.GameState;
import utils.AudioUtils;
import utils.CollisionUtils;
import utils.Const;
import utils.Planets;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static utils.Const.rand;

/**
 * author: ahror
 * <p>
 * since: 8/29/24
 */
@Getter
public class GamePanel extends JPanel implements GamePanelInputListener {
    private Player player;
    private final List<Enemy> enemies = new ArrayList<>();

    private JLabel bulletCountLabel;
    private JLabel gravityLabel;
    private JLabel reloadAlertLabel;
    private float alertOpacity = 0.0f;
    private boolean fadeIn = true;

    private static final int TIMER_DELAY = 16;

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
        AudioUtils.preloadSound("shoot.wav", 10);
        AudioUtils.preloadSound("die.wav", 5);
    }

    private void createUIComponents() {
        bulletCountLabel = createLabel("0", 0, 10, 0, 10);
        gravityLabel = createLabel("Gravity: ", 10, 10, 10, 10);
        bulletCountLabel.setHorizontalTextPosition(JLabel.RIGHT);
        gravityLabel.setHorizontalTextPosition(JLabel.RIGHT);

        reloadAlertLabel = new JLabel("Press R to reload!");
        reloadAlertLabel.setFont(Game.UIFont.deriveFont(24f));
        reloadAlertLabel.setForeground(new Color(225, 45, 45, 87));
        reloadAlertLabel.setHorizontalAlignment(SwingConstants.CENTER);
        reloadAlertLabel.setVerticalAlignment(SwingConstants.CENTER);
        reloadAlertLabel.setOpaque(false);
        add(reloadAlertLabel);
        add(gravityLabel);
        add(bulletCountLabel);
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

    public void spawnEnemies() {
        if (enemies.isEmpty()) for (int i = 0; i < rand.nextInt(30) + 1; i++) {
            enemies.add(new Enemy(UUID.randomUUID()));
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
            bulletCountLabel.setText(String.format("⚡: %d", player.getCurrentBullets()));
            gravityLabel.setText(String.format("🪐: %s", Planets.getCurrentPlanet()));
            gravityLabel.setForeground(player.isGravityEnabled() ? Color.WHITE : new Color(83, 78, 78));
            checkForReloadLabel();
            setCursor(player.isLaserActive() ? new Cursor(Cursor.CROSSHAIR_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
            if (enemies.isEmpty()) GameEndDialog.getInstance(this).showDialog();
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
        handleBulletCollisions(g, enemyIterator, player);
        reloadAlertLabel.setBounds((getWidth() - 400) / 2, getHeight() / 4, 400, 50);
    }

    private void handleBulletCollisions(Graphics g, Iterator<Enemy> enemyIterator, Player player) {
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.spawn(g);

            if (enemy.isDead()) {
                enemyIterator.remove();
                continue;
            }

            Iterator<Bullet> bulletIterator = player.getBullets().iterator();
            while (bulletIterator.hasNext()) {
                Bullet bullet = bulletIterator.next();
                if (CollisionUtils.checkSweptAABBCollision(enemy, bullet)) {
                    enemy.takeDamage(bullet.getDAMAGE());
                    bulletIterator.remove();
                    break;
                }
            }
        }
    }

    @Override
    public void requestFocus() {
        spawnEnemies();
        super.requestFocus();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) ShowPlanets.getInstance().showDialog();
    }
}