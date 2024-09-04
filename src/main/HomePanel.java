package main;

import components.HelpDialog;
import components.RoundedButton;

import javax.swing.*;
import java.awt.*;

/**
 * author: ahror
 * <p>
 * since: 9/3/24
 */
public class HomePanel extends JPanel {
    private final Game game;

    public HomePanel(Game game) {
        this.game = game;
        setBackground(Color.BLACK);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel welcomeLabel = new JLabel("Welcome to Nexus Horizons°!", SwingConstants.CENTER);
        welcomeLabel.setFont(Game.UIFont.deriveFont(42f));
        welcomeLabel.setForeground(Color.WHITE);
        gbc.insets = new Insets(100, 0, 0, 0); // Top padding
        gbc.gridy = 0;
        gbc.gridx = 0;
        add(welcomeLabel, gbc);
        gbc.weighty = 1.0;
        add(Box.createVerticalGlue(), gbc);

        // Playground button
        RoundedButton playground = new RoundedButton("Playground", 20, new Dimension(170, 40), actionEvent -> game.showGamePanel());
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0); // 30 pixels padding below
        gbc.weighty = 0; // Reset weight
        add(playground, gbc);

        // Info button
        RoundedButton info = new RoundedButton("Info", 20, new Dimension(170, 40), actionEvent -> HelpDialog.getInstance().showDialog());
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 30, 0); // 30 pixels padding below
        add(info, gbc);

        // Exit button
        RoundedButton exit = new RoundedButton("Exit", 20, new Dimension(170, 40), actionEvent -> System.exit(0));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0); // No padding below the last button
        add(exit, gbc);

        // Add vertical glue at the bottom
        gbc.gridy = 4;
        gbc.weighty = 1.0; // This will push everything up
        add(Box.createVerticalGlue(), gbc);
    }
}
