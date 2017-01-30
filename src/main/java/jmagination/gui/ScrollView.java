package jmagination.gui;

import jmagination.ConstantsInitializers;

import javax.swing.*;
import java.awt.*;

/**
 * Created by darek on 29.12.2016.
 */
public class ScrollView extends JComponent {

    double scaler = 1.0;

    Rectangle graphicsRectangle;
    Point graphicsRectangleHandle;

    JFrame window = new JFrame();

    Point mouseInitialPosition;

    public ScrollView(Dimension viewDimension, Dimension imageDimension, Point mouseInitialPosition, Point mousePictureOriginOffset) {

        this.mouseInitialPosition = new Point(mouseInitialPosition);

        graphicsRectangle = new Rectangle();

        window.setUndecorated(true);
        window.setOpacity(ConstantsInitializers.GUI_SCROLLWINDOW_OPACITY);
        window.setLocation(MouseInfo.getPointerInfo().getLocation());
        window.getContentPane().add(this);
        window.getContentPane().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        graphicsRectangleHandle = new Point();

        applyGeometricFeatures(viewDimension, imageDimension, mousePictureOriginOffset);

        window.setVisible(true);
    }

    private void applyGeometricFeatures(Dimension viewDimension, Dimension imageDimension, Point mousePictureOriginOffset) {
        GraphicsConfiguration gc = window.getGraphicsConfiguration();
        GraphicsDevice gd = gc.getDevice();
        GraphicsConfiguration gdc = gd.getDefaultConfiguration();
        Rectangle  gcBounds = gdc.getBounds();
        Dimension gcDimension = new Dimension(gcBounds.getSize());

        int windowWidth;
        int windowHeight;

        double imageWidth = imageDimension.getWidth();
        double imageHeight = imageDimension.getHeight();

        if(imageWidth/gcDimension.getWidth() > imageHeight/gcDimension.getHeight()) {
            scaler = gcDimension.getHeight() / ( imageHeight * ConstantsInitializers.GUI_DISPLAY_TO_SCROLLWINDOW_SIZE_RATIO);
        } else {
            scaler = gcDimension.getWidth() / ( imageWidth * ConstantsInitializers.GUI_DISPLAY_TO_SCROLLWINDOW_SIZE_RATIO);
        }

        windowWidth = (int) (imageWidth * scaler);
        windowHeight = (int)(imageHeight * scaler);

        window.setSize(windowWidth, windowHeight);
//            System.out.printf("Scroll window size: %s\n",window.getSize().toString());



        graphicsRectangle.setSize((int)(viewDimension.getWidth() * scaler) - 1, (int)(viewDimension.getHeight() * scaler) - 1);

        graphicsRectangleHandle.setLocation(mousePictureOriginOffset.getX() * scaler, mousePictureOriginOffset.getY() * scaler);

    }

    public void closeWindow() {
        window.setVisible(false);
        window.dispose();
    }

    public Point getMouseInitialPosition() {
        return mouseInitialPosition;
    }

    public void setMouseInitialPosition(Point p) {
        mouseInitialPosition = new Point(p);
    }

    public void calculateDrawingAnchor(Point p) {
        graphicsRectangle.setLocation( (int) (p.getX() * scaler), (int) (p.getY() * scaler));
    }

    public void updateSymbols(Point p) {
        calculateDrawingAnchor(p);
        Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
        window.setLocation((int) ( mouseLoc.getX() - graphicsRectangle.getX() - graphicsRectangleHandle.getX()), (int) ( mouseLoc.getY() - graphicsRectangle.getY() - graphicsRectangleHandle.getY()));

//            System.out.printf("Scroll window position: %s\n",window.getLocation().toString());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(ConstantsInitializers.GUI_CHARTS_BG_COLOR);
        g.fillRect( 0, 0, getWidth() - 1, getHeight() -1 );
        g.setColor(Color.RED);
        g.drawRect( 0, 0, getWidth() - 1, getHeight() -1 );
        g.setColor(Color.BLUE);
/*            g.drawRect( (int) ( graphicsRectangle.getLocation().getX() ),
                        (int) ( graphicsRectangle.getLocation().getY() ),
                        (int) ( graphicsRectangle.getLocation().getX() > 0 ? graphicsRectangle.getWidth() - graphicsRectangle.getLocation().getX() : graphicsRectangle.getWidth() + graphicsRectangle.getLocation().getX() ),
                        (int) ( graphicsRectangle.getLocation().getY() > graphicsRectangle.getHeight() - 1 ? graphicsRectangle.getHeight() - graphicsRectangle.getLocation().getY() : graphicsRectangle.getHeight() + graphicsRectangle.getLocation().getY() )
            );*/

        g.drawRect( (int) ( graphicsRectangle.getLocation().getX() ),
                (int) ( graphicsRectangle.getLocation().getY() ),
                (int) ( graphicsRectangle.getWidth() ),
                (int) ( graphicsRectangle.getHeight() )
        );

//            System.out.printf("Rectangle location %s\n", graphicsRectangle.getLocation().toString());
    }

}
