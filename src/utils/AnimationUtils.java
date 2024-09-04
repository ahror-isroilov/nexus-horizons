package utils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import static utils.Const.rand;

/**
 * author: ahror
 * <p>
 * since: 9/3/24
 */
public final class AnimationUtils {

    public static void drawMuzzleFlash(Graphics2D g2d, float diameter, Position position, Point2D.Float currentEyeDirection,long flashStartTime,float FLASH_DURATION) {
        float flashRadius = diameter * 0.5f;
        float flashX = position.x() + diameter / 2 + currentEyeDirection.x * diameter / 2;
        float flashY = position.y() + diameter / 2 + currentEyeDirection.y * diameter / 2;

        // Calculate flash alpha based on time, clamped between 0 and 1
        float alpha = Math.max(0, Math.min(1, 1.0f - (System.currentTimeMillis() - flashStartTime) / (float) FLASH_DURATION));

        // Save the original composite and transform
        Composite originalComposite = g2d.getComposite();
        AffineTransform originalTransform = g2d.getTransform();

        // Create and draw the main flash
        drawFlashCore(g2d, flashX, flashY, flashRadius, alpha);

        // Draw additional flames
        drawFlames(g2d, flashX, flashY, flashRadius, alpha);

        // Draw sparks
        drawSparks(g2d, flashX, flashY, flashRadius, alpha);

        // Restore the original composite and transform
        g2d.setComposite(originalComposite);
        g2d.setTransform(originalTransform);
    }

    private static void drawFlashCore(Graphics2D g2d, float x, float y, float radius, float alpha) {
        // Create a star-like shape for the flash
        Path2D flashShape = createStarShape(x, y, radius, 8, 0.5f);

        // Create a radial gradient for the flash
        RadialGradientPaint gradient = new RadialGradientPaint(
                x, y, radius,
                new float[]{0.0f, 0.7f, 1.0f},
                new Color[]{
                        new Color(1.0f, 1.0f, 0.8f, alpha),
                        new Color(1.0f, 0.8f, 0.0f, alpha * 0.8f),
                        new Color(1.0f, 0.0f, 0.0f, 0.0f)
                }
        );

        g2d.setPaint(gradient);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // Apply a slight rotation for variation
        AffineTransform rotation = AffineTransform.getRotateInstance(rand.nextDouble() * Math.PI * 2, x, y);
        g2d.transform(rotation);

        g2d.fill(flashShape);
    }

    private static Path2D createStarShape(float centerX, float centerY, float radius, int points, float innerRadiusFactor) {
        Path2D path = new Path2D.Float();
        float angle = (float) Math.PI * 2 / points;

        for (int i = 0; i < points * 2; i++) {
            float r = (i % 2 == 0) ? radius : radius * innerRadiusFactor;
            float x = centerX + (float) Math.cos(i * angle / 2) * r;
            float y = centerY + (float) Math.sin(i * angle / 2) * r;

            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        path.closePath();
        return path;
    }

    private static void drawFlames(Graphics2D g2d, float x, float y, float radius, float alpha) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.7f));

        for (int i = 0; i < 8; i++) {
            double angle = rand.nextDouble() * Math.PI * 2;
            float flameLength = radius * (0.5f + rand.nextFloat() * 0.5f);
            float flameWidth = flameLength * 0.3f;

            Path2D flame = new Path2D.Float();
            flame.moveTo(x, y);
            flame.curveTo(
                    x + Math.cos(angle - 0.5) * flameWidth, y + Math.sin(angle - 0.5) * flameWidth,
                    x + Math.cos(angle) * flameLength * 0.7, y + Math.sin(angle) * flameLength * 0.7,
                    x + Math.cos(angle) * flameLength, y + Math.sin(angle) * flameLength
            );
            flame.curveTo(
                    x + Math.cos(angle) * flameLength * 0.7, y + Math.sin(angle) * flameLength * 0.7,
                    x + Math.cos(angle + 0.5) * flameWidth, y + Math.sin(angle + 0.5) * flameWidth,
                    x, y
            );

            g2d.setPaint(new RadialGradientPaint(
                    x, y, flameLength,
                    new float[]{0.0f, 0.7f, 1.0f},
                    new Color[]{
                            new Color(1.0f, 0.8f, 0.2f, alpha * 0.8f),
                            new Color(1.0f, 0.4f, 0.0f, alpha * 0.4f),
                            new Color(1.0f, 0.0f, 0.0f, 0.0f)
                    }
            ));
            g2d.fill(flame);
        }
    }

    private static void drawSparks(Graphics2D g2d, float x, float y, float radius, float alpha) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.setColor(new Color(1.0f, 0.9f, 0.6f));

        for (int i = 0; i < 20; i++) {
            double angle = rand.nextDouble() * Math.PI * 2;
            float distance = rand.nextFloat() * radius * 1.2f;
            float sparkSize = 1 + rand.nextFloat() * 2;

            g2d.fillOval(
                    (int)(x + Math.cos(angle) * distance - sparkSize / 2),
                    (int)(y + Math.sin(angle) * distance - sparkSize / 2),
                    (int)sparkSize,
                    (int)sparkSize
            );
        }
    }
}
