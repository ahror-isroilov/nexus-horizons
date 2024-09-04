package inputs;

import main.GamePanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * author: ahror
 * <p>
 * since: 8/29/24
 */
public class MouseInputs implements MouseMotionListener, MouseListener {
    private final GamePanel gPanel;
    private static volatile MouseInputs instance;

    public MouseInputs(GamePanel gamePanel) {
        this.gPanel = gamePanel;
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        gPanel.getPlayer().mouseDragged(mouseEvent);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        gPanel.getPlayer().mouseMoved(e);
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        gPanel.getPlayer().mouseClicked(mouseEvent);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        gPanel.getPlayer().mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        gPanel.getPlayer().mouseReleased(mouseEvent);
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        gPanel.getPlayer().mouseEntered(mouseEvent);
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        gPanel.getPlayer().mouseExited(mouseEvent);
    }

    public static MouseInputs getInstance(GamePanel gamePanel) {
        if (instance == null) {
            synchronized (MouseInputs.class) {
                if (instance == null) {
                    instance = new MouseInputs(gamePanel);
                }
            }
        }
        return instance;
    }
}
