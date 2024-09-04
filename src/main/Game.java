package main;

import multiplayer.GameState;
import multiplayer.NetworkHandler;
import utils.AudioUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.io.InputStream;

/**
 * author: ahror
 * <p>
 * since: 8/29/24
 */
public class Game {
    private static final Game INSTANCE = new Game();
    private final int FPS = 60;
    private static final int UPDATES_PER_SECOND = 60;
    private static final long UPDATE_INTERVAL = 1000000000 / UPDATES_PER_SECOND;

    private boolean isRunning = false;
    private final GamePanel gamePanel;
    private final HomePanel homePanel;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final GameWindow gameWindow;
    public static Font UIFont;

    private NetworkHandler networkHandler;
    private boolean isMultiplayer;
    private boolean isHost;

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
        startGame();
    }

    public void startGame() {
        isRunning = true;
        Thread gameThread = new Thread(this::startGameLoop);
        gameThread.setPriority(Thread.MAX_PRIORITY);
        gameThread.start();
    }

    public void startMultiplayerGame(boolean asHost, String playerName, String ipAddress) {
        networkHandler = new NetworkHandler();
        isMultiplayer = true;
        isHost = asHost;
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (isHost) {
                    System.out.println("Starting server...");
                    networkHandler.startServer();
                } else {
                    System.out.println("Connecting to server at " + ipAddress);
                    networkHandler.connectToServer(ipAddress);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // This will throw an exception if there was an error in doInBackground
                    System.out.println("Network setup complete. Starting game...");
                    initializeMultiplayerGame(playerName);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(gameWindow, "Failed to " + (isHost ? "start server" : "connect to server") + ": " + e.getMessage(), "Network Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void initializeMultiplayerGame(String playerName) {
        SwingUtilities.invokeLater(() -> {
            gamePanel.setMultiplayerMode(true, isHost);
            gamePanel.getPlayer().setName(playerName);
            showGamePanel();
            gamePanel.updateFromGameState(networkHandler.receiveGameState()); // You might need to add this method to GamePanel
        });
    }

    private void startGameLoop() {
        double timePerFrame = 1_000_000_000.0 / FPS;
        long lastUpdateTime = System.nanoTime();
        long lastSyncTime = System.nanoTime();

        while (isRunning) {
            long now = System.nanoTime();
            if (now - lastUpdateTime >= timePerFrame) {
                update();
                lastUpdateTime = now;
            }
            if (isMultiplayer) {
                if (now - lastSyncTime >= 1000000000 / FPS) {  // Sync 60 times per second
                    syncGameState();
                    lastSyncTime = now;
                }
            }
        }
    }

    private void syncGameState() {
        if (!networkHandler.isConnected()) {
            handleNetworkError(new IOException("Network connection lost"));
            return;
        }
        if (isHost) {
            sendGameState();
        } else {
            receiveGameState();
        }
    }

    private void sendGameState() {
        GameState state = gamePanel.createGameState();
        networkHandler.sendGameState(state);
    }

    private void receiveGameState() {
        GameState gameState = networkHandler.receiveGameState();
        gamePanel.updateFromGameState(gameState);
    }

    private void handleNetworkError(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(gameWindow, "Network error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        isRunning = false;
    }

    private void endMultiplayerGame() {
        isRunning = false;
        if (networkHandler != null) {
            networkHandler.close();
        }
    }

    private void update() {
        gamePanel.repaint();
    }

    public void showHomePanel() {
        SwingUtilities.invokeLater(() -> {
            if (!homePanel.isRequestFocusEnabled()) {
                cardLayout.show(mainPanel, "Home");
                homePanel.setRequestFocusEnabled(true);
                homePanel.requestFocus();
            }
        });
    }

    public void showGamePanel() {
        SwingUtilities.invokeLater(() -> {
            cardLayout.show(mainPanel, "Game");
            gamePanel.requestFocus();
            homePanel.setRequestFocusEnabled(false);
        });
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
