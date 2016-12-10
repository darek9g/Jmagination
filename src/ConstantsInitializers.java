import javax.swing.*;
import java.awt.*;

/**
 * Created by darek on 27.10.16.
 */
public final class ConstantsInitializers {

    public static final Dimension GUI_BUTTON_SIZE_LONG = new Dimension(320,30);
    public static final Dimension GUI_BUTTON_SIZE_SHORT = new Dimension(140,20);



    public static final Font GUI_FONT_TITLE = Font.getFont(Font.MONOSPACED);
//    public static final Font GUI_FONT_TITLE = Font.getFont(Font.MONOSPACED).deriveFont((float) 1.0);

    public static final int GUI_WORKSPACEWINDOW_GAP_SIZE = 0;
    public static final Dimension GUI_WORKSCACE_OPER_PANEL_SIZE = new Dimension(400,300);

    public static final Color GUI_DRAWING_BG_COLOR = new Color(238, 238, 238);
    public static final Color GUI_CONTROLS_BG_COLOR = new Color(238, 238, 238);
    //public static final Color GUI_CONTROLS_BG_COLOR = new Color(200, 221, 242);
    public static final Color GUI_CONTROLS_BG_ALT_COLOR = new Color(238, 238, 238);

    public static final int GUI_DISPLAY_TO_SCROLLWINDOW_SIZE_RATIO = 40;
    public static final float GUI_SCROLLWINDOW_OPACITY = (float) 0.3;

    public static final Color GUI_CHARTS_BG_COLOR = new Color(192, 192, 192);
    public static final Color GUI_CHARTS_FG_COLOR = new Color(212, 212, 212);
    public static final Color GUI_CHARTS_CONSTR_COLOR = new Color(128, 128, 128);

    public static final int GUI_CHART_MARIGIN_LEFT_SIZE_PX = 20;
    public static final int GUI_CHART_MARIGIN_RIGHT_SIZE_PX = 100;
    public static final int GUI_CHART_MARIGIN_TOP_SIZE_PX = 20;
    public static final int GUI_CHART_MARIGIN_BOTTOM_SIZE_PX = 20;

    public static final int GUI_CHART_X_GRID_POINTS = 7;
    public static final int GUI_CHART_Y_GRID_POINTS = 15;






    private static final int operationsPanelNorthHeight = 30;
    private static final int operationsPanelCentralHeight = 300;
    private static final int operationsPanelSouthHeight = 0;

    private static final int histogramPanelNorthHeight = 30;
    private static final int histogramPanelCentralHeight = 300;
    private static final int histogramPanelSouthHeight = 40;

    private static final int managerPanelNorthHeight = operationsPanelNorthHeight;
    private static final int managerPanelCentralHeight = operationsPanelCentralHeight + operationsPanelSouthHeight + histogramPanelNorthHeight + histogramPanelCentralHeight;
    private static final int managerPanelSouthHeight = histogramPanelSouthHeight;

    private static final int imagePanelNorthHeight = managerPanelNorthHeight;
    private static final int imagePanelCentralHeight = managerPanelCentralHeight;
    private static final int imagePanelSouthHeight = managerPanelSouthHeight;

    private static final int operationsPanelWidth = 360;

    private static final int operationsPanelNorthWidth = operationsPanelWidth;
    private static final int operationsPanelCentralWidth = operationsPanelWidth;
    private static final int operationsPanelSouthWidth = operationsPanelWidth;

    private static final int histogramPanelWidth = operationsPanelWidth;

    private static final int histogramPanelNorthWidth = histogramPanelWidth;
    private static final int histogramPanelCentralWidth = histogramPanelWidth;
    private static final int histogramPanelSouthWidth = histogramPanelWidth;

    private static final int managerPanelWidth = 300;

    private static final int managerPanelNorthWidth = managerPanelWidth;
    private static final int managerPanelCentralWidth = managerPanelWidth;
    private static final int managerPanelSouthWidth = managerPanelWidth;

    private static final int imagePanelWidth = 500;

