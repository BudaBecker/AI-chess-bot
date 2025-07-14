package UI;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class ChessPanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    final int tileSize = 100;
    final int screenWidth = (tileSize * 8) + 250; // Board + InfoPanel
    final int screenHeight = tileSize * 8;
    final int FPS = 60;

    Thread chessGameThread;

    public ChessPanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true); // improve game's rendering performance
    }

    public void startChessGameThread() {
        chessGameThread = new Thread(this);
        chessGameThread.start();
    }

    @Override
    public void run() {

        // Show and Manage FPS
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        // Main game loop
        while (chessGameThread != null) {

            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += currentTime - lastTime;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1000000000) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.WHITE);

        g2.fillRect(0, 0, tileSize * 8, tileSize * 8);

        g2.dispose();
    }
}
