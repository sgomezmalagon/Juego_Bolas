// Bola controlable por el jugador (WASD o modo nave)
package model;

import java.awt.Color;

public class PlayerBall extends Ball {
    // Física y parámetros (breves comentarios)
    private static final double MAX_SPEED = 0.9; // px/ms (modo nave)
    private static final double DIRECT_MAX_SPEED = 0.4; // px/ms (modo directo)
    private static final double FRICTION_ACCEL = 0.0009; // fricción

    // movimiento rotacional
    private double angle = 0.0; // radianes
    private int rotationInput = 0;
    private double angularVel = 0.0;

    // flags de control
    private boolean forward = false;
    private boolean backward = false;
    private boolean turbo = false;

    // modo directo (WASD)
    private boolean directControl = false;
    private double dirX = 0.0;
    private double dirY = 0.0;

    // parámetros de la nave
    private static final double ANG_ACCEL = 0.0006;
    private static final double ANG_IMPULSE = 0.0025;
    private static final double ANG_DAMP = 0.0012;
    private static final double ACCEL = 0.0012;
    private static final double BACK_ACCEL = 0.0020;
    private static final double TURBO_MULT = 2.2;

    // modo directo
    private static final double DIRECT_ACCEL = 0.0010;

    private boolean controlled = false;

    public PlayerBall(int boundsWidth, int boundsHeight) {
        super(boundsWidth, boundsHeight);
    }

    @Override
    public Color getColor() {
        return new Color(0, 90, 200);
    }

    @Override
    public int getRadius() {
        return 12;
    }

    // API de control (métodos usados por la vista)
    public void setRotationInput(int dir) { if (dir < -1) dir = -1; if (dir > 1) dir = 1; this.rotationInput = dir; }
    public void setForward(boolean f) { this.forward = f; }
    public void setBackward(boolean b) { this.backward = b; }
    public void setTurbo(boolean t) { this.turbo = t; }
    public void setDirectControl(boolean on) { this.directControl = on; }
    public void setDirectMovement(double dx, double dy) { this.dirX = dx; this.dirY = dy; }
    public void setControlled(boolean c) { this.controlled = c; }
    public boolean isControlled() { return controlled; }
    public double getAngle() { return angle; }
    public double getVx() { return vx; }
    public double getVy() { return vy; }
    public boolean isTurbo() { return turbo; }

    @Override
    public void update(double dt, int width, int height, Room room) {
        // Modo directo (WASD)
        if (directControl) {
            double len = Math.sqrt(dirX*dirX + dirY*dirY);
            double accel = 0.0;
            if (len > 1e-6) {
                double nx = dirX / len;
                double ny = dirY / len;
                accel = DIRECT_ACCEL * (turbo ? TURBO_MULT : 1.0);
                ax = nx * accel;
                ay = ny * accel;
            } else {
                double v = Math.sqrt(vx*vx + vy*vy);
                if (v > 1e-8) {
                    ax = - (vx / v) * FRICTION_ACCEL;
                    ay = - (vy / v) * FRICTION_ACCEL;
                } else { ax = 0; ay = 0; }
            }

            vx += ax * dt;
            vy += ay * dt;
            double sp = Math.sqrt(vx*vx + vy*vy);
            double limit = DIRECT_MAX_SPEED * (turbo ? TURBO_MULT : 1.0);
            if (sp > limit) { double s = limit / sp; vx *= s; vy *= s; }

            if (Math.abs(vx) < 1e-5) vx = 0;
            if (Math.abs(vy) < 1e-5) vy = 0;

            x += vx * dt;
            y += vy * dt;

            int r = getRadius();
            if (x - r < 0) { x = r; vx = -vx; ax = -ax; }
            if (x + r > width) { x = width - r; vx = -vx; ax = -ax; }
            if (y - r < 0) { y = r; vy = -vy; ay = -ay; }
            if (y + r > height) { y = height - r; vy = -vy; ay = -ay; }

            handleRoomInteraction(room);
            return;
        }

        // Modo nave (rotacional)
        if (rotationInput != 0) angularVel += rotationInput * ANG_IMPULSE;
        angularVel += rotationInput * ANG_ACCEL * dt;
        if (angularVel > 0) angularVel = Math.max(0, angularVel - ANG_DAMP * dt);
        else angularVel = Math.min(0, angularVel + ANG_DAMP * dt);
        angle += angularVel * dt;
        if (angle > Math.PI) angle -= Math.PI * 2;
        if (angle < -Math.PI) angle += Math.PI * 2;

        double accel = 0.0;
        if (forward) accel += ACCEL;
        if (backward) accel -= BACK_ACCEL;
        if (turbo) accel *= TURBO_MULT;

        if (Math.abs(accel) > 1e-12) {
            ax = Math.cos(angle) * accel;
            ay = Math.sin(angle) * accel;
        } else {
            double v = Math.sqrt(vx*vx + vy*vy);
            if (v > 1e-8) {
                ax = - (vx / v) * FRICTION_ACCEL;
                ay = - (vy / v) * FRICTION_ACCEL;
            } else { ax = 0; ay = 0; }
        }

        vx += ax * dt;
        vy += ay * dt;
        double sp = Math.sqrt(vx*vx + vy*vy);
        if (sp > MAX_SPEED) { double s = MAX_SPEED / sp; vx *= s; vy *= s; }
        if (Math.abs(vx) < 1e-5) vx = 0;
        if (Math.abs(vy) < 1e-5) vy = 0;

        x += vx * dt;
        y += vy * dt;

        int r = getRadius();
        if (x - r < 0) { x = r; vx = -vx; ax = -ax; }
        if (x + r > width) { x = width - r; vx = -vx; ax = -ax; }
        if (y - r < 0) { y = r; vy = -vy; ay = -ay; }
        if (y + r > height) { y = height - r; vy = -vy; ay = -ay; }

        handleRoomInteraction(room);
    }
}
