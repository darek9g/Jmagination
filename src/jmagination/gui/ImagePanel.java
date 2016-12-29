package jmagination.gui;

import jmagination.ConstantsInitializers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

/**
 * Created by darek on 29.12.2016.
 */
public class ImagePanel extends JPanel implements MouseListener, MouseMotionListener {

    BufferedImage img;
    Point origin = new Point(0,0);

    ScrollView scrollView;

    public ImagePanel(BufferedImage img) {
        super();
        this.img = img;

        setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);

        Dimension dim = new Dimension(img.getWidth(), img.getHeight());

        setMinimumSize(dim);
        setPreferredSize(dim);
        addMouseMotionListener(this);
        addMouseListener(this);
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

    public void resizeMaximize(JComponent container) {
        Dimension dim = container.getSize();
        setPreferredSize(dim);
    }


    public void setOriginCmd(String command) {

        double paneWidth = getSize().getWidth();
        double paneHeight = getSize().getHeight();
        double imgWidth = img.getWidth();
        double imgHeight = img.getHeight();

        double oriX = origin.getX();
        double oriY = origin.getY();

        switch(command) {
            case PresenterTabImage.CMD_TL:
                origin.setLocation(0,0);
                break;
            case PresenterTabImage.CMD_TR:
                origin.setLocation( paneWidth - imgWidth, 0 );
                break;
            case PresenterTabImage.CMD_CE:
                origin.setLocation( paneWidth / 2 - imgWidth / 2, paneHeight / 2 - imgHeight / 2 );
                break;
            case PresenterTabImage.CMD_BL:
                origin.setLocation( 0 , paneHeight - imgHeight );
                break;
            case PresenterTabImage.CMD_BR:
                origin.setLocation( paneWidth - imgWidth , paneHeight - imgHeight );
                break;
            case PresenterTabImage.CMD_AT:
                origin.setLocation( oriX , 0 );
                break;
            case PresenterTabImage.CMD_AB:
                origin.setLocation( oriX , paneHeight - imgHeight );
                break;
            case PresenterTabImage.CMD_AL:
                origin.setLocation( 0 , oriY );
                break;
            case PresenterTabImage.CMD_AR:
                origin.setLocation( paneWidth - imgWidth , oriY );
                break;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, (int)origin.getX(), (int)origin.getY(), null);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        origin.setLocation( origin.getX() + e.getPoint().getX() - scrollView.getMouseInitialPosition().getX(), origin.getY() + e.getPoint().getY() - scrollView.getMouseInitialPosition().getY());
//            System.out.printf("Drag: %s\n",origin.toString());
        repaint();
        scrollView.setMouseInitialPosition(e.getPoint());
        scrollView.updateSymbols(origin);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        scrollView = new ScrollView(this.getSize(),new Dimension(img.getWidth(), img.getHeight()), e.getPoint(), new Point( (int) (e.getX() - origin.getX()) , (int) (e.getY() - origin.getY()) ));
        scrollView.updateSymbols(origin);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        scrollView.closeWindow();
        scrollView = null;

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setCursor(new Cursor(Cursor.HAND_CURSOR));

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
