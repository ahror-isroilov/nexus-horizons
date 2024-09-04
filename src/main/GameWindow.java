package main;

import lombok.Getter;
import lombok.Setter;
import utils.Const;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * author: ahror
 * <p>
 * since: 8/29/24
 */
public class GameWindow extends JFrame {
    @Setter
    @Getter
    private static int screenWidth = Const.screenWidth;
    @Setter
    @Getter
    private static int screenHeight = Const.screenHeight;

    public GameWindow(String title) {
        super(title);
        setLayout(new BorderLayout());
        setBackground(Const.background);

        setSize(new Dimension(Const.screenWidth, Const.screenHeight));
        setMinimumSize(new Dimension(Const.screenWidth / 2, Const.screenHeight / 2));
        setLocationRelativeTo(null);
        addWindowClosingEvent();
        setResizable(false);
    }

    private void addWindowClosingEvent() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public void maximizeWindow() {
//        setResizable(true);
        int gameWidth = 1200;
        int gameHeight = 800;
        setSize(new Dimension(gameWidth, gameHeight));
        setLocationRelativeTo(null);
//        setResizable(false);

        GameWindow.setScreenWidth(gameWidth);
        GameWindow.setScreenHeight(gameHeight);
    }

    public void restoreDefaultSize() {
        setResizable(true);

        setExtendedState(JFrame.NORMAL);
        setSize(new Dimension(screenWidth, screenHeight));
        setLocationRelativeTo(null);
        setResizable(false);

        GameWindow.setScreenWidth(Const.screenWidth);
        GameWindow.setScreenHeight(Const.screenHeight);
    }
}
