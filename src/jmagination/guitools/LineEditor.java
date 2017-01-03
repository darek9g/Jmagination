package jmagination.guitools;

import jmagination.ConstantsInitializers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by darek on 29.12.2016.
 */
public class LineEditor extends JPanel implements MouseListener, MouseMotionListener {


    public static final int MIN_ORG_MODE = 0;
    public static final int MIN_MAX_MODE = 1;
    public static final int FREE_MODE = 2;

    private int marigin = 10;

    int minXSetup = 10;
    int maxXSetup = 10;
    int minYSetup = 10;
    int maxYSetup = 10;

    int operationMode = 0;

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
        this.minXSetup = minXSetup;
        this.maxXSetup = maxXSetup;
        this.minYSetup = minYSetup;
        this.maxYSetup = maxYSetup;

        Dimension dimension = new Dimension( 2 * marigin + maxXSetup - minXSetup + 1, 2 * marigin + maxYSetup - minYSetup + 1);

        this.setMinimumSize(dimension);
        this.setMaximumSize(dimension);
        this.setPreferredSize(dimension);

        setBackground(ConstantsInitializers.GUI_CHARTS_BG_COLOR);

        // wartości absolutne

        referenceLine.addPoint(xToPict(0), yToPict(0));
        referenceLine.addPoint(xToPict(maxXSetup - minXSetup ), yToPict(maxYSetup - minYSetup));

        processHandles();

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private int xToPict(int v) {
        return v + marigin;
    }

    private int xFromPict(int v) {
        return v - marigin;
    }

    private int yToPict(int v) {
        return maxYSetup - minYSetup + marigin - v;
    }

    private int yFromPict(int v) {
        return - v +  maxYSetup - minYSetup + marigin;
    }

    private void processHandles() {
        ArrayList<Point> newHandles = new ArrayList<>();
        boolean zeroHandle = false;

        for(int j=handles.size()-1;j>=0; --j) {

            Point h = handles.get(j);

            if (h.x == 0) {
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
            handles.add(new Point(0,0));
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

        if(points.size() % 2 == 1) {
            points.remove(0);
            outputPolygon.addPoint(xToPict(0), yToPict(0));
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
                case FREE_MODE:
                default:
                    y = points.get(i).y;
            }

            switch(operationMode) {
                case FREE_MODE:
                    outputPolygon.addPoint(xToPict(points.get(i).x), yToPict(y));
                    break;
                default:
                    if(i % 2 == 0) {
                        outputPolygon.addPoint(xToPict(points.get(i).x), yToPict(0));
                        outputPolygon.addPoint(xToPict(points.get(i).x), yToPict(y));
                    } else {
                        outputPolygon.addPoint(xToPict(points.get(i).x), yToPict(y));
                        outputPolygon.addPoint(xToPict(points.get(i).x), yToPict(0));
                    }
            }

        }

        outputPolygon.addPoint(xToPict(maxXSetup - minXSetup), yToPict(0));

        return outputPolygon;
    }

    public int[] getOutputPoints() {
        ArrayList<Point> points = new ArrayList<>(handles);
        int[] outputPoints = new int[maxXSetup - minXSetup + 1];

        if(points.size() % 2 == 1) {
            points.remove(0);
        }

        for(int i=0; i<outputPoints.length; ++i) {
            outputPoints[i] = 0;
        }


/*        for(int i=0; i<points.size(); ++i) {


            int y;
            switch(operationMode) {
                case MIN_ORG_MODE:
                    y = points.get(i).x;
                    break;
                case MIN_MAX_MODE:
                    y = maxYSetup - minYSetup;
                    break;
                case FREE_MODE:
                default:
                    y = points.get(i).y;
            }

            outputPoints.add(new Point(points.get(i).x, y));
            if(points.get(i).x == maxXSetup - minXSetup) { maxHandle = true; }

        }*/



        return outputPoints;
    }

    @Override
    public void paintComponent(Graphics graphics) {
        int width = maxXSetup - minXSetup;
        int height = maxYSetup - minYSetup;


        // rysowanie tła

        graphics.setColor(ConstantsInitializers.GUI_CHARTS_BG_COLOR);
        Polygon bgPolygon = new Polygon(new int[]{xToPict(0), xToPict(width), xToPict(width), xToPict(0)},
                new int[]{yToPict(0), yToPict(0), yToPict(height), yToPict(height)}, 4);
        graphics.fillPolygon(bgPolygon);

        // rysowanie lini odniesienia
        graphics.setColor(Color.orange);
        graphics.drawPolygon(referenceLine);


        // rysowanie uchwytów i etykiet narzędzi edycji w tle
        for(Point p: handles) {
            if(p!=handlePressed) {
                graphics.setColor(new Color(255, 175, 175));
                if(p.x != 0) {
                    graphics.drawLine(xToPict(p.x), yToPict(0), xToPict(p.x), yToPict(height));
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

        // rysowanie uchwytów i etykiet narzędzi edycji na pierwszym planie


        for(Point p: handles) {
            if(p==handlePressed) {
                graphics.setColor(new Color(175, 255, 175));
                graphics.drawLine(xToPict(p.x), yToPict(0), xToPict(p.x), yToPict(height));
            } else {
                graphics.setColor(new Color(255, 175, 175));
            }

            graphics.fillOval(xToPict(p.x - marigin / 2 ), yToPict(p.y + marigin / 2 ), marigin, marigin);

            if(p==handlePressed) {
                graphics.setColor(new Color(0, 255, 0));
            } else {
                graphics.setColor(new Color(255, 0, 0));
            }
            graphics.drawOval(xToPict(p.x - marigin / 2 ), yToPict(p.y + marigin / 2 ), marigin, marigin);

            if( p.x > 0 && p.x < maxXSetup - minXSetup) {
                graphics.drawString(String.valueOf(p.x), xToPict(p.x + 2 ), yToPict(p.y + 2 + marigin / 2));
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
            double range = Math.pow( marigin, 2);

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
            if( xFromPict(eX) < 0 ) {
                eX = xToPict(0);
            }

            if( yFromPict(eY) > maxYSetup - minYSetup ) {
                eY = yToPict(maxYSetup - minYSetup );
            }
            if( yFromPict(eY) < 0 ) {
                eY = yToPict(0);
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
