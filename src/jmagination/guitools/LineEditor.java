package jmagination.guitools;

import jmagination.ConstantsInitializers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

/**
 * Created by darek on 29.12.2016.
 */
public class LineEditor extends JPanel implements MouseListener, MouseMotionListener {


    public static final int MIN_ORG_MODE = 0;
    public static final int MIN_MAX_MODE = 1;
    public static final int FREE_MODE = 2;
    public static final int STRECH_MODE = 3;
    public static final int SCALE_MODE = 4;
    public static final int SCALEDOWN_MODE = 5;
    public static final int NEGATE_MODE = 6;
    private static final int LINE_MODE = 99;


    private int marigin = 20;
    private int handleSize = 10;

    int minXSetup = 10;
    int maxXSetup = 10;
    int minYSetup = 10;
    int maxYSetup = 10;
    int limitYSetup = 10;

    int operationMode = 0;
    boolean editableMode = true;

    Polygon referenceLine;
    Polygon finalLine;

    ArrayList<Point> handles;
    Point handlePressed = null;

    ArrayList<ActionListener> actionListeners;
    private int actionEventId = 0;

    {
        referenceLine = new Polygon();
        finalLine = new Polygon();
        handles = new ArrayList<>();
        actionListeners = new ArrayList<>();

    }

    public LineEditor(int operationMode, int minXSetup, int maxXSetup, int minYSetup, int maxYSetup) {
        super();

        this.operationMode = operationMode;
        this.editableMode = editableMode;

        this.minXSetup = minXSetup;
        this.maxXSetup = maxXSetup;
        this.minYSetup = minYSetup;
        this.maxYSetup = maxYSetup;
        this.limitYSetup = maxYSetup;

        Dimension dimension = new Dimension( 2 * marigin + maxXSetup - minXSetup + 1, 2 * marigin + maxYSetup - minYSetup + 1);

        this.setMinimumSize(dimension);
        this.setMaximumSize(dimension);
        this.setPreferredSize(dimension);

        setBackground(ConstantsInitializers.GUI_CHARTS_BG_COLOR);

        // wartości absolutne

        referenceLine.addPoint(xToPict(minXSetup), yToPict(minYSetup));
        referenceLine.addPoint(xToPict(maxXSetup - minXSetup ), yToPict(maxYSetup - minYSetup));

        setInitialHandles();
        processHandles();

        if(editableMode == true) {
            addMouseListener(this);
            addMouseMotionListener(this);
        }
    }

    public LineEditor(int operationMode, int minXSetup, int maxXSetup, int minYSetup, int maxYSetup, int limitYSetup) {
        this(operationMode, minXSetup, maxXSetup, minYSetup, maxYSetup);
        this.limitYSetup = limitYSetup;
    }


    public void setMode(int mode) {
        operationMode = mode;
        handles.clear();
        processHandles();
        getParent().repaint();
    }

    public void setLimitYSetup(int limitY) {
        limitYSetup = limitY;
        getParent().repaint();
    }

    public int getMode() {
        return operationMode;
    }

    private int xToPict(int v) {
        return v + marigin;
    }

    private int xFromPict(int v) {
        return v - marigin;
    }

    private double xFromPict(double v) {
        return v - marigin;
    }

    private int yToPict(int v) {
        return maxYSetup - minYSetup + marigin - v;
    }

    private int yFromPict(int v) {
        return - v +  maxYSetup - minYSetup + marigin;
    }

    private double yFromPict(double v) {
        return - v +  maxYSetup - minYSetup + marigin;
    }

    private void setInitialHandles() {
        switch(operationMode) {
            case NEGATE_MODE:
                handles.add(new Point(minXSetup, maxYSetup - minYSetup));
                handles.add(new Point(maxXSetup - minXSetup, minYSetup));
                break;
        }

        if(operationMode == NEGATE_MODE) {
            operationMode = LINE_MODE;
            editableMode = false;
        }

    }

