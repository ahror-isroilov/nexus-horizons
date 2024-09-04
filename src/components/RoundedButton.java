package components;

import lombok.Setter;
import main.Game;
import utils.AudioUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class RoundedButton extends JButton {
    private boolean isHovered = false;
    @Setter private Color hoverBackgroundColor = new Color(90, 90, 90);
    @Setter private Color normalBackgroundColor = new Color(70, 70, 70);
    private final int cornerRadius;

    public RoundedButton(String text, int cornerRadius) {
        super(text);
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setForeground(new Color(230, 230, 230));
        setFont(Game.UIFont);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.cornerRadius = cornerRadius;
        addMouseListener();
    }

    public RoundedButton(String text, int cornerRadius, Dimension size, ActionListener l) {
        super(text);
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setPreferredSize(size);
        addActionListener(l);
        setContentAreaFilled(false);
        setForeground(new Color(230, 230, 230));
        setFont(Game.UIFont);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.cornerRadius = cornerRadius;
        addMouseListener();
    }

    private void addMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                AudioUtils.play("button.wav");
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) AudioUtils.play("click.wav");
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isHovered) {
            g2.setColor(hoverBackgroundColor);
        } else {
            g2.setColor(normalBackgroundColor);
        }

        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));

        super.paintComponent(g2);
        g2.dispose();
    }
}
