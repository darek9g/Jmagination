package jmagination.gui;

import jmagination.ConstantsInitializers;
import jmagination.histogram.LineProfileData;
import jmagination.histogram.LineProfileDatum;
import jmagination.util.SimpleHSVBufferedImage;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by darek on 25.01.2017.
 */
public class ImagePanel4 extends ImagePanel3 implements MouseListener, MouseMotionListener {

    Polygon finalLine;
    ArrayList<Point> handles;
    ArrayList<Point> readhandles;
    Point handlePressed = null;
    Point handleToDelete = null;
    int handleToCreateIndex = -1;
    ArrayList<ActionListener> actionListeners;
    private int actionEventId = 0;

    private int handleSize = 10;

    private int minXSetup;
    private int maxXSetup;
    private int minYSetup;
    private int maxYSetup;

    private final String pointSymbol = "P";

    private boolean drawFeatures;
    private boolean trueImage;

    {
        finalLine = new Polygon();
        handles = new ArrayList<>();
        actionListeners = new ArrayList<>();

        drawFeatures = false;
        trueImage = false;

    }


    public ImagePanel4(SimpleHSVBufferedImage img) {
        super(img);
    }

    @Override
    public void setImage(SimpleHSVBufferedImage bufferedImage) {
        super.setImage(bufferedImage);

        trueImage = true;
        setWorkspace();
        setInitialHandles();

    }

    public boolean isDrawingFeatures() {
        return drawFeatures;
    }

    public void setFakeImage(SimpleHSVBufferedImage bufferedImage) {
        super.setImage(bufferedImage);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if(drawFeatures == false) { return; }

        graphics.setPaintMode();
        graphics.setColor(ConstantsInitializers.GUI_CHARTS_BG_COLOR);



        graphics.setColor(new Color(255, 206, 68));
        for(int i=1; i<handles.size(); i++) {
            Point p1 = handles.get(i-1);
            Point p2 = handles.get(i);
            graphics.drawLine(xToPict(p1.x), yToPict(p1.y), xToPict(p2.x), yToPict(p2.y));
        }

        // rysowanie uchwytów i etykiet narzędzi edycji w tle
//        for(Point p: handles) {
        for(int i=0; i<handles.size(); i++) {
            Point p = handles.get(i);
            if(p==handlePressed) {
                graphics.setColor(new Color(175, 255, 175));
                graphics.drawLine(xToPict(p.x), yToPict(minYSetup), xToPict(p.x), yToPict(maxYSetup));
                graphics.drawLine(xToPict(minXSetup), yToPict(p.y), xToPict(maxXSetup), yToPict(p.y));
            } else {
                graphics.setColor(new Color(255, 175, 175));
            }

//            graphics.fillOval(xToPict(p.x - handleSize / 2 ), yToPict(p.y + handleSize / 2 ), handleSize, handleSize);

            if(p==handlePressed) {
                graphics.setColor(new Color(0, 0, 200));
            } else {
                graphics.setColor(new Color(255, 0, 0));
            }
            graphics.drawOval(xToPict(p.x - handleSize / 2 ), yToPict(p.y + handleSize / 2 ), handleSize, handleSize);

            // etkiety

            String label = createPointLabel(p,i);
            Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(label, graphics);
            int fontDescent = graphics.getFontMetrics().getDescent();
            int xLabelOffset = p.x > (maxXSetup - minXSetup) / 2 ? -handleSize/2 - (int)stringBounds.getWidth() : handleSize/2;
            int yLabelOffset = p.y > (maxYSetup - minYSetup) / 2 ? - 2 * ((int)stringBounds.getHeight() - handleSize): 0;

            Color lastColor = graphics.getColor();
            graphics.setColor(new Color(255, 206, 68));
            graphics.fillRect(xToPict(p.x + xLabelOffset ), yToPict(p.y + yLabelOffset + (int)stringBounds.getHeight() - fontDescent),
                    (int)stringBounds.getWidth(), (int)stringBounds.getHeight());
            graphics.setColor(lastColor);
            graphics.drawString( label, xToPict(p.x + xLabelOffset ), yToPict(p.y + yLabelOffset ));

        }
    }

    private String createPointLabel(Point p, int index) {
        String label = pointSymbol + String.valueOf(index) + " (" + String.valueOf(p.x) + "," + String.valueOf(p.y) + ")";

        return label;
    }

    private void noImgCleanUp() {
        trueImage = false;
        disableFeatures();
    }

    public void enableFeatures() {
        if(drawFeatures == true) { return; }
        if(trueImage == false) { return; }
        drawFeatures = true;
        addMouseListener(this);
        addMouseMotionListener(this);
        setWorkspace();
        repaint();
    }

    public void disableFeatures() {
        if(drawFeatures == false) { return; }
        removeMouseListener(this);
        removeMouseMotionListener(this);
        repaint();
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

        boolean recreate = false;
        for(Point p: handles) {
            if(p.x<0 || p.x> maxXSetup - minXSetup || p.y<0 || p.y> maxYSetup - minYSetup) {
                recreate = true;
            }
        }
        if(recreate==true) {
            handles.clear();
        }

        if(handles.size()<1) {
            handles.add(new Point( (maxXSetup - minXSetup) / 4,3 * (maxYSetup - minYSetup) / 4));
        }

        if(handles.size()<2) {
            handles.add(new Point( 3 * (maxXSetup - minXSetup) / 4,(maxYSetup - minYSetup) / 4));
        }

    }

