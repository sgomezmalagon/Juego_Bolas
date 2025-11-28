/*
 */
package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameModel {
    private final List<Ball> balls = new ArrayList<>();
    private final List<PlayerBall> playerBalls = new ArrayList<>();
    private final Room room;
    private int width;
    private int height;

    private List<Ball> cachedBallsView = Collections.emptyList();
    private List<PlayerBall> cachedPlayersView = Collections.emptyList();
    private boolean ballsDirty = true;
    private boolean playersDirty = true;

    public GameModel(int width, int height) {
        this.width = width;
        this.height = height;
        int roomWidth = 300;
        int roomHeight = 300;
        int roomX = (width - roomWidth) / 2;
        int roomY = (height - roomHeight) / 2;
        this.room = new Room(roomX, roomY, roomWidth, roomHeight);
    }

    // actualizar límites (synchronized por si Swing lo llama desde otro hilo)
    public synchronized void setBounds(int width, int height) {
        this.width = width;
        this.height = height;
        int roomWidth = room.getBounds().width;
        int roomHeight = room.getBounds().height;
        int roomX = (width - roomWidth) / 2;
        int roomY = (height - roomHeight) / 2;
        room.getBounds().setLocation(roomX, roomY);
    }

    public synchronized void addBall() {
        balls.add(new Ball(width, height));
        ballsDirty = true;
    }

    public synchronized boolean addPlayerBall() {
        if (!playerBalls.isEmpty()) return false;
        PlayerBall pb = new PlayerBall(width, height);
        pb.setControlled(true);
        pb.setDirectControl(true);
        playerBalls.add(pb);
        playersDirty = true;
        return true;
    }

    public synchronized void clearBalls() {
        balls.clear();
        ballsDirty = true;
    }

    public synchronized List<Ball> getSnapshot() {
        if (ballsDirty) {
            cachedBallsView = Collections.unmodifiableList(new ArrayList<>(balls));
            ballsDirty = false;
        }
        return cachedBallsView;
    }

    // update de física (dt en ms)
    public synchronized void update(double dt) {
        for (PlayerBall pb : playerBalls) {
            pb.update(dt, width, height, room);
        }
        for (Ball b : balls) {
            b.update(dt, width, height, room);
        }
        handleCollisions();
    }

    // colisiones entre todas las bolas (incluye jugador)
    private void handleCollisions() {
        int total = balls.size() + playerBalls.size();
        for (int i = 0; i < total; i++) {
            Ball a = (i < balls.size()) ? balls.get(i) : playerBalls.get(i - balls.size());
            for (int j = i + 1; j < total; j++) {
                Ball b = (j < balls.size()) ? balls.get(j) : playerBalls.get(j - balls.size());

                int ra = a.getRadius();
                int rb = b.getRadius();
                double dx = b.x - a.x;
                double dy = b.y - a.y;
                double minDist = ra + rb;
                double dist2 = dx * dx + dy * dy;
                if (dist2 <= 0) dist2 = 0.0001;
                if (dist2 < (minDist * minDist)) {
                    double dist = Math.sqrt(dist2);
                    double nx = dx / dist;
                    double ny = dy / dist;
                    double overlap = (minDist - dist);
                    double half = overlap / 2.0;
                    a.x -= nx * half;
                    a.y -= ny * half;
                    b.x += nx * half;
                    b.y += ny * half;

                    double rvx = b.vx - a.vx;
                    double rvy = b.vy - a.vy;
                    double vn = rvx * nx + rvy * ny;
                    if (vn < 0) {
                        double e = 1.0;
                        double impulse = -(1 + e) * vn / 2.0;
                        double ix = impulse * nx;
                        double iy = impulse * ny;
                        a.vx -= ix; a.vy -= iy;
                        b.vx += ix; b.vy += iy;
                    }
                }
            }
        }
    }

    public synchronized int count() { return balls.size(); }
    public Room getRoom() { return room; }

    public synchronized List<PlayerBall> getPlayerBalls() {
        if (playersDirty) {
            cachedPlayersView = Collections.unmodifiableList(new ArrayList<>(playerBalls));
            playersDirty = false;
        }
        return cachedPlayersView;
    }
}
