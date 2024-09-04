package main.entity;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * author: ahror
 * <p>
 * since: 8/30/24
 */
public interface PlayerMovement {
    void mouseMoved(MouseEvent e);

    void mouseClicked(MouseEvent e);
    void mousePressed(MouseEvent e);
    void mouseReleased(MouseEvent e);
    void mouseDragged(MouseEvent e);

    void mouseExited(MouseEvent e);
    void mouseEntered(MouseEvent e);

    void keyPressed(KeyEvent e);

    void keyReleased(KeyEvent e);
}
