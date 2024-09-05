package components;

import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/**
 * author: ahror
 * <p>
 * since: 9/1/24
 */
public class RoundedDialog extends JDialog {
    protected JPanel mainPanel = new JPanel(new BorderLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Paint main background
            g2d.setColor(new Color(40, 40, 40));
            g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));

            Path2D.Float roundRect = drawRoundedDragArea();
            // Paint drag area
            g2d.setColor(new Color(70, 70, 70));
            g2d.fill(roundRect);

            g2d.dispose();
        }

        private Path2D.Float drawRoundedDragArea() {
            Path2D.Float roundRect = new Path2D.Float();
            roundRect.moveTo(0, DRAG_AREA_HEIGHT); // Start at the bottom-left of drag area
            roundRect.lineTo(0, 10); // Move up to start of rounding
            roundRect.quadTo(0, 0, 10, 0); // Top-left corner
            roundRect.lineTo(getWidth() - 10, 0); // Top side
            roundRect.quadTo(getWidth(), 0, getWidth(), 10); // Top-right corner
            roundRect.lineTo(getWidth(), DRAG_AREA_HEIGHT); // Move down to bottom-right of drag area
            roundRect.closePath();
            return roundRect;
        }
    };
    protected ActionMap actionMap = mainPanel.getActionMap();
    protected InputMap inputMap = mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

    private Point dragOffset;
    private final int DRAG_AREA_HEIGHT = 30; // Height of the top area where dragging is allowed

    public RoundedDialog(int width, int height) {
        setUndecorated(true);
        setModal(true);
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 0));
        mainPanel.setOpaque(true);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);
        setSize(width, height);
        setLocationRelativeTo(null);
        addInputListeners();
        addDragListener();
        setFocusable(true);
        requestFocus();
    }

    private void addInputListeners() {
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
        actionMap.put("close", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void addDragListener() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getY() <= DRAG_AREA_HEIGHT) {
                    dragOffset = new Point(e.getX(), e.getY());
                }
            }

            public void mouseReleased(MouseEvent e) {
                dragOffset = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragOffset != null) {
                    Point currentScreen = e.getLocationOnScreen();
                    setLocation(currentScreen.x - dragOffset.x, currentScreen.y - dragOffset.y);
                }
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setComposite(AlphaComposite.SrcOver);
        super.paint(g2d);
        g2d.dispose();
    }

    public void showDialog() {
        setVisible(true);
    }
}
