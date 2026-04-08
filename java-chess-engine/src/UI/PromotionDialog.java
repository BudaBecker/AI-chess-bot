package UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import chess.Color;
import chess.Piece;
import chess.pieces.Bishop;
import chess.pieces.Knight;
import chess.pieces.Queen;
import chess.pieces.Rook;

/**
 * Modal dialog for pawn promotion.
 * Presents the player with 4 piece choices (Q, R, B, N) as clickable image buttons.
 */
public class PromotionDialog extends JDialog {

    private static final int BTN_SIZE = 90;

    private String selected = "Queen"; // default

    public PromotionDialog(Color color) {
        super((Frame) null, "Pawn Promotion", true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { /* force a choice */ }
        });

        String prefix = (color == Color.WHITE) ? "w" : "b";
        String[] names = {"queen", "rook", "bishop", "knight"};
        String[] labels = {"Queen", "Rook", "Bishop", "Knight"};

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new java.awt.Color(50, 50, 50));

        for (int i = 0; i < names.length; i++) {
            final String choice = labels[i];
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(BTN_SIZE, BTN_SIZE));
            btn.setBackground(new java.awt.Color(80, 80, 80));
            btn.setBorderPainted(true);
            btn.setFocusPainted(false);

            try {
                Image img = ImageIO.read(new File("res/pieces/" + prefix + "_" + names[i] + ".png"))
                        .getScaledInstance(BTN_SIZE - 10, BTN_SIZE - 10, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
            } catch (IOException e) {
                btn.setText(choice);
            }

            btn.addActionListener(e -> {
                selected = choice;
                dispose();
            });
            panel.add(btn);
        }

        JLabel title = new JLabel("Choose promotion piece:", JLabel.CENTER);
        title.setForeground(java.awt.Color.WHITE);
        title.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new java.awt.Color(50, 50, 50));
        titlePanel.add(title, BorderLayout.CENTER);

        setLayout(new BorderLayout(0, 5));
        getContentPane().setBackground(new java.awt.Color(50, 50, 50));
        add(titlePanel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    /** Returns the new piece based on the player's selection, placed at (col, row). */
    public Piece getSelectedPiece(int col, int row, Color color) {
        return switch (selected) {
            case "Rook"   -> new Rook(col, row, color);
            case "Bishop" -> new Bishop(col, row, color);
            case "Knight" -> new Knight(col, row, color);
            default       -> new Queen(col, row, color);
        };
    }
}
