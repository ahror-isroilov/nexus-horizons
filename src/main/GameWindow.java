package main;

import components.GameEndDialog;
import lombok.Getter;
import lombok.Setter;
import utils.Const;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * author: ahror
 * <p>
 * since: 8/29/24
 */

public class GameWindow extends JFrame {
    @Getter
    @Setter
    private static int screenWidth = Const.screenWidth;
    @Getter
    @Setter
    private static int screenHeight = Const.screenHeight;

    private final GamePanel gamePanel;
    private final HomePanel homePanel;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    public GameWindow(String title) {
        super(title);
        initializeWindow();
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        gamePanel = new GamePanel();
        homePanel = new HomePanel();
        mainPanel.add(homePanel, "Home");
        mainPanel.add(gamePanel, "Game");
        add(mainPanel);
        setVisible(true);
    }

    private void initializeWindow() {
        setLayout(new BorderLayout());
        setBackground(Const.background);
        setSize(new Dimension(Const.screenWidth, Const.screenHeight));
        setMinimumSize(new Dimension(Const.screenWidth / 2, Const.screenHeight / 2));
        setLocationRelativeTo(null);
        setResizable(false);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateComponentSizes();
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public void showHomePanel() {
        SwingUtilities.invokeLater(() -> {
            if (!homePanel.isRequestFocusEnabled()) {
                cardLayout.show(mainPanel, "Home");
                homePanel.setRequestFocusEnabled(true);
                homePanel.requestFocus();
                GameEndDialog.getInstance(gamePanel).dispose();
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

    private void updateComponentSizes() {
        Dimension size = getSize();
        mainPanel.setSize(size);
        gamePanel.setSize(size);
        homePanel.setSize(size);
        GameWindow.setScreenWidth(size.width);
        GameWindow.setScreenHeight(size.height);
    }
}
