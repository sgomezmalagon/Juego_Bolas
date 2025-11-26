package view;

import javax.swing.*;
import java.awt.*;

// Panel lateral con botones para controlar el juego. Comentado y sencillo.
// Aquí explico qué hace cada botón y por qué lo dejamos público.
public class SidePanel extends JPanel {
    // Botones públicos para que el controlador (GameController) pueda añadir listeners.
    public final JButton addButton = new JButton("Agregar Bola");
    public final JButton clearButton = new JButton("Eliminar Todas");
    public final JButton addPlayerButton = new JButton("Bola Controlable");
    // Nuevos botones para cargar imágenes
    public final JButton loadBgButton = new JButton("Cargar Fondo");
    public final JButton loadBallTexButton = new JButton("Cargar Textura Bola");

    public SidePanel() {
        // GridBagLayout porque es fácil de alinear verticalmente sin complicarse mucho
        setLayout(new GridBagLayout());
        // Pedimos un panel lateral pequeño para que la pantalla de juego sea grande
        setPreferredSize(new Dimension(140, 700));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // una columna
        gbc.gridy = GridBagConstraints.RELATIVE; // cada componente baja una fila
        gbc.fill = GridBagConstraints.HORIZONTAL; // los botones ocupan todo el ancho
        gbc.insets = new Insets(8, 8, 8, 8); // espacio alrededor

        // Añadimos los botones en orden. El controlador se encarga de los listeners.
        add(addButton, gbc);
        add(addPlayerButton, gbc);
        add(clearButton, gbc);
        // Separador visual
        add(new JSeparator(), gbc);
        add(loadBgButton, gbc);
        add(loadBallTexButton, gbc);

        // Añadimos un título (etiqueta). No tiene listener, es solo informativa.
        add(new JLabel("Juego Bolas MVC"), gbc);
    }
}
