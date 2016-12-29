package jmagination.gui;

import jmagination.ConstantsInitializers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

/**
 * Created by darek on 29.12.2016.
 */
public class ImagePanel3 extends JPanel implements Scrollable, MouseMotionListener {

    BufferedImage img = null;

    private int maxUnitIncrement = 1;

    public ImagePanel3(BufferedImage img) {
        super();
        this.img = img;

        setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);

        if(this.img != null) {
            Dimension dim = new Dimension(img.getWidth(), img.getHeight());
            setPreferredSize(dim);
        }


        //Let the user scroll by dragging to outside the window.
        setAutoscrolls(true); //enable synthetic drag events
        addMouseMotionListener(this); //handle mouse drags
    }

    public void setImage(BufferedImage bufferedImage) {
        img = bufferedImage;
        Dimension dim = new Dimension(img.getWidth(), img.getHeight());
        setPreferredSize(dim);
        revalidate();
        repaint();
    }

    public BufferedImage getImage() {
        return img;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, null);
    }

    //Methods required by the MouseMotionListener interface:
    public void mouseMoved(MouseEvent e) { }
    public void mouseDragged(MouseEvent e) {
        //The user is dragging us, so scroll!
        Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
        scrollRectToVisible(r);
    }

    public Dimension getPreferredSize() {
        if (img == null) {
            return new Dimension(320, 480);
        } else {
            return super.getPreferredSize();
        }
    }

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect,
                                          int orientation,
                                          int direction) {
        //Get the current position.
        int currentPosition = 0;
        if (orientation == SwingConstants.HORIZONTAL) {
            currentPosition = visibleRect.x;
        } else {
            currentPosition = visibleRect.y;
        }

        //Return the number of pixels between currentPosition
        //and the nearest tick mark in the indicated direction.
        if (direction < 0) {
            int newPosition = currentPosition -
                    (currentPosition / maxUnitIncrement)
                            * maxUnitIncrement;
            return (newPosition == 0) ? maxUnitIncrement : newPosition;
        } else {
            return ((currentPosition / maxUnitIncrement) + 1)
                    * maxUnitIncrement
                    - currentPosition;
        }
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                           int orientation,
                                           int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return visibleRect.width - maxUnitIncrement;
        } else {
            return visibleRect.height - maxUnitIncrement;
        }
    }

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public void setMaxUnitIncrement(int pixels) {
        maxUnitIncrement = pixels;
    }

}
