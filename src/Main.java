// Programa principal 
import model.GameModel;
import view.MainFrame;
import controller.GameController;
import javax.swing.*;
import imagenes.ImageAssets;

public class Main {
    public static void main(String[] args) {
        // Ejecutar GUI en EDT
        SwingUtilities.invokeLater(() -> {
            // Cargo imágenes por defecto (fondo y texturas)
            ImageAssets.loadDefaults();

            // Modelo y vista
            GameModel model = new GameModel(1000, 700);
            MainFrame frame = new MainFrame(model);

            // Controlador que conecta modelo y vista
            GameController controller = new GameController(model, frame);

            frame.setVisible(true);

            // Parar hilos al cerrar la ventana
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    controller.shutdown();
                }
            });
        });
    }
}
