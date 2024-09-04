package main;

import components.HelpDialog;
import components.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

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

    //multiplayer methods

    private void showPlaygroundDialog() {
        JTextField nameField = new JTextField(20);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Enter your name:"));
        panel.add(nameField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Player Setup",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String playerName = nameField.getText().trim();
            if (!playerName.isEmpty()) {
                showHostOrConnectDialog(playerName);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a valid name.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showHostOrConnectDialog(String playerName) {
        String[] options = {"Host Game", "Connect to Game"};
        int choice = JOptionPane.showOptionDialog(this, "Do you want to host or connect to a game?",
                "Game Mode", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice == 0) {
            hostGame(playerName);
        } else if (choice == 1) {
            connectToGame(playerName);
        }
    }

    private void hostGame(String playerName) {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            JOptionPane.showMessageDialog(this, "Your IP address is: " + hostAddress +
                    "\nShare this with the other player to connect.", "Host Information", JOptionPane.INFORMATION_MESSAGE);
            game.startMultiplayerGame(true, playerName, null);
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(this, "Error getting host address: " + e.getMessage(),
                    "Host Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void connectToGame(String playerName) {
        String hostAddress = JOptionPane.showInputDialog(this, "Enter the host's IP address:");
        if (hostAddress != null && !hostAddress.trim().isEmpty()) {
            game.startMultiplayerGame(false, playerName, hostAddress.trim());
        } else {
            JOptionPane.showMessageDialog(this, "Invalid host address.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
