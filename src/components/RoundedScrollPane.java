package components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * author: ahror
 * <p>
 * since: 8/31/24
 */
public class RoundedScrollPane extends JScrollPane {
    private int cornerRadius;

    public RoundedScrollPane(Component view, int cornerRadius) {
        super(view,VERTICAL_SCROLLBAR_NEVER,HORIZONTAL_SCROLLBAR_NEVER);
        setOpaque(false);
        getViewport().setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setWheelScrollingEnabled(true);
        setCornerRadius(cornerRadius);
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (g != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));
                super.paintComponent(g2);
            } finally {
                g2.dispose();
            }
        }
    }
}
