package UI;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

public class Mouse extends MouseAdapter {

    public int x, y;
    public boolean pressed;

    // Single-click tracking for panel buttons — consumed in update()
    public boolean clicked;
    public int clickX, clickY;

    // Right-click tracking for editor — consumed in update()
    public boolean rightClicked;
    public int rightClickX, rightClickY;

    @Override
    public void mousePressed(MouseEvent e) {
        pressed = true;
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressed = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            clicked = true;
            clickX = e.getX();
            clickY = e.getY();
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            rightClicked = true;
            rightClickX = e.getX();
            rightClickY = e.getY();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }

}
