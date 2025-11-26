/*
 * Ball (bola automática)
 * - Representa una bola que se mueve sola y rebota en paredes y habitación.
 * - Unidades: velocidad en px/ms y aceleración en px/ms^2 para que sea "de verdad".
 * - variantIndex: número aleatorio para elegir qué textura usar (asteroid1/asteroid2, etc.).
 */
package model;

import java.awt.Color;
import java.util.Random;

// Bola automática: posición, velocidad (px/ms) y aceleración (px/ms^2)
public class Ball {
    private static final Random RNG = new Random();
    private static final int MIN_RADIUS = 10;
    private static final int MAX_RADIUS = 30;

    // posición y velocidad
    protected double x;
    protected double y;
    protected double vx; // ahora protected para que subclases puedan acceder
    protected double vy;
    // Aceleración en px/ms^2 (puede ser positiva o negativa)
    protected double ax;
    protected double ay;
    private final int radius; // radio en píxeles
    private final Color color; // color para dibujar
    private boolean inRoom = false; // true si la bola está dentro de la habitación

    // Límite de velocidad para evitar que sean demasiado rápidas (px/ms)
    private static final double MAX_SPEED = 0.8; // 800 px/s

    // Índice de variante para textura (para elegir entre varias imágenes)
    private final int variantIndex;

    public Ball(int boundsWidth, int boundsHeight) {
        // Elegimos un radio aleatorio para que no todas sean iguales
        this.radius = RNG.nextInt(MAX_RADIUS - MIN_RADIUS + 1) + MIN_RADIUS;
        // Posicionamos la bola dentro de los límites teniendo en cuenta el radio
        this.x = RNG.nextInt(Math.max(1, boundsWidth - 2 * radius)) + radius;
        this.y = RNG.nextInt(Math.max(1, boundsHeight - 2 * radius)) + radius;
        // Velocidad aleatoria en px/ms
        this.vx = ((RNG.nextDouble() * 2 - 1) * (50 + RNG.nextInt(100))) / 1000.0;
        this.vy = ((RNG.nextDouble() * 2 - 1) * (50 + RNG.nextInt(100))) / 1000.0;
        // Aceleración pequeña aleatoria
        this.ax = (RNG.nextDouble() * 2 - 1) * 0.0001;
        this.ay = (RNG.nextDouble() * 2 - 1) * 0.0001;
        // Color aleatorio para fallback si no hay textura
        this.color = new Color(RNG.nextInt(256), RNG.nextInt(256), RNG.nextInt(256));
        // Variant index aleatorio para repartir las texturas
        this.variantIndex = RNG.nextInt(10_000); // grande para mezclar bien con % count
    }

    // actualizar posición y velocidad (dt en ms)
    public void update(double dt, int width, int height, Room room) {
        // dt está en ms y vx/vy en px/ms -> movimiento correcto: px
        // Primero actualizamos la velocidad usando la aceleración: v += a * dt
        vx += ax * dt;
        vy += ay * dt;

        // Limitamos la velocidad absoluta para que no explote
        limitSpeed();

        // Luego movemos según la velocidad actual
        x += vx * dt;
        y += vy * dt;

        // Después de mover, comprobamos la interacción con la habitación
        handleRoomInteraction(room);

        // También comprobamos rebote con los bordes de la ventana
        bounce(width, height);
    }

    // Limitar la velocidad total (magnitud) a MAX_SPEED
    private void limitSpeed() {
        double speed = Math.sqrt(vx * vx + vy * vy);
        if (speed > MAX_SPEED) {
            double scale = MAX_SPEED / speed;
            vx *= scale;
            vy *= scale;
        }
    }

