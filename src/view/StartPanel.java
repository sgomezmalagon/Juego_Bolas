package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

// Panel de inicio que muestra los controles y un botón START.
public class StartPanel extends JPanel {
    private final JButton startButton = new JButton("INICIAR");

    public StartPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30)); // fondo oscuro para la pantalla de inicio

        // Título grande
        JLabel title = new JLabel("Juego de Bolas", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(24, 12, 12, 12));
        add(title, BorderLayout.NORTH);

        // Área con los controles explicados (WASD, espacio, botones)
        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setOpaque(false);
        info.setForeground(Color.WHITE);
        info.setFont(new Font("Monospaced", Font.PLAIN, 14));
        info.setText(
                "Controles:\n" +
                " - Mover: W / A / S / D o Flechas\n" +
                " - Propulsión: Barra Espaciadora\n" +
                " - Agregar bola automática: Botón 'Agregar Bola'\n" +
                " - Agregar bola controlable: Botón 'Bola Controlable'\n" +
                " - Eliminar todas: Botón 'Eliminar Todas'\n\n" +
                "Objetivo:\n" +
                " - Observa cómo rebotan las bolas.\n" +
                " - Solo una bola puede entrar en la habitación central.\n"
        );
        info.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Centrar el texto en un panel para que quede bonito
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(info, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // Panel inferior con botón START y pequeña nota
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        startButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        startButton.setPreferredSize(new Dimension(180, 40));
        JPanel btnWrap = new JPanel();
        btnWrap.setOpaque(false);
        btnWrap.add(startButton);
        bottom.add(btnWrap, BorderLayout.CENTER);

        JLabel hint = new JLabel("Haz clic en INICIAR para empezar", SwingConstants.CENTER);
        hint.setForeground(Color.LIGHT_GRAY);
        hint.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));
        bottom.add(hint, BorderLayout.SOUTH);

        add(bottom, BorderLayout.SOUTH);
    }

    // Permite que quien use este panel (MainFrame) asigne la acción del botón start.
    public void setStartAction(Runnable action) {
        // Quitamos listeners anteriores y ponemos uno nuevo
        for (ActionListener al : startButton.getActionListeners()) startButton.removeActionListener(al);
        startButton.addActionListener(e -> action.run());
    }
}

