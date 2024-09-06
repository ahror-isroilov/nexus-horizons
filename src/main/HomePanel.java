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

    public HomePanel() {
        setBackground(Color.BLACK);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel welcomeLabel = new JLabel("Welcome to Nexus Horizons°!", SwingConstants.CENTER);
        welcomeLabel.setFont(Game.UIFont.deriveFont(42f));
        welcomeLabel.setForeground(Color.WHITE);
        gbc.insets = new Insets(100, 0, 0, 0);
        gbc.gridy = 0;
        gbc.gridx = 0;
        add(welcomeLabel, gbc);
        gbc.weighty = 1.0;
        add(Box.createVerticalGlue(), gbc);

        RoundedButton playground = new RoundedButton("Playground", 20, new Dimension(170, 40), actionEvent -> Game.gameWindow.showGamePanel());
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0);
        gbc.weighty = 0; // Reset weight
        add(playground, gbc);

        RoundedButton info = new RoundedButton("Info", 20, new Dimension(170, 40), actionEvent -> HelpDialog.getInstance().showDialog());
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        add(info, gbc);

        RoundedButton exit = new RoundedButton("Exit", 20, new Dimension(170, 40), actionEvent -> System.exit(0));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(exit, gbc);

        gbc.gridy = 4;
        gbc.weighty = 1.0;
        add(Box.createVerticalGlue(), gbc);
    }
}
