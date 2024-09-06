package components;

import main.Game;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

/**
 * author: ahror
 * <p>
 * since: 8/31/24
 */
public class HelpDialog extends RoundedDialog {
    private static HelpDialog instance;

    public HelpDialog() {
        super(250, 300);
        initializeUI();
    }

    private void initializeUI() {
        JTextPane textPane = new JTextPane();
        textPane.setOpaque(false); // Makes the background transparent
        textPane.setEditable(false); // Makes the text pane non-editable
        // Set up the text and center alignment
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(center, Color.WHITE); // Set text color
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        textPane.setFont(Game.UIFont.deriveFont(12f));
        textPane.setText("""
                - Press the `P` key to select planet
                
                - Press the `G` key to enable/disable gravity
                
                - Press the `L` key to activate laser
                
                - Press the `Q` key to return home
                
                
                Ahror Isroilov | 01.09.2024
                """);
        textPane.setBorder(new EmptyBorder(50, 0, 0, 0));
        mainPanel.add(textPane, BorderLayout.CENTER);

        RoundedButton ok = new RoundedButton("Ok", 15);
        ok.setSize(50, 20);
        ok.addActionListener(e -> dispose());
        mainPanel.add(ok, BorderLayout.SOUTH);
    }

    public static HelpDialog getInstance() {
        if (instance == null) {
            instance = new HelpDialog();
        }
        return instance;
    }
}
