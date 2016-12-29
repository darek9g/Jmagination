package jmagination.guitools;

import jmagination.ConstantsInitializers;

import javax.swing.*;
import java.awt.*;

/**
 * Created by darek on 29.12.2016.
 */
public class LineEditor extends JPanel {


    public final int MIN_ORG_MODE = 0;
    public final int MIN_MAX_MODE = 1;
    public final int FREE_MODE = 2;

    int minXSetup = 10;
    int maxXSetup = 10;
    int minYSetup = 10;
    int maxYSetup = 10;

    int operationMode = 0;

    Polygon polygon;


    public LineEditor(int operationMode, int minXSetup, int maxXSetup, int minYSetup, int maxYSetup) {
        super();

        polygon = new Polygon();
        this.operationMode = operationMode;
        this.minXSetup = minXSetup;
        this.maxXSetup = maxXSetup;
        this.minYSetup = minYSetup;
        this.maxYSetup = maxYSetup;

        Dimension dimension = new Dimension(maxXSetup - minXSetup, maxYSetup - minYSetup);

        this.setMinimumSize(dimension);
        this.setMaximumSize(dimension);
        this.setPreferredSize(dimension);

    }


    @Override
    public void paintComponent(Graphics graphics) {
        graphics.setColor(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR);
        graphics.drawPolygon(polygon);
    }


}
