package jmagination.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by darek on 22.01.2017.
 */
public class ImagePanel2 extends JPanel{

    private BufferedImage img;

    {
        img = null;
    }

    public void setImage(BufferedImage bufferedImage) {
        img = bufferedImage;
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, null);
    }
}