    public LineProfileData getLineProfileData() {


        ArrayList<Point> points = new ArrayList<>(readhandles);

        int imageChannels = img.getRaster().getNumDataElements();
        boolean colorMode = false;
        if(imageChannels > 1) { colorMode = true; }

        int channels;

        String[] channelNames;
        String[] channelNamesColor = { "Red", "Green", "Blue", "Hue", "Saturation", "Value" };
        String[] channelNamesGrey = { "Grey" };

        LineProfileData lineProfileData;
        if ( colorMode == true) {
            channels = 6;
            channelNames = channelNamesColor;
        } else {
            channels = 1;
            channelNames = channelNamesGrey;
        }

        lineProfileData = new LineProfileData(channels, channelNames);
        if(points.size()<2) {
            return lineProfileData;
        }

/*
        for(int i=1; i<points.size(); i++) {
            Point p0 = points.get(i-1);
            Point p1 = points.get(i);
            System.out.println("i " + i + " Punkty p0: " + p0.x + "," + p0.y + ") p1: (" + p1.x + "," + p1.y + ")");
        }
        */

        int length = -1;

        for(int i=1; i<points.size(); i++) {
            Point p0 = points.get(i-1);
            Point p1 = points.get(i);


            if(p0.x == p1.x && p0.y == p1.y) {
                continue;
            } else {
                // vertical
                if(p0.x == p1.x) {
                    for(int d=0;d< p1.y - p0.y - 1; d++) {
                        length+=1;
                        addLineProfileDatum(lineProfileData, colorMode, length,
                                p0.x, p0.y + d, d==0 ? createPointLabel(p0, i-1) : "");
                    }
                    continue;
                }
                //horizontal
                if(p0.y == p1.y) {
                    for(int d=0;d< p1.x - p0.x - 1; d++) {
                        length+=1;
                        addLineProfileDatum(lineProfileData, colorMode, length,
                                p0.x + d, p0.y, d==0 ? createPointLabel(p0, i-1) : "");
                    }
                    continue;
                }

                // at angle
                double coeff = Math.atan2(0.0 + p1.y - p0.y, p1.x - p0.x);
                double section_length = Math.sqrt( Math.pow(0.0 + p1.x - p0.x, 2) + Math.pow(0.0 + p1.y - p0.y, 2) );
                double distance = 0.0;

                while(distance < section_length) {
                    length++;

                    int imgX = (int)Math.round(p0.x + Math.cos(coeff) * distance);
                    int imgY = (int)Math.round(p0.y + Math.sin(coeff) * distance);

                    addLineProfileDatum(lineProfileData, colorMode, length,
                            imgX, imgY, distance<1.0 ? createPointLabel(p0, i-1) : "");

                    distance = distance + 1.0;

                }
            }
            // last point with label
            if(i>0 && i>points.size()-2) {
                length++;
                addLineProfileDatum(lineProfileData, colorMode, length,
                        p1.x, p1.y, createPointLabel(p1, i));

            }
        }

//        lineProfileData.printData();
        return lineProfileData;
    }

    private void addLineProfileDatum(LineProfileData lineProfileData, boolean colorMode, int length, int imgX, int imgY, String label) {
        float[] pixelF;

        if (colorMode == false) {
            int[] pixel = new int[1];
            pixelF = new float[1];
            img.getRaster().getPixel(imgX, imgY, pixel);
            for (int b = 0; b < pixel.length; b++) {
                pixelF[b] = pixel[b];
            }

        } else {
            int[] pixel = new int[3];
            pixelF = new float[6];

            try {
                img.getRaster().getPixel(imgX, imgY, pixel);
            } catch (Exception e) {
                System.out.println("Pixele spoza obrazu x: " + imgX + " y:" + imgY );
                throw new ArrayIndexOutOfBoundsException();
            }
            for (int b = 0; b < pixel.length; b++) {
                pixelF[b] = pixel[b];
            }
            pixelF[3] = img.getHsv()[imgX][imgY][0];
            pixelF[4] = img.getHsv()[imgX][imgY][1];
            pixelF[5] = img.getHsv()[imgX][imgY][2];

        }

        lineProfileData.addDatum(length, pixelF, label);
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
        return - v +  maxYSetup - minYSetup;
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

        boolean leftButton = e.getButton() == MouseEvent.BUTTON1 ? true : false;
        boolean rightButton = e.getButton() == MouseEvent.BUTTON3 ? true : false;

        int eX = e.getX();
        int eY = e.getY();

        handleToDelete = null;
        handleToCreateIndex = -1;

        handlePressed = null;
        for(Point h: handles) {
            int distX = eX - xToPict(h.x);
            int distY = eY - yToPict(h.y);
            double dist = Math.pow(distX, 2) + Math.pow(distY, 2);
            double range = Math.pow( handleSize, 2);

            if(dist < range) {

                if(leftButton) {
                    handlePressed = h;
                    break;
                }
                if(rightButton) {
                    if(h==handles.get(0)) {
                        handleToCreateIndex = 1;
                        handlePressed = h;
                        break;
                    }
                    if(h==handles.get(handles.size()-1)) {
                        handleToCreateIndex = handles.size() - 1;
                        handlePressed = h;
                        break;
                    }

                    handleToDelete = h;
                    break;
                }
            }
        }


    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(handleToDelete != null) {
            handles.remove(handleToDelete);
            handleToDelete = null;

            handlePressed = null;

            getParent().repaint();
            fireActionEvent();

            return;
        }

        if(handlePressed!=null) {

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

            if(handleToCreateIndex >= 0) {
                handles.add(handleToCreateIndex, new Point(handlePressed));
            }
            handleToCreateIndex = -1;

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
        readhandles = new ArrayList<>();
        for(Point p: handles) {
            readhandles.add(new Point(xToPict(p.x), yToPict(p.y)));
        }

        for(ActionListener actionListener: actionListeners) {
            actionListener.actionPerformed(new ActionEvent((Object) this, actionEventId++, "User interacted" ));
        }
    }

}
