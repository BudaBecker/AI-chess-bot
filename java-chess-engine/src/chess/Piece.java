package chess;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import UI.ChessPanel;

public abstract class Piece {

    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public Color color;
    public int moveCount = 0;

    public Piece(int col, int row, Color color) {
        this.col = col;
        this.row = row;
        this.color = color;
        this.preCol = col;
        this.preRow = row;

        this.x = getPosX();
        this.y = getPosY();
    }

    public BufferedImage getImage(String name) {
        BufferedImage image = null;

        try {
            // To package your resources into a JAR file, place the res folder inside src
            // and use this function:
            // image = ImageIO.read(getClass().getResourceAsStream( path ));
            image = ImageIO.read(new File("res/pieces/" + name + ".png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public int getPosX() {
        return this.col * ChessPanel.tileSize;
    }

    public int getPosY() {
        return this.row * ChessPanel.tileSize;
    }

    public int getCol() {
        return (this.x + ChessPanel.tileSize / 2) / ChessPanel.tileSize;
    }

    public int getRow() {
        return (this.y + ChessPanel.tileSize / 2) / ChessPanel.tileSize;
    }

    // Returns an 8x8 grid where true means the piece can legally move to that
    // square
    public abstract boolean[][] possibleMoves(ArrayList<Piece> pieces);

    protected Piece getPieceAt(int col, int row, ArrayList<Piece> pieces) {
        for (Piece p : pieces) {
            if (p.col == col && p.row == row) {
                return p;
            }
        }
        return null;
    }

    // True if the square is in bounds and not occupied by a friendly piece
    protected boolean canMoveTo(int col, int row, ArrayList<Piece> pieces) {
        if (col < 0 || col > 7 || row < 0 || row > 7)
            return false;
        Piece target = getPieceAt(col, row, pieces);
        return target == null || target.color != this.color;
    }
}
