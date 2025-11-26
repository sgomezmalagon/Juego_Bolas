// Room: habitación central donde solo una bola puede estar dentro
package model;

import java.awt.Rectangle;

public class Room {
    private final Rectangle bounds;
    private Ball occupant = null;

    public Room(int x, int y, int width, int height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    public Rectangle getBounds() { return bounds; }

    public synchronized boolean isOccupied() { return occupant != null; }

    // intentar entrar: si está libre se pone como ocupante y notifica
    public synchronized boolean tryEnter(Ball ball) {
        if (occupant == null) {
            occupant = ball;
            this.notifyAll();
            return true;
        }
        this.notifyAll();
        return false;
    }

    // si la bola que sale era la ocupante, la liberamos
    public synchronized void tryLeave(Ball ball) {
        if (occupant == ball) {
            occupant = null;
            this.notifyAll();
        }
    }

    public synchronized Ball getOccupant() { return occupant; }

    public boolean contains(int x, int y) {
        return bounds.contains(x, y);
    }

    public boolean intersects(int x, int y, int radius) {
        int closestX = Math.max(bounds.x, Math.min(x, bounds.x + bounds.width));
        int closestY = Math.max(bounds.y, Math.min(y, bounds.y + bounds.height));
        int distX = x - closestX;
        int distY = y - closestY;
        return (distX * distX + distY * distY) < (radius * radius);
    }
}
