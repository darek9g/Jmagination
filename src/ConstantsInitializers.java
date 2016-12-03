import java.awt.*;

/**
 * Created by darek on 27.10.16.
 */
public final class ConstantsInitializers {

    public static final Dimension GUI_BUTTON_SIZE_LONG = new Dimension(320,30);
    public static final Dimension GUI_BUTTON_SIZE_SHORT = new Dimension(140,20);
    public static final Dimension GUI_IMAGEWINDOW_SIZE = new Dimension(640,480);


    public static final Font GUI_FONT_TITLE = Font.getFont(Font.MONOSPACED);
//    public static final Font GUI_FONT_TITLE = Font.getFont(Font.MONOSPACED).deriveFont((float) 1.0);

    public static final int GUI_WORKSPACEWINDOW_GAP_SIZE = 0;
    public static final Dimension GUI_WORKSCACE_OPER_PANEL_SIZE = new Dimension(400,300);

    public static final Color GUI_DRAWING_BG_COLOR = new Color(50, 44, 58);
    public static final Color GUI_CONTROLS_BG_COLOR = new Color(50, 44, 58);
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

    private ConstantsInitializers() {
        throw new AssertionError();
    }

}
