package jmagination.gui;

import jmagination.ConstantsInitializers;
import util.SimpleHSVBufferedImage;

import javax.swing.*;
import java.awt.*;


/**
 * Created by darek on 29.12.2016.
 */
public class ImagePanel3 extends JPanel implements Scrollable {

    SimpleHSVBufferedImage img = null;

    private int maxUnitIncrement = 1;

    public ImagePanel3(SimpleHSVBufferedImage img) {
        super();
        this.img = img;

        setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);

        if(this.img != null) {
            Dimension dim = new Dimension(img.getWidth(), img.getHeight());
            setPreferredSize(dim);
        }


        //Let the user scroll by dragging to outside the window.
        setAutoscrolls(true); //enable synthetic drag events
    }

    public void setImage(SimpleHSVBufferedImage bufferedImage) {
        img = bufferedImage;
        Dimension dim = new Dimension(img.getWidth(), img.getHeight());
        setPreferredSize(dim);
        revalidate();
        repaint();
    }

    public SimpleHSVBufferedImage getImage() {
        return img;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, null);
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
