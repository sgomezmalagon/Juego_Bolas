// Ventana principal - comentarios cortos (2º DAM)
package view;

import model.GameModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MainFrame extends JFrame {
    private final GamePanel gamePanel;
    private final SidePanel sidePanel;
    private final GameModel model;
    private final StartPanel startPanel;
    private final JPanel cards; // CardLayout

    public MainFrame(GameModel model) {
        super("Juego Bolas");
        this.model = model;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // paneles
        this.gamePanel = new GamePanel(model);
        this.sidePanel = new SidePanel();
        this.startPanel = new StartPanel();

        cards = new JPanel(new CardLayout());
        cards.add(startPanel, "start");
        cards.add(gamePanel, "game");

        add(cards, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);

        // actualizar límites del modelo al redimensionar
        gamePanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                model.setBounds(gamePanel.getWidth(), gamePanel.getHeight());
            }
        });
    }

    public StartPanel getStartPanel() { return startPanel; }

    // muestra el juego
    public void showGame() {
        CardLayout cl = (CardLayout) (cards.getLayout());
        cl.show(cards, "game");
        SwingUtilities.invokeLater(() -> {
            gamePanel.requestFocusInWindow();
            gamePanel.requestFocus();
        });
    }

    public GamePanel getGamePanel() { return gamePanel; }
    public SidePanel getSidePanel() { return sidePanel; }
}