    private static final int imagePanelNorthWidth = imagePanelWidth;
    private static final int imagePanelCentralWidth = imagePanelWidth;
    private static final int imagePanelSouthWidth = imagePanelWidth;


    public static final Dimension GUI_DIMENSION_managerPanelNorth = new Dimension(managerPanelNorthWidth, managerPanelNorthHeight);
    public static final Dimension GUI_DIMENSION_managerPanelCentral = new Dimension(managerPanelCentralWidth, managerPanelCentralHeight);
    public static final Dimension GUI_DIMENSION_managerPanelSouth = new Dimension(managerPanelSouthWidth, managerPanelSouthHeight);

    public static final Dimension GUI_DIMENSION_managerPanel = new Dimension(managerPanelNorthWidth, managerPanelNorthHeight + managerPanelCentralHeight + managerPanelSouthHeight);

    public static final Dimension GUI_DIMENSION_imagePanelNorth = new Dimension(imagePanelNorthWidth, imagePanelNorthHeight);
    public static final Dimension GUI_DIMENSION_imagePanelCentral = new Dimension(imagePanelCentralWidth, imagePanelCentralHeight);
    public static final Dimension GUI_DIMENSION_imagePanelSouth = new Dimension(imagePanelSouthWidth, imagePanelSouthHeight);

    public static final Dimension GUI_DIMENSION_imagePanel = new Dimension(imagePanelNorthWidth, imagePanelNorthHeight + imagePanelCentralHeight + imagePanelSouthHeight);

    public static final Dimension GUI_DIMENSION_operationsPanelNorth = new Dimension(operationsPanelNorthWidth, operationsPanelNorthHeight);
    public static final Dimension GUI_DIMENSION_operationsPanelCentral = new Dimension(operationsPanelCentralWidth, operationsPanelCentralHeight);
    public static final Dimension GUI_DIMENSION_operationsPanelSouth = new Dimension(operationsPanelSouthWidth, operationsPanelSouthHeight);

    public static final Dimension GUI_DIMENSION_operationsPanel = new Dimension(operationsPanelNorthWidth, operationsPanelNorthHeight + operationsPanelCentralHeight + operationsPanelSouthHeight);

    public static final Dimension GUI_DIMENSION_histogramPanelNorth = new Dimension(histogramPanelNorthWidth, histogramPanelNorthHeight);
    public static final Dimension GUI_DIMENSION_histogramPanelCentral = new Dimension(histogramPanelCentralWidth, histogramPanelCentralHeight);
    public static final Dimension GUI_DIMENSION_histogramPanelSouth = new Dimension(histogramPanelSouthWidth, histogramPanelSouthHeight);

    public static final Dimension GUI_DIMENSION_histogramPanel = new Dimension(histogramPanelNorthWidth, histogramPanelNorthHeight + histogramPanelCentralHeight + histogramPanelSouthHeight);

    public static final Dimension GUI_DIMENSION_level1Left = new Dimension(GUI_DIMENSION_managerPanel);
    public static final Dimension GUI_DIMENSION_level1Right = new Dimension((int) GUI_DIMENSION_operationsPanel.getWidth(), (int) ( GUI_DIMENSION_operationsPanel.getHeight() + GUI_DIMENSION_histogramPanel.getHeight()) );

    public static final Dimension GUI_DIMENSION_level0Right = new Dimension(GUI_DIMENSION_imagePanel);
    public static final Dimension GUI_DIMENSION_level0Left = new Dimension((int) ( GUI_DIMENSION_level1Left.getWidth() + GUI_DIMENSION_level1Right.getWidth()), (int) ( GUI_DIMENSION_level1Left.getHeight() ) );

    public static final Dimension GUI_DIMENSION_level0SplitPane = new Dimension((int) ( GUI_DIMENSION_level0Left.getWidth() + GUI_DIMENSION_level0Right.getWidth()), (int) ( GUI_DIMENSION_level0Left.getHeight() ) );


    private ConstantsInitializers() {
        throw new AssertionError();
    }

}