    // Maneja la lógica para entrar/salir/rebotar con la habitación
    protected void handleRoomInteraction(Room room) {
        if (room == null) return; // si no hay habitación, no hacemos nada

        int centerX = getX();
        int centerY = getY();
        boolean nowInside = room.contains(centerX, centerY);

        if (!inRoom && nowInside) {
            // Si antes no estaba dentro y ahora sí, intenta entrar
            if (room.tryEnter(this)) {
                // Si tryEnter devuelve true, esta bola se convierte en el ocupante
                inRoom = true;
            } else {
                // Si no pudo entrar (la habitación está ocupada), rebotamos con la habitación
                bounceWithRoom(room);
            }
        } else if (inRoom && !nowInside) {
            // Si estaba dentro y ya no está, avisamos que se va
            room.tryLeave(this);
            inRoom = false;
        } else if (!inRoom && room.isOccupied() && room.intersects(centerX, centerY, radius)) {
            // Si la habitación está ocupada y la bola choca con sus paredes, rebotar
            bounceWithRoom(room);
        }
    }

    // Calcula un rebote simple cuando una bola choca con la habitación
    private void bounceWithRoom(Room room) {
        java.awt.Rectangle bounds = room.getBounds();
        int cx = getX();
        int cy = getY();

        // Calculamos la distancia a cada pared para saber con cuál colisionamos
        double leftDist = Math.abs(cx - bounds.x);
        double rightDist = Math.abs(cx - (bounds.x + bounds.width));
        double topDist = Math.abs(cy - bounds.y);
        double bottomDist = Math.abs(cy - (bounds.y + bounds.height));

        double minDist = Math.min(Math.min(leftDist, rightDist), Math.min(topDist, bottomDist));

        if (minDist == leftDist || minDist == rightDist) {
            // Colisión lateral -> invertimos la velocidad horizontal
            vx = -vx;
            // Invertir aceleración horizontal para que no vuelva a empujar hacia la pared
            ax = -ax;
            if (minDist == leftDist) {
                x = bounds.x - radius - 1; // colocamos la bola justo fuera de la pared
            } else {
                x = bounds.x + bounds.width + radius + 1;
            }
        } else {
            // Colisión superior/inferior -> invertimos la velocidad vertical
            vy = -vy;
            ay = -ay;
            if (minDist == topDist) {
                y = bounds.y - radius - 1;
            } else {
                y = bounds.y + bounds.height + radius + 1;
            }
        }

        // Añadimos una pequeña variación aleatoria (antes 20 px/s) -> ahora 0.02 px/ms
        vx += ((RNG.nextDouble() * 2 - 1) * 20) / 1000.0;
        vy += ((RNG.nextDouble() * 2 - 1) * 20) / 1000.0;
    }

    // Rebote en los bordes de la ventana (izquierda/derecha/arriba/abajo)
    private void bounce(int width, int height) {
        boolean bounced = false;
        if (x - radius < 0) {
            x = radius;
            vx = -vx;
            ax = -ax;
            bounced = true;
        } else if (x + radius > width) {
            x = width - radius;
            vx = -vx;
            ax = -ax;
            bounced = true;
        }
        if (y - radius < 0) {
            y = radius;
            vy = -vy;
            ay = -ay;
            bounced = true;
        } else if (y + radius > height) {
            y = height - radius;
            vy = -vy;
            ay = -ay;
            bounced = true;
        }
        if (bounced) {
            // Si ha rebotado con la ventana, le damos una pequeña variación a la velocidad
            vx += ((RNG.nextDouble() * 2 - 1) * 20) / 1000.0;
            vy += ((RNG.nextDouble() * 2 - 1) * 20) / 1000.0;
            // Limitamos de nuevo por si la variación lo dejó muy rápido
            limitSpeed();
        }
    }

    // Getters sencillos: devolvemos la posición redondeada para dibujar
    public int getX() { return (int) Math.round(x); }
    public int getY() { return (int) Math.round(y); }
    public int getRadius() { return radius; }
    public Color getColor() { return color; }
    public boolean isInRoom() { return inRoom; }

    // Nuevo getter: índice de variante de textura
    public int getVariantIndex() { return variantIndex; }
}
