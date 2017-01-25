package jmagination.gui;

import jmagination.ConstantsInitializers;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by darek on 25.01.2017.
 */
public class ImagePanel4 extends ImagePanel3 implements MouseListener, MouseMotionListener{

    Polygon finalLine;
    ArrayList<Point> handles;
    Point handlePressed = null;
    ArrayList<ActionListener> actionListeners;
    private int actionEventId = 0;

    private int handleSize = 10;

    private int minXSetup;
    private int maxXSetup;
    private int minYSetup;
    private int maxYSetup;

    private boolean drawFeatures;

    {
        finalLine = new Polygon();
        handles = new ArrayList<>();
        actionListeners = new ArrayList<>();

        drawFeatures = false;

    }


    public ImagePanel4(BufferedImage img) {
        super(img);
    }

    @Override
    public void setImage(BufferedImage bufferedImage) {
        super.setImage(bufferedImage);

        // temporary
        enableFeatures();

        // temporary
        setInitialHandles();

    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if(drawFeatures == false) { return; }


        graphics.setColor(ConstantsInitializers.GUI_CHARTS_BG_COLOR);

        // rysowanie uchwytów i etykiet narzędzi edycji w tle
        for(Point p: handles) {
            if(p!=handlePressed) {
                graphics.setColor(new Color(255, 175, 175));
                if(p.x != 0) {
                    graphics.drawLine(xToPict(p.x), yToPict(minYSetup), xToPict(p.x), yToPict(maxYSetup));
                }
            }

        }

        for(Point p: handles) {
            if(p==handlePressed) {
                graphics.setColor(new Color(175, 255, 175));
                graphics.drawLine(xToPict(p.x), yToPict(minYSetup), xToPict(p.x), yToPict(maxYSetup));
            } else {
                graphics.setColor(new Color(255, 175, 175));
            }

            graphics.fillOval(xToPict(p.x - handleSize / 2 ), yToPict(p.y + handleSize / 2 ), handleSize, handleSize);

            if(p==handlePressed) {
                graphics.setColor(new Color(0, 255, 0));
            } else {
                graphics.setColor(new Color(255, 0, 0));
            }
            graphics.drawOval(xToPict(p.x - handleSize / 2 ), yToPict(p.y + handleSize / 2 ), handleSize, handleSize);


            if( p.x > 0 && p.x < maxXSetup - minXSetup) {
                graphics.drawString(String.valueOf(p.x), xToPict(p.x - 2 ), yToPict(p.y + 2 + handleSize / 2));
            }

        }
    }

    private void noImgCleanUp() {
        disableFeatures();
    }

    private void enableFeatures() {
        addMouseListener(this);
        addMouseMotionListener(this);
        setWorkspace();
        drawFeatures = true;
    }

    private void disableFeatures() {
        removeMouseListener(this);
        removeMouseMotionListener(this);
        drawFeatures = false;
    }

    public void setWorkspace() {
        if(img == null) {
            noImgCleanUp();
        } else {
            minXSetup = 0;
            minYSetup = 0;
            maxXSetup = minXSetup + getImage().getWidth() - 1;
            maxYSetup = minYSetup + getImage().getHeight() - 1;
        }
    }

    private void setInitialHandles() {

        // temporary
        handles.add(new Point(34,76));
    }

    private void processHandles() {

    }

    public void removeDuplicateXHandles(Point active) {
        for(Point passive: handles) {
            if(passive.x == active.x) {
                passive.y = active.y;
            }
        }
    }

    public Polygon getOutputPolygon() {
        ArrayList<Point> points = new ArrayList<>(handles);
        Polygon outputPolygon = new Polygon();

        return outputPolygon;
    }

    private int xToPict(int v) {
        return v;
    }

    private int xFromPict(int v) {
        return v;
    }

    private double xFromPict(double v) {
        return v;
    }

    private int yToPict(int v) {
        return maxYSetup - minYSetup;
    }

    private int yFromPict(int v) {
        return - v +  maxYSetup - minYSetup;
    }

    private double yFromPict(double v) {
        return - v +  maxYSetup - minYSetup;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int eX = e.getX();
        int eY = e.getY();

        handlePressed = null;
        for(Point h: handles) {
            int distX = eX - xToPict(h.x);
            int distY = eY - yToPict(h.y);
            double dist = Math.pow(distX, 2) + Math.pow(distY, 2);
            double range = Math.pow( handleSize, 2);

            if(dist < range) {
                handlePressed = h;
                break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(handlePressed!=null) {
            removeDuplicateXHandles(handlePressed);
            handlePressed = null;
//            processHandles();
            getParent().repaint();
            // opcja performance
            fireActionEvent();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int eX = e.getX();
        int eY = e.getY();

        if(handlePressed != null) {

            if( xFromPict(eX) > maxXSetup - minXSetup ) {
                eX = xToPict(maxXSetup - minXSetup );
            }
            if( xFromPict(eX) < minXSetup ) {
                eX = xToPict(minXSetup);
            }

            if( yFromPict(eY) > maxYSetup - minYSetup ) {
                eY = yToPict(maxYSetup - minYSetup );
            } else {
                if (yFromPict(eY) < minYSetup) {
                    eY = yToPict(minYSetup);
                }
            }


            handlePressed.setLocation(xFromPict(eX), yFromPict(eY));

            processHandles();
            getParent().repaint();
            // opcja quality
//            fireActionEvent();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public void addActionListener(ActionListener actionListener) {
        actionListeners.add(actionListener);
    }

    public void removeActionListener(ActionListener actionListener) {
        actionListeners.remove(actionListener);
    }

    public void fireActionEvent() {
        for(ActionListener actionListener: actionListeners) {
            actionListener.actionPerformed(new ActionEvent((Object) this, actionEventId++, "User interacted" ));
        }
    }
}
