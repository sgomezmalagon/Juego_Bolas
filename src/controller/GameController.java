// Controlador principal - comentarios breves (estudiante 2º DAM)
package controller;

import model.GameModel;
import view.MainFrame;
import view.ImageManager;
import util.Debug;

import javax.swing.*;
import java.io.File;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameController {
    private final GameModel model;
    private final MainFrame frame;
    // Usamos un executor para tener hilos programados: uno actualiza la física y otro genera bolas
    // Lo hago así para no bloquear el EDT (la interfaz va por separado).
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    // flag para controlar si los bucles deben seguir
    private volatile boolean running = false;

    public GameController(GameModel model, MainFrame frame) {
        this.model = model;
        this.frame = frame;
        Debug.log("Controller", "Inicializando controlador");
        wireActions(); // conecto botones y acciones

        // START: mostramos la pantalla de inicio -> al pulsar INICIAR se llama a start()
        frame.getStartPanel().setStartAction(() -> {
            frame.showGame();
            start();
        });
    }

    // arrancar bucles si no están ya corriendo
    public void start() {
        if (running) return; // ya está en marcha
        running = true;
        startLoops(); // lanzo hilos de física y generación
        // pedimos foco al panel para que las teclas funcionen (esto se hace en EDT)
        SwingUtilities.invokeLater(() -> frame.getGamePanel().requestFocusInWindow());
    }

    @SuppressWarnings("unused")
    // conectar botones del menú con acciones del modelo
    private void wireActions() {
        // botón: añadir bola automática
        frame.getSidePanel().addButton.addActionListener(e -> {
            model.addBall();
            Debug.log("Controller", "Se añadió una bola automática. Total=" + model.count());
            frame.getGamePanel().requestFocusInWindow();
        });
        // botón: añadir bola controlable (solo 1)
        frame.getSidePanel().addPlayerButton.addActionListener(e -> {
            boolean created = model.addPlayerBall();
            if (created) {
                Debug.log("Controller", "Se añadió una bola de jugador.");
            } else {
                // aviso sencillo si ya existe una
                Debug.log("Controller", "Ya existe una bola controlable");
                JOptionPane.showMessageDialog(frame, "Ya existe una bola controlable.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
            frame.getGamePanel().requestFocusInWindow();
        });
        // botón: eliminar todas las bolas automáticas
        frame.getSidePanel().clearButton.addActionListener(e -> {
            model.clearBalls();
            Debug.log("Controller", "Se limpiaron las bolas automáticas.");
            frame.getGamePanel().requestFocusInWindow();
        });

        // cargar fondo desde archivo (elige carpeta assets/images por defecto)
        frame.getSidePanel().loadBgButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(new File("assets/images"));
            int res = chooser.showOpenDialog(frame);
            if (res == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    var img = ImageManager.loadImage(file);
                    ImageManager.setBackground(img);
                    // repaint en EDT para evitar concurrencia con Swing
                    SwingUtilities.invokeLater(frame.getGamePanel()::repaint);
                    Debug.log("Controller", "Fondo cargado: " + file.getName());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "No se pudo cargar la imagen: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            frame.getGamePanel().requestFocusInWindow();
        });

        // cargar textura de bola (para que aparezcan asteroid1/2)
        frame.getSidePanel().loadBallTexButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(new File("assets/images"));
            int res = chooser.showOpenDialog(frame);
            if (res == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    var img = ImageManager.loadImage(file);
                    ImageManager.setBallTexture(img);
                    // repintar para que se vea la textura nueva
                    SwingUtilities.invokeLater(frame.getGamePanel()::repaint);
                    Debug.log("Controller", "Textura de bola cargada: " + file.getName());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "No se pudo cargar la imagen: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            frame.getGamePanel().requestFocusInWindow();
        });
    }

    @SuppressWarnings("unused")
    // inicia los bucles: física (~60FPS) y generación periódica
    private void startLoops() {
        Debug.log("Controller", "Arrancando bucles");
        final int frameMs = 16; // ~60 fps (objetivo)
        // runnable que calcula dt real y actualiza el modelo
        executor.scheduleAtFixedRate(new Runnable() {
            long last = System.nanoTime();
            int frames = 0;
            @Override
            public void run() {
                if (!running) return; // si nos pidieron parar salimos
                long now = System.nanoTime();
                long delta = now - last;
                last = now;
                // convertimos a milisegundos reales para el modelo (dt en ms)
                double dtMs = Math.max(1, delta / 1_000_000.0);
                model.update(dtMs); // actualizamos física
                frames++;
                if (frames >= 60) {
                    frames = 0;
                    Debug.log("Physics", "update dt=" + dtMs + " ms");
                }
                // pedimos repintado en EDT (sí o sí en Swing)
                SwingUtilities.invokeLater(frame.getGamePanel()::repaint);
            }
        }, 0, frameMs, TimeUnit.MILLISECONDS);

        // generador: cada 2s añadimos una bola automática (se puede quitar si no quieres)
        executor.scheduleAtFixedRate(() -> {
            if (!running) return;
            model.addBall();
            Debug.log("Controller", "Bola generada automáticamente. Total=" + model.count());
        }, 2, 2, TimeUnit.SECONDS);
    }

    // parar ejecución: detenemos el executor
    public void shutdown() {
        Debug.log("Controller", "Shutdown solicitado");
        running = false;
        executor.shutdownNow();
    }
}
