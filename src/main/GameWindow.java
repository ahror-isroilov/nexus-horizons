package main;

import lombok.Getter;
import lombok.Setter;
import utils.Const;

import javax.swing.*;
import java.awt.*;

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


    private final int defaultWidth;
    private final int defaultHeight;

    public GameWindow(String title) {
        super(title);
        setLayout(new BorderLayout());
        setBackground(Const.background);

        defaultWidth = Const.screenWidth;
        defaultHeight = Const.screenHeight;

        setSize(new Dimension(Const.screenWidth, Const.screenHeight));
        setMinimumSize(new Dimension(Const.screenWidth / 2, Const.screenHeight / 2));
        setLocationRelativeTo(null);

        setResizable(false);
    }

    public void maximizeWindow() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        Rectangle bounds = device.getDefaultConfiguration().getBounds();

        setResizable(true);

//        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(new Dimension(1080, 720));
//        setSize(bounds.width, bounds.height);
        setLocationRelativeTo(null);

        screenWidth = bounds.width;
        screenHeight = bounds.height;

        setResizable(false);
    }

    public void restoreDefaultSize() {
        setResizable(true);

        setExtendedState(JFrame.NORMAL);
        setSize(new Dimension(defaultWidth, defaultHeight));
        setLocationRelativeTo(null);

        screenWidth = defaultWidth;
        screenHeight = defaultHeight;

        setResizable(false);
    }
}
