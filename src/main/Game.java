package main;

import utils.AudioUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.InputStream;

/**
 * author: ahror
 * <p>
 * since: 8/29/24
 */
public class Game {
    private static final Game INSTANCE = new Game();
    private final int FPS = 60;
    private final GamePanel gamePanel;
    private final HomePanel homePanel;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final GameWindow gameWindow;
    public static Font UIFont;

    public Game() {
        AudioUtils.preloadSound("drop.wav", 1);
        AudioUtils.preloadSound("click.wav", 1);
        AudioUtils.preloadSound("button.wav", 1);
        loadCustomFont();
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        gamePanel = new GamePanel();
        homePanel = new HomePanel(this);

        mainPanel.add(homePanel, "Home");
        mainPanel.add(gamePanel, "Game");

        gameWindow = new GameWindow("Bounce");
        gameWindow.add(mainPanel, BorderLayout.CENTER);
        gameWindow.setVisible(true);

        gameWindow.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateComponentSizes();
            }
        });

        showHomePanel();
        startGameLoop();
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

    private void startGameLoop() {
        Thread thread = new Thread(() -> {
            double timePerFrame = 1_000_000_000.0 / FPS;
            long lastFrame = System.nanoTime();
            long now;

            int frames = 0;
            long lastCheck = System.currentTimeMillis();
            while (true) {
                now = System.nanoTime();
                if (now - lastFrame >= timePerFrame) {
                    gamePanel.repaint();
                    lastFrame = now;
                    frames++;
                }

                if (System.currentTimeMillis() - lastCheck >= 1000) {
                    lastCheck = System.currentTimeMillis();
                    System.out.printf("FPS: %s\n", frames);
                    frames = 0;
                }
            }
        });
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setDaemon(true);
        thread.start();
    }

    public void showHomePanel() {
        SwingUtilities.invokeLater(() -> {
            if (!homePanel.isRequestFocusEnabled()) {
                cardLayout.show(mainPanel, "Home");
                homePanel.setRequestFocusEnabled(true);
                homePanel.requestFocus();
                gameWindow.restoreDefaultSize();
            }
        });
    }

    public void showGamePanel() {
        SwingUtilities.invokeLater(() -> {
            cardLayout.show(mainPanel, "Game");
            gamePanel.requestFocus();
            homePanel.setRequestFocusEnabled(false);
            gameWindow.maximizeWindow();
        });
    }

    private void updateComponentSizes() {
        Dimension size = gameWindow.getSize();
        mainPanel.setSize(size);
        gamePanel.setSize(size);
        homePanel.setSize(size);
        GameWindow.setScreenWidth(size.width);
        GameWindow.setScreenHeight(size.height);
    }

    public static Game getInstance() {
        if (INSTANCE != null) return INSTANCE;
        else return new Game();
    }
}
