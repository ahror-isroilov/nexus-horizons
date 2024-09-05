package components;

import main.Game;
import main.GamePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * author: ahror
 * <p>
 * since: 8/31/24
 */
public class GameEndDialog extends RoundedDialog {
    private static GameEndDialog instance;
    private GamePanel gamePanel;

    public GameEndDialog(GamePanel gamePanel) {
        super(200, 100);
        this.gamePanel = gamePanel;
        initializeUI();
    }

    private void initializeUI() {
        JLabel titleLabel = new JLabel("Enemies are over", SwingConstants.CENTER);
        titleLabel.setForeground(new Color(230, 230, 230));
        titleLabel.setFont(Game.UIFont);
        titleLabel.setBorder(new EmptyBorder(30, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        RoundedButton respawn = new RoundedButton("Respawn", 15, new Dimension(100, 20), event -> {
            gamePanel.spawnEnemies();
            dispose();
        });
        mainPanel.add(respawn, BorderLayout.SOUTH);
    }

    public static GameEndDialog getInstance(GamePanel gamePanel) {
        if (instance == null) {
            instance = new GameEndDialog(gamePanel);
        }
        return instance;
    }
}
