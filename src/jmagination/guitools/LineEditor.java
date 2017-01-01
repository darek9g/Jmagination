package jmagination.guitools;

import jmagination.ConstantsInitializers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Created by darek on 29.12.2016.
 */
public class LineEditor extends JPanel implements MouseListener {


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

        finalLine.addPoint(xToPict(0), yToPict(0));
        finalLine.addPoint(xToPict(maxXSetup - minXSetup), yToPict(maxYSetup - minYSetup));

        handles.add(new Point(0, 0 ));

        addMouseListener(this);
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

            System.out.println("Process handles");


            if (h.x == 0) {
                if (zeroHandle == true) {
                    continue;
                }
                zeroHandle = true;
            }

            int insertPos = 0;
            boolean isBiggest = false;
            for (int i = 0; i < newHandles.size(); ++i) {
                if (h.x > newHandles.get(i).x) {
                    ++insertPos;
                } else {
                    break;
                }

                if (i == newHandles.size()) {
                    isBiggest = true;
                }
            }

            newHandles.add(insertPos, h);

        }

        for (int i=0; i<handles.size(); ++i) {
            handles.remove(i);
        }

        handles.addAll(newHandles);
    }

    public ArrayList<Point> getHandles() {
        ArrayList<Point> ret = new ArrayList<>(handles);
        if(ret.size() % 2 == 1) {
            ret.remove(0);
        }
        return ret;
    }

    @Override
    public void paintComponent(Graphics graphics) {
        int width = maxXSetup - minXSetup;
        int height = maxYSetup - minYSetup;
//        graphics.clearRect(0,0, width, height);

        graphics.setColor(ConstantsInitializers.GUI_CHARTS_BG_COLOR);
        Polygon bgPolygon = new Polygon(new int[]{xToPict(0), xToPict(width), xToPict(width), xToPict(0)},
                new int[]{yToPict(0), yToPict(0), yToPict(height), yToPict(height)}, 4);
        graphics.fillPolygon(bgPolygon);

//        graphics.setColor(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR);
        graphics.setColor(Color.orange);
        graphics.drawPolygon(referenceLine);


        for(Point p: handles) {
            graphics.setColor(new Color(255, 175, 175));
            graphics.drawLine(xToPict(p.x), yToPict(0), xToPict(p.x), yToPict(height));

            graphics.fillOval(xToPict(p.x - marigin / 2 ), yToPict(height / 2 ), marigin, marigin);
            graphics.setColor(new Color(255, 0, 0));
            graphics.drawOval(xToPict(p.x - marigin / 2 ), yToPict(height / 2 ), marigin, marigin);
        }


        ArrayList<Point> realHandles = getHandles();

        graphics.setColor(Color.BLACK);
        finalLine.reset();

        finalLine.addPoint(xToPict(0), yToPict(0));

        for(int i=0; i<realHandles.size(); ++i) {

            if(i % 2 == 0) {
                finalLine.addPoint(xToPict(realHandles.get(i).x), yToPict(0));
                finalLine.addPoint(xToPict(realHandles.get(i).x), yToPict(height));

            } else {
                finalLine.addPoint(xToPict(realHandles.get(i).x), yToPict(height));
                finalLine.addPoint(xToPict(realHandles.get(i).x), yToPict(0));
            }
        }

        finalLine.addPoint(xToPict(width), yToPict(0));
        graphics.drawPolygon(finalLine);


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
            double dist = distX ^ 2 + distY ^ 2;
            double range = ( marigin / 2 ) ^ 2;

            if(dist < range) {
                handlePressed = h;
                System.out.println("Contact");
                break;
            }
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {

        int eX = e.getX();
        int eY = e.getY();

        int handlePressedY = handlePressed.y;
        if(handlePressed != null) {
            handlePressed.setLocation(xFromPict(eX), handlePressedY);
            System.out.println("Update");
        }

        handlePressed = null;
        processHandles();
        getParent().repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
