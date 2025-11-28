/*
 * GamePanel (MVC - Vista)
 * - Este panel pinta TODO lo que se ve del juego: fondo, habitación, bolas y naves.
 * - Uso Graphics2D con antialiasing para que se vea suave.
 */
package view;

import model.Ball;
import model.GameModel;
import model.PlayerBall;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.awt.image.BufferedImage;

// Panel que pinta el juego
// Aquí explico qué hace cada parte para que sea fácil de entender.
public class GamePanel extends JPanel {
    private final GameModel model;
    // Guardamos las teclas presionadas para saber qué direccion quiere el jugador
    private final Set<Integer> pressedKeys = new HashSet<>();

    public GamePanel(GameModel model) {
        this.model = model;
        // Preferimos un tamaño grande para que se vea más claro
        setPreferredSize(new Dimension(1000, 700));
        // Fondo blanco como pidió el usuario
        setBackground(Color.WHITE);
        // Doble buffer para que se pinte suavemente y no parpadee
        setDoubleBuffered(true);
        // Necesitamos recibir eventos de teclado, así que hacemos focusable
        setFocusable(true);
        // Configuramos listeners de teclado y ratón
        setupKeyListeners();
        setupKeyBindings(); // <-- Añadido: key bindings para WHEN_IN_FOCUSED_WINDOW
        setupMouseListeners();
        // Listener de foco: si perdemos foco limpiamos teclas para que no quede "pegado"
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                pressedKeys.clear();
                updatePlayerMovement();
            }
        });
        // Pedimos foco para que cuando empiece la app ya podamos usar WASD
        requestFocusInWindow();
    }

    // Si haces click en el panel pedimos foco para recibir teclado
    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });
    }

    // Listener de teclado: guardamos teclas presionadas y actualizamos movimiento
    private void setupKeyListeners() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Añadimos la tecla al conjunto (para soportar varias teclas a la vez)
                pressedKeys.add(e.getKeyCode());
                updatePlayerMovement();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // Quitamos la tecla cuando se suelta
                pressedKeys.remove(e.getKeyCode());
                updatePlayerMovement();
            }
        });
    }

    // Configura Key Bindings para que las teclas funcionen aunque el panel no tenga foco
    private void setupKeyBindings() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        // Helper para registrar tecla (pressed/released)
        java.util.function.BiConsumer<Integer, String> reg = (key, name) -> {
            String pressed = name + ":pressed";
            String released = name + ":released";
            im.put(KeyStroke.getKeyStroke(key, 0, false), pressed);
            im.put(KeyStroke.getKeyStroke(key, 0, true), released);
            am.put(pressed, new AbstractAction() {
                @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                    pressedKeys.add(key);
                    updatePlayerMovement();
                }
            });
            am.put(released, new AbstractAction() {
                @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                    pressedKeys.remove(key);
                    updatePlayerMovement();
                }
            });
        };

        // WASD
        reg.accept(KeyEvent.VK_W, "W");
        reg.accept(KeyEvent.VK_A, "A");
        reg.accept(KeyEvent.VK_S, "S");
        reg.accept(KeyEvent.VK_D, "D");
        // Flechas
        reg.accept(KeyEvent.VK_UP, "UP");
        reg.accept(KeyEvent.VK_LEFT, "LEFT");
        reg.accept(KeyEvent.VK_DOWN, "DOWN");
        reg.accept(KeyEvent.VK_RIGHT, "RIGHT");
        // Espacio
        reg.accept(KeyEvent.VK_SPACE, "SPACE");
    }

    // Calcula la dirección de movimiento del jugador según teclas
    // y propulsión (tecla espacio). También normaliza diagonales.
    private void updatePlayerMovement() {
        // Control directo WASD: calculamos dx/dy y lo pasamos a la PlayerBall
        double dx = 0;
        double dy = 0;
        boolean turbo = false;

        if (pressedKeys.contains(KeyEvent.VK_A) || pressedKeys.contains(KeyEvent.VK_LEFT)) dx -= 1;
        if (pressedKeys.contains(KeyEvent.VK_D) || pressedKeys.contains(KeyEvent.VK_RIGHT)) dx += 1;
        if (pressedKeys.contains(KeyEvent.VK_W) || pressedKeys.contains(KeyEvent.VK_UP)) dy -= 1;
        if (pressedKeys.contains(KeyEvent.VK_S) || pressedKeys.contains(KeyEvent.VK_DOWN)) dy += 1;
        if (pressedKeys.contains(KeyEvent.VK_SPACE)) turbo = true;

        // Normalizar si es diagonal
        if (dx != 0 && dy != 0) {
            double len = Math.sqrt(dx*dx + dy*dy);
            dx /= len; dy /= len;
        }

        for (PlayerBall pb : model.getPlayerBalls()) {
            pb.setDirectMovement(dx, dy);
            pb.setTurbo(turbo);
        }
    }

    // Método principal de dibujo: se llama muchas veces por segundo
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Habilitar antialiasing para formas suaves
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 0) Pintar fondo si hay imagen cargada
        BufferedImage bg = ImageManager.getBackground();
        if (bg != null) {
            // Usamos el fondo escalado en cache según tamaño del panel
            Image scaledBg = ImageManager.getScaledBackground(getWidth(), getHeight());
            g2.drawImage(scaledBg, 0, 0, this);
        } else {
            // Fondo por defecto blanco (ya lo pone setBackground), pero pintamos por si acaso
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // 1) Dibujar la habitación (rectángulo en el centro)
        java.awt.Rectangle roomBounds = model.getRoom().getBounds();
        // Fondo gris claro para distinguirla (si hay fondo, le ponemos un marco)
        g2.setColor(new Color(200, 200, 200, 180));
        g2.fillRect(roomBounds.x, roomBounds.y, roomBounds.width, roomBounds.height);
        g2.setColor(new Color(60, 60, 60));
        g2.drawRect(roomBounds.x, roomBounds.y, roomBounds.width, roomBounds.height);

        // Si está ocupada, pintamos encima con un color rojizo para que se vea
        if (model.getRoom().isOccupied()) {
            g2.setColor(new Color(255, 200, 200, 180));
            g2.fillRect(roomBounds.x, roomBounds.y, roomBounds.width, roomBounds.height);
            g2.setColor(Color.RED);
            g2.drawRect(roomBounds.x, roomBounds.y, roomBounds.width, roomBounds.height);
        }

        // 2) Dibujar las bolas normales (si hay texturas, las usamos) else círculos simples
        List<Ball> balls = model.getSnapshot();
        for (Ball b : balls) {
            int r = b.getRadius();
            int drawX = b.getX() - r;
            int drawY = b.getY() - r;

            // Intentamos obtener una textura para esta bola según su variante y radio
            Image tex = null;
            if (view.ImageManager.getBallTexturesCount() > 0) {
                tex = view.ImageManager.getBallTextureForRadiusVariant(r, b.getVariantIndex());
            }

            if (tex != null) {
                // Si hay textura, la dibujamos centrada en la posición
                g2.drawImage(tex, drawX, drawY, r * 2, r * 2, this);
            } else {
                // Dibujamos siempre como círculo de color
                g2.setColor(b.getColor());
                g2.fillOval(drawX, drawY, r * 2, r * 2);
            }
            // Si está en la habitación, lo marcamos con borde
            if (b.isInRoom()) {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(drawX - 2, drawY - 2, r * 2 + 4, r * 2 + 4);
            }
        }

        // 3) Dibujar las bolas-jugador también como círculos simples
        List<PlayerBall> playerBalls = model.getPlayerBalls();
        for (PlayerBall p : playerBalls) {
            int r = p.getRadius();
            int drawX = p.getX() - r;
            int drawY = p.getY() - r;

            // Si es la bola controlada, dibujamos un contorno amarillo simple
            if (p.isControlled()) {
                g2.setColor(new Color(255, 220, 0)); // amarillo
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(drawX - 3, drawY - 3, r * 2 + 6, r * 2 + 6);
                // Indicador de dirección: línea desde el centro hacia la punta según ángulo
                double ang = p.getAngle();
                int len = r + 8;
                int ix = p.getX() + (int) Math.round(Math.cos(ang) * len);
                int iy = p.getY() + (int) Math.round(Math.sin(ang) * len);
                g2.setColor(new Color(255, 200, 0));
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(p.getX(), p.getY(), ix, iy);

                // HUD: velocidad y turbo en la esquina superior izquierda
                double speed = Math.sqrt(p.getVx() * p.getVx() + p.getVy() * p.getVy());
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.setColor(new Color(30, 30, 30));
                g2.drawString(String.format("Vel: %.2f px/ms", speed), 8, 18);
                g2.drawString("Turbo: " + (p.isTurbo() ? "ON" : "OFF"), 8, 34);
            }

            g2.setColor(p.getColor());
            g2.fillOval(drawX, drawY, r * 2, r * 2);
            if (p.isInRoom()) {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(drawX - 3, drawY - 3, r * 2 + 6, r * 2 + 6);
            }
        }
    }
}
