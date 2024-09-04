package inputs;

import main.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * author: ahror
 * <p>
 * since: 8/29/24
 */
public class KeyboardInputs implements KeyListener {
    private final GamePanel gPanel;

    public KeyboardInputs(GamePanel gPanel) {
        this.gPanel = gPanel;
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        gPanel.getPlayer().keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        gPanel.getPlayer().keyReleased(e);
    }
}
