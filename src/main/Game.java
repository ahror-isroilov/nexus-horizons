package main;

import lombok.Singular;
import utils.AudioUtils;

import java.awt.*;
import java.io.InputStream;

/**
 * author: ahror
 * <p>
 * since: 8/29/24
 */
public class Game {
    private static final Game INSTANCE = new Game();
    private static final int FPS = 60;

    private boolean isRunning = false;
    public static GameWindow gameWindow;
    public static Font UIFont;

    public Game() {
        loadResources();
        gameWindow = new GameWindow("Nexus Horizons");
        gameWindow.showHomePanel();
        startGame();
    }

    private void loadResources() {
        AudioUtils.preloadSound("drop.wav", 1);
        AudioUtils.preloadSound("button.wav", 1);
        loadCustomFont();
    }

    public void startGame() {
        isRunning = true;
        Thread gameThread = new Thread(this::startGameLoop);
        gameThread.setPriority(Thread.MAX_PRIORITY);
        gameThread.start();
    }

    private void startGameLoop() {
        double timePerFrame = 1_000_000_000.0 / FPS;
        long lastUpdateTime = System.nanoTime();

        while (isRunning) {
            long now = System.nanoTime();
            if (now - lastUpdateTime >= timePerFrame) {
                update();
                lastUpdateTime = now;
            }
        }
    }

    private void update() {
        gameWindow.repaint();
    }

    private void loadCustomFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/aldrich.ttf");
            UIFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(14f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(UIFont);
        } catch (Exception e) {
            System.out.printf("Error loading custom font: %s\n", e.getMessage());
            UIFont = new Font("Segoe UI", Font.BOLD, 14); // Fallback font
        }
    }

    public static Game getInstance() {
        if (INSTANCE != null) return INSTANCE;
        else return new Game();
    }
}