    private void processHandles() {

        switch (operationMode) {
            case SCALE_MODE:
            case SCALEDOWN_MODE:
            case LINE_MODE:
                if(handles.size() != 2) {
                    handles.add(new Point(maxXSetup - minXSetup, maxYSetup - minYSetup));
                }
        }

        ArrayList<Point> newHandles = new ArrayList<>();
        boolean zeroHandle = false;

        for(int j=handles.size()-1;j>=0; --j) {

            Point h = handles.get(j);

            if (h.x == minXSetup) {
                if (zeroHandle == true) {
                    continue;
                }
                zeroHandle = true;
            }

            int insertPos = 0;
            for (int i = 0; i < newHandles.size(); ++i) {
                if (h.x >= newHandles.get(i).x) {
                    insertPos = i + 1;
                } else {
                    insertPos = i;
                    break;
                }
            }

            newHandles.add(insertPos, h);

        }

        handles.clear();

        if(zeroHandle == false) {
            handles.add(new Point(minXSetup,minYSetup));
        }
        handles.addAll(newHandles);
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


        switch(operationMode) {
            case MIN_ORG_MODE:
            case MIN_MAX_MODE:
            case STRECH_MODE:
                if(points.size() % 2 == 1) {
                    points.remove(0);
                    outputPolygon.addPoint(xToPict(minXSetup), yToPict(minYSetup));
                }
                break;
            case FREE_MODE:
            case SCALE_MODE:
            case SCALEDOWN_MODE:
            case LINE_MODE:
                outputPolygon.addPoint(xToPict(minXSetup), yToPict(minYSetup));
            default:
        }


        for(int i=0; i<points.size(); ++i) {

            int y;
            switch(operationMode) {
                case MIN_ORG_MODE:
                    y = points.get(i).x;
                    break;
                case MIN_MAX_MODE:
                    y = maxYSetup  - minYSetup;
                    break;
                case STRECH_MODE:
                    y = limitYSetup  - minYSetup;
                    break;
                case FREE_MODE:
                case SCALE_MODE:
                case SCALEDOWN_MODE:
                case LINE_MODE:
                default:
                    y = points.get(i).y;
            }

            switch(operationMode) {
                case FREE_MODE:
                case SCALE_MODE:
                case SCALEDOWN_MODE:
                case LINE_MODE:
                    outputPolygon.addPoint(xToPict(points.get(i).x), yToPict(y));
                    break;
                case STRECH_MODE:
                    if(i % 2 == 0) {
                        outputPolygon.addPoint(xToPict(points.get(i).x), yToPict(minYSetup));
                    } else {
                        outputPolygon.addPoint(xToPict(points.get(i).x), yToPict(y));
                        outputPolygon.addPoint(xToPict(points.get(i).x), yToPict(minYSetup));
                    }
                    break;
                default:
                    if(i % 2 == 0) {
                        outputPolygon.addPoint(xToPict(points.get(i).x), yToPict(minYSetup));
                        outputPolygon.addPoint(xToPict(points.get(i).x), yToPict(y));
                    } else {
                        outputPolygon.addPoint(xToPict(points.get(i).x), yToPict(y));
                        outputPolygon.addPoint(xToPict(points.get(i).x), yToPict(minYSetup));
                    }
            }

        }

        outputPolygon.addPoint(xToPict(maxXSetup - minXSetup), yToPict(minYSetup));

        return outputPolygon;
    }

    public int[] getOutputMap() {
        int[] outputPoints = new int[maxXSetup - minXSetup + 1];
        Polygon polygon = getOutputPolygon();


        for(int i=0; i<outputPoints.length; ++i) {
            outputPoints[i] = minYSetup;
        }

        double Ax = 0;
        double Ay = 0;

        boolean firstSegment = true;

        PathIterator pathIterator = polygon.getPathIterator(null);
        do {
            double[] segment = new double[6];
            if(pathIterator.currentSegment(segment) != PathIterator.SEG_CLOSE) {

                double Bx = xFromPict(segment[0]);
                double By = yFromPict(segment[1]);

                if(firstSegment==false) {
                    if (Ax == Bx) {
                        outputPoints[(int) Bx] = (int) By;
                    } else {
                        double lineCoeff = (By - Ay) / (Bx - Ax);
                        int fromX = (int) Math.round(Ax);
                        int toX = (int) Math.round(Bx);

                        int fromY = (int) Math.round(Ay);

                        for (int i = fromX; i <= toX; ++i) {
                            outputPoints[i] = (int) Math.round(Ay + (i - fromX) * lineCoeff);
                        }
                    }
                } else {
                    firstSegment = false;
                }

                Ax = Bx;
                Ay = By;
            }


            pathIterator.next();
        } while(pathIterator.isDone()==false);

        return outputPoints;
    }

