package main;

import inputs.GlobalKeyListener;

import javax.swing.*;
import java.awt.*;

/**
 * author: ahror
 * <p>
 * since: 8/29/24 1:29 PM
 */

public class MainClass {
    public static void main(String[] args) {
        GlobalKeyListener globalKeyListener = new GlobalKeyListener();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(globalKeyListener);
        SwingUtilities.invokeLater(Game::getInstance);
    }
}
