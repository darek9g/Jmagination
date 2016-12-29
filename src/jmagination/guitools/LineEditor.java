package jmagination.guitools;

import jmagination.ConstantsInitializers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by darek on 29.12.2016.
 */
public class LineEditor extends JPanel implements MouseListener {


    public static final int MIN_ORG_MODE = 0;
    public static final int MIN_MAX_MODE = 1;
    public static final int FREE_MODE = 2;

    int minXSetup = 10;
    int maxXSetup = 10;
    int minYSetup = 10;
    int maxYSetup = 10;

    int operationMode = 0;

    Polygon polygon;


    public LineEditor(int operationMode, int minXSetup, int maxXSetup, int minYSetup, int maxYSetup) {
        super();

        setOpaque(false);
        this.operationMode = operationMode;
        this.minXSetup = minXSetup;
        this.maxXSetup = maxXSetup;
        this.minYSetup = minYSetup;
        this.maxYSetup = maxYSetup;

        Dimension dimension = new Dimension(maxXSetup - minXSetup, maxYSetup - minYSetup);

        this.setMinimumSize(dimension);
        this.setMaximumSize(dimension);
        this.setPreferredSize(dimension);

        setBackground(ConstantsInitializers.GUI_CHARTS_BG_COLOR);

        polygon = new Polygon();
        polygon.addPoint(0,maxYSetup - minYSetup);
        polygon.addPoint(maxXSetup - minXSetup, 0);

        addMouseListener(this);
    }


    @Override
    public void paintComponent(Graphics graphics) {
        int width = maxXSetup - minXSetup;
        int height = maxYSetup - minYSetup;
//        graphics.clearRect(0,0, width, height);
        graphics.setColor(ConstantsInitializers.GUI_CHARTS_BG_COLOR);
        graphics.fillRect(0,0, width, height);
        graphics.setColor(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR);
        graphics.drawPolygon(polygon);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        polygon.addPoint(e.getX(), e.getY());
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