    @Override
    public void paintComponent(Graphics graphics) {
        int width = maxXSetup - minXSetup;
        int height = maxYSetup - minYSetup;


        // rysowanie tła

        graphics.setColor(ConstantsInitializers.GUI_CHARTS_BG_COLOR);
        Polygon bgPolygon = new Polygon(new int[]{xToPict(minXSetup), xToPict(width), xToPict(width), xToPict(minXSetup)},
                new int[]{yToPict(minYSetup), yToPict(minYSetup), yToPict(height), yToPict(height)}, 4);
        graphics.fillPolygon(bgPolygon);

        // rysowanie uchwytów i etykiet narzędzi edycji w tle
        for(Point p: handles) {
            if(p!=handlePressed) {
                graphics.setColor(new Color(255, 175, 175));
                if(p.x != 0) {
                    graphics.drawLine(xToPict(p.x), yToPict(minYSetup), xToPict(p.x), yToPict(height));
                }
            }

        }

        // rysowanie polygony opisującej schemat operacji

        finalLine.reset();
        finalLine = getOutputPolygon();

        graphics.setColor(new Color(182, 182, 182));
        graphics.fillPolygon(finalLine);

        graphics.setColor(Color.BLACK);
        graphics.drawPolygon(finalLine);

        // rysowanie lini odniesienia
        graphics.setColor(Color.orange);
        graphics.drawPolygon(referenceLine);


        // rysowanie uchwytów i etykiet narzędzi edycji na pierwszym planie


        for(Point p: handles) {
            if(p==handlePressed) {
                graphics.setColor(new Color(175, 255, 175));
                graphics.drawLine(xToPict(p.x), yToPict(minYSetup), xToPict(p.x), yToPict(height));
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


            switch(operationMode) {
                case SCALE_MODE:
                case SCALEDOWN_MODE:
                case LINE_MODE:
                    graphics.drawString(String.valueOf(p.y), xToPict(p.x - 2 ), yToPict(p.y + 2 + handleSize / 2));
                    break;
                default:
                    if( p.x > 0 && p.x < maxXSetup - minXSetup) {
                        graphics.drawString(String.valueOf(p.x), xToPict(p.x - 2 ), yToPict(p.y + 2 + handleSize / 2));
                    }
            }

        }

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

                switch(operationMode) {
                    case SCALEDOWN_MODE:
                        if(h.x == minXSetup) { return; }
                }

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

            switch (operationMode) {

                case SCALE_MODE:
                case SCALEDOWN_MODE:
                case LINE_MODE:
                    if(handlePressed.x <= minXSetup) {
                        eX = xToPict(minXSetup);
                    } else {
                        eX = xToPict(maxXSetup - minXSetup);
                    }

                    break;
                default:
                    if( xFromPict(eX) > maxXSetup - minXSetup ) {
                        eX = xToPict(maxXSetup - minXSetup );
                    }
                    if( xFromPict(eX) < minXSetup ) {
                        eX = xToPict(minXSetup);
                    }

            }

            if( yFromPict(eY) > maxYSetup - minYSetup ) {
                eY = yToPict(maxYSetup - minYSetup );
            }
            if( yFromPict(eY) < minYSetup ) {
                eY = yToPict(minYSetup);
            }


            handlePressed.setLocation(xFromPict(eX), yFromPict(eY));

            processHandles();
            getParent().repaint();
            fireActionEvent();
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
