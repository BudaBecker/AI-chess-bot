package app;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import UI.ChessPanel;

public class Program {
    public static void main(String[] args) {

        JFrame window = new JFrame("AI-Chess Engine");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setMinimumSize(new Dimension(1050, 800));

        // Window icon
        try {
            BufferedImage icon = ImageIO.read(new File("res/pieces/w_king.png"));
            window.setIconImage(icon);
        } catch (IOException ignored) {}

        ChessPanel chessPanel = new ChessPanel();
        window.add(chessPanel);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");

        JMenuItem newGame = new JMenuItem("New Game");
        newGame.setAccelerator(KeyStroke.getKeyStroke("control N"));
        newGame.addActionListener(e -> chessPanel.resetGame());

        JMenuItem undo = new JMenuItem("Undo");
        undo.setAccelerator(KeyStroke.getKeyStroke("control Z"));
        undo.addActionListener(e -> chessPanel.undoLastMove());

        JMenuItem savePgn = new JMenuItem("Save PGN");
        savePgn.setAccelerator(KeyStroke.getKeyStroke("control S"));
        savePgn.addActionListener(e -> chessPanel.exportPgn());

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));

        gameMenu.add(newGame);
        gameMenu.add(undo);
        gameMenu.addSeparator();
        gameMenu.add(savePgn);
        gameMenu.addSeparator();
        gameMenu.add(exit);
        menuBar.add(gameMenu);
        window.setJMenuBar(menuBar);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        chessPanel.startChessGameThread();
    }
}
