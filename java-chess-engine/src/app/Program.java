package app;

import javax.swing.JFrame;

import UI.ChessPanel;

public class Program {
    public static void main(String[] args) {

        JFrame window = new JFrame("AI-Chess Engine");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        ChessPanel chessPanel = new ChessPanel();
        window.add(chessPanel);
        window.pack();

        window.setLocationRelativeTo(null); // create windows at the center of the screen
        window.setVisible(true);

        chessPanel.startChessGameThread();
    }
}
