package utils;

/**
 * author: ahror
 * <p>
 * since: 8/30/24
 */
public class Position {
    private float x;
    private float y;

    public Position(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Position() {
        this.x = 0;
        this.y = 0;
    }

    public float x() {
        return x;
    }

    public void x(float x) {
        this.x = x;
    }

    public void incX(float x) {
        this.x += x;
    }

    public void decX(float x) {
        this.x -= x;
    }

    public float y() {
        return y;
    }

    public void y(float y) {
        this.y = y;
    }

    public void incY(float y) {
        this.y += y;
    }

    public void decY(float y) {
        this.y -= y;
    }

    public void setCoordinates(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
