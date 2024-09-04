package utils;

import java.awt.*;
import java.util.Random;

/**
 * author: ahror
 * <p>
 * since: 8/29/24
 */
public interface Const {
    int minSpeed = 20;
    int maxSpeed = 99999;
    int diameter = 35;
    int screenWidth = 800;
    int screenHeight = 800;
    Color background = new Color(1, 1, 18, 255);
    Color foreground = new Color(253, 254, 255);
    Random rand = new Random();
}
