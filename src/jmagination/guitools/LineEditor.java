package jmagination.guitools;

import jmagination.ConstantsInitializers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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

    {
        referenceLine = new Polygon();

        finalLine = new Polygon();

        handles = new ArrayList<>();

    }


    public LineEditor(int operationMode, int minXSetup, int maxXSetup, int minYSetup, int maxYSetup) {
        super();

        this.operationMode = operationMode;
        this.minXSetup = minXSetup;
        this.maxXSetup = maxXSetup;
        this.minYSetup = minYSetup;
        this.maxYSetup = maxYSetup;

        Dimension dimension = new Dimension( 2 * marigin + maxXSetup - minXSetup , 2 * marigin + maxYSetup - minYSetup );

        this.setMinimumSize(dimension);
        this.setMaximumSize(dimension);
        this.setPreferredSize(dimension);

        setBackground(ConstantsInitializers.GUI_CHARTS_BG_COLOR);

        // wartości absolutne

        referenceLine.addPoint(xToPict(0), yToPict(0));
        referenceLine.addPoint(xToPict(maxXSetup - minXSetup), yToPict(maxYSetup - minYSetup));

/*        finalLine.addPoint(xToPict(0), yToPict(0));
        finalLine.addPoint(xToPict(maxXSetup - minXSetup), yToPict(maxYSetup - minYSetup));*/

//        handles.add(new Point(0, 0 ));
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
        return v - ( maxYSetup - minYSetup + marigin);
    }

    private void processHandles() {
        ArrayList<Point> newHandles = new ArrayList<>();
        boolean zeroHandle = false;
        for(Point h: handles) {

            if (h.x == 0) {
                if (zeroHandle == true) {
                    continue;
                }
                zeroHandle = true;
            }

            int insertPos = 0;
            for (int i = 0; i < newHandles.size(); ++i) {
                if (h.x > newHandles.get(i).x) {
                    insertPos = i + 1;
                } else {
                    insertPos = i;
                    break;
                }
            }

            newHandles.add(insertPos, h);

        }

        for (int i=0; i<handles.size(); ++i) {
            System.out.println("Org " + handles.get(i));
        }

        for (int i=0; i<newHandles.size(); ++i) {
            System.out.println("New " + newHandles.get(i));
        }

        handles.clear();

        if(zeroHandle == false) {
            handles.add(new Point(0,0));
        }
        handles.addAll(newHandles);

        for (int i=0; i<handles.size(); ++i) {
            System.out.println("Final " + handles.get(i));
        }
    }

    public ArrayList<Point> getHandles() {
        ArrayList<Point> ret = new ArrayList<>(handles);
        System.out.println("Initial");
        for(Point h: ret) {
//            System.out.println(h.toString());
        }
        if(ret.size() % 2 == 1) {
            ret.remove(0);
            System.out.println("Removing");
        }

        System.out.println("Final");
        for(Point h: ret) {
//            System.out.println(h.toString());
        }
        return ret;
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


        // rysowanie polygony opisującej schemat operacji

        ArrayList<Point> realHandles = getHandles();

        graphics.setColor(Color.BLACK);
        finalLine.reset();

        finalLine.addPoint(xToPict(0), yToPict(0));

        for(int i=0; i<realHandles.size(); ++i) {

            if(i % 2 == 0) {
                finalLine.addPoint(xToPict(realHandles.get(i).x), yToPict(0));
                finalLine.addPoint(xToPict(realHandles.get(i).x), yToPict(height/2));
            } else {
                finalLine.addPoint(xToPict(realHandles.get(i).x), yToPict(height/2));
                finalLine.addPoint(xToPict(realHandles.get(i).x), yToPict(0));
            }
        }

        finalLine.addPoint(xToPict(width), yToPict(0));
        graphics.drawPolygon(finalLine);
        setOpaque(true);
        graphics.setColor(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR);
        graphics.fillPolygon(finalLine);
        setOpaque(false);

        // rysowanie lini odniesienia
        graphics.setColor(Color.orange);
        graphics.drawPolygon(referenceLine);


        // rysowanie uchwytów i etykiet narzędzi edycji
        for(Point p: handles) {
            if(p==handlePressed) {
                graphics.setColor(new Color(175, 255, 175));
            } else {
                graphics.setColor(new Color(255, 175, 175));
            }
            if(p.x != 0) {
                graphics.drawLine(xToPict(p.x), yToPict(0), xToPict(p.x), yToPict(height));
            }

            graphics.fillOval(xToPict(p.x - marigin / 2 ), yToPict(height / 2 ), marigin, marigin);

            if(p==handlePressed) {
                graphics.setColor(new Color(0, 255, 0));
            } else {
                graphics.setColor(new Color(255, 0, 0));
            }
            graphics.drawOval(xToPict(p.x - marigin / 2 ), yToPict(height / 2 ), marigin, marigin);
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
            int distY = eY - yToPict((maxYSetup - minYSetup) / 2 );
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
            handlePressed = null;
            processHandles();
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
            int handlePressedY = handlePressed.y;

            if( xFromPict(eX) > maxXSetup - minXSetup ) {
                eX = xToPict(maxXSetup - minXSetup );
            }
            if( xFromPict(eX) < 0 ) {
                eX = xToPict(0);
            }

            handlePressed.setLocation(xFromPict(eX), handlePressedY);

            getParent().repaint();
        }

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
