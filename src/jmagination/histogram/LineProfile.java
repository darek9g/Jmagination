package jmagination.histogram;

import jmagination.ConstantsInitializers;
import jmagination.gui.ImagePanel2;
import jmagination.gui.ImagePanel3;
import jmagination.gui.ImagePanel4;
import util.MaskGenerator;
import util.SimpleHSVBufferedImage;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import static jmagination.Workspace.crateAltImage;

/**
 * Created by darek on 27.01.2017.
 */
public class LineProfile implements ChangeListener, ActionListener{

    public static final int RGB_MODE = 0;
    public static final int HSV_MODE = 1;

    ImagePanel4 dataSource;
    ArrayList <DataDrawer> dataDrawers;

    int mariginLeft = ConstantsInitializers.GUI_CHART_MARIGIN_LEFT_SIZE_PX;
    int mariginRight = ConstantsInitializers.GUI_CHART_MARIGIN_RIGHT_SIZE_PX;
    int mariginTop = ConstantsInitializers.GUI_CHART_MARIGIN_TOP_SIZE_PX;
    int mariginBottom = ConstantsInitializers.GUI_CHART_MARIGIN_BOTTOM_SIZE_PX;

    public LineProfile(ImagePanel4 dataSource) {
        this.dataDrawers = new ArrayList<>();
        this.dataSource = dataSource;
        this.dataSource.addActionListener(this);
    }



    protected class DataDrawer {

        ImagePanel3 drawer;
        int[] selectedChannels;
        int mode;

        public DataDrawer(ImagePanel3 dataDrawer, int[] selectedChannels, int mode) {
            this.drawer = dataDrawer;
            this.selectedChannels = selectedChannels;
            this.mode = mode;
        }
    }

    public void addDataDrawer(ImagePanel3 dataDrawer, int[] selectedChannels, int mode) {
        dataDrawers.add(new DataDrawer(dataDrawer, selectedChannels, mode));
    }


    public SimpleHSVBufferedImage drawImg(ImagePanel3 dataDrawer, LineProfileData data, int[] selectedChannels, int mode) {
        ArrayList<Integer> verifiedChannels = new ArrayList<>();

//        int minImageWidth = dataDrawer.getWidth();
        int minImageWidth = 50;

        int yScale = 1;
        int minYValue = 0;
        int maxYValue = 255;
        float minLevel = 0.0f;
        float maxLevel = 255.0f;

        int minXValue = 0;
        int maxXValue = 255;

        switch (mode) {
            case RGB_MODE:
                yScale = 1;
                maxYValue = 255;
                minLevel = 0.0f;
                maxLevel = 255;
                break;
            case HSV_MODE:
                yScale = 100;
                maxYValue = 100;
                minLevel = 0.0f;
                maxLevel = 1.0f;
                break;
            default:
        }

        minXValue = data.data.get(0).distance;
        maxXValue = data.data.get(data.data.size()-1).distance;

        int chartAreaWidth = minImageWidth > data.length ? minImageWidth : data.length;
        int chartAreaHeight = maxYValue;

        int imageWidth = chartAreaWidth + mariginLeft + mariginRight;
        int imageHeight = chartAreaHeight + mariginTop + mariginBottom;

        SimpleHSVBufferedImage chart = new SimpleHSVBufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics hgr = chart.createGraphics();

        for(int i=0;i<data.channels; i++) {
            for (int channel: selectedChannels) {
                if(channel == i) {
                    verifiedChannels.add(i);
                }
            }
        }

        if(verifiedChannels.size()==0) {
            hgr.setColor(dataDrawer.getBackground());
            hgr.fillRect(0, 0, imageWidth, imageHeight);
            hgr.setColor(Color.BLACK);
            hgr.drawString("Dane nie dostępne", 0, hgr.getFont().getSize());
            hgr.dispose();
            return chart;
        }

        // draw background and frame
        hgr.setColor(ConstantsInitializers.GUI_CHARTS_BG_COLOR);
        hgr.fillRect(0, 0, imageWidth, imageHeight);
        hgr.setColor(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR);
        hgr.drawRect(0, 0, imageWidth - 1, imageHeight - 1);

        // draw chart area

        hgr.setColor(ConstantsInitializers.GUI_CHARTS_FG_COLOR);
        hgr.fillRect(mariginLeft, mariginTop, chartAreaWidth, chartAreaHeight);

        hgr.setColor(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR);
        hgr.drawRect(mariginLeft, mariginTop, chartAreaWidth, chartAreaHeight);


        int seriesLength = data.length;

        //draw horizontal axis labels
        {

            int minDrawingLevel = mariginLeft;
            int maxDrawingLevel = mariginLeft + chartAreaWidth;

            int levelLabelX = mariginLeft;
            int levelLabelY = mariginTop + chartAreaHeight + hgr.getFont().getSize();
            int levelTickY = mariginTop + chartAreaHeight;
            int levelTickSize = hgr.getFont().getSize() / 2;

            int divPoints = 2;
            int valueDelta = (maxXValue - minXValue) / (divPoints + 1);
            int drawingDelta = (maxDrawingLevel - minDrawingLevel) / (divPoints + 1);

            hgr.setColor(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR);

            for (int step = 0; step <= divPoints; ++step) {
                String s = String.valueOf(minXValue + step * valueDelta);

                hgr.drawString(s,levelLabelX + 1, levelLabelY);
                hgr.drawRect(levelLabelX, levelTickY, 1, levelTickSize);

                levelLabelX += drawingDelta;
            }

            //last label
            hgr.drawString(String.valueOf(maxXValue), maxDrawingLevel + 1, levelLabelY);
            hgr.drawRect(maxDrawingLevel, levelTickY, 1, levelTickSize);
        }

        //draw vertical axis labels
        {

            int minDrawingLevel = mariginTop + chartAreaHeight;
            int maxDrawingLevel = mariginTop;

            int levelLabelX = mariginLeft + chartAreaWidth + hgr.getFont().getSize();
            int levelLabelY = minDrawingLevel;
            int levelTickX = mariginLeft + chartAreaWidth;
            int levelTickSize = hgr.getFont().getSize() / 2;

            int divPoints = 2;
            int valueDelta = (maxYValue - minYValue) / (divPoints + 1);
            int drawingDelta = (maxDrawingLevel - minDrawingLevel) / (divPoints + 1);

            hgr.setColor(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR);

            for (int step = 0; step <= divPoints; ++step) {
                String s;
                switch (mode) {
                    case RGB_MODE:
                        s = String.valueOf((int) (minLevel/yScale + step * valueDelta/yScale));
                        break;
                    case HSV_MODE:
                        s = String.valueOf( (float)((minLevel + 0.0)/yScale + (step * valueDelta + 0.0)/yScale));
                        break;
                    default:
                        s = String.valueOf(minLevel/yScale + step * valueDelta/yScale);
                }
                hgr.drawString(s, levelLabelX, levelLabelY + 1);
                hgr.drawRect(levelTickX, levelLabelY, levelTickSize, 1);

                levelLabelY += drawingDelta;
            }

            //last label
            String s;
            switch (mode) {
                case RGB_MODE:
                    s = String.valueOf((int) (maxLevel));
                    break;
                case HSV_MODE:
                    s = String.valueOf( (float)(maxLevel));
                    break;
                default:
                    s = String.valueOf(maxLevel);
            }
            hgr.drawString(s, levelLabelX, maxDrawingLevel + 1);
            hgr.drawRect(levelTickX, maxDrawingLevel, levelTickSize, 1);
        }


        for (int level = 0; level < seriesLength; ++level) {

            Color channelColor = Color.DARK_GRAY;

            ArrayList<ColorStrip> bar = new ArrayList<>();

            int channelsDrawed = 0;
            for (int ch : verifiedChannels) {
                channelsDrawed++;
                switch (ch) {
                    case 0:
                        if (data.channels > 1) {
                            channelColor = new Color(200, 0, 0, 255);
                        } else {
                            channelColor = Color.DARK_GRAY;
                        }
                        break;
                    case 1:
                        channelColor = new Color(0, 200, 0, 255);
                        break;
                    case 2:
                        channelColor = new Color(0, 0, 200, 255);
                        break;
                    case 3:
                        channelColor = new Color(255, 95, 2);
                        break;
                    case 4:
                        channelColor = new Color(255, 188, 2);
                        break;
                    case 5:
                        channelColor = new Color(124, 1, 255);
                        break;
                    default:
                }
                hgr.setColor(channelColor);
                if(level==0) {
                    String s = data.channelNames[ch];
                    Rectangle2D stringBounds = hgr.getFontMetrics().getStringBounds(s, hgr);
                    int fontDescent = hgr.getFontMetrics().getDescent();
                    int xLegend = imageWidth - (int)stringBounds.getWidth() - 10;
                    int yLegend =  (channelsDrawed) * (int)stringBounds.getHeight();
                    hgr.fillRect(xLegend, yLegend - (int)stringBounds.getHeight()  + fontDescent, (int)stringBounds.getWidth(), (int)stringBounds.getHeight());
                    hgr.setColor(new Color(238, 238, 238));
                    hgr.drawString(s, xLegend, yLegend);

                }
                hgr.setColor(channelColor);
//                System.out.printf("%d %f %d\n", level, data.data.get(level).channelData[ch], Math.round(data.data.get(level).channelData[ch]*yScale));
                bar.add(new ColorStrip(Math.round(data.data.get(level).channelData[ch]*yScale), channelColor));
            }

            Collections.sort(bar);
            Collections.reverse(bar);

            int barWidth = 1;

            for (ColorStrip cs : bar) {
//                int barHeight = (int) (cs.size * yScale);
                int barHeight = (int) (cs.size);
                int barX = mariginLeft + level;
                int barY = mariginTop + chartAreaHeight - barHeight;

                hgr.setColor(cs.color);
                hgr.fillRect(barX, barY, barWidth, barHeight);
            }
        }
        return chart;
    }


    @Override
    public void stateChanged(ChangeEvent e) {
        boolean enabled = false;
        for(DataDrawer dataDrawer: dataDrawers) {
            if (dataDrawer.drawer.isShowing()) {
                dataSource.enableFeatures();
//                dataDrawer.drawer.setImage(drawImg(dataDrawer.drawer,dataSource.getLineProfileData(), dataDrawer.selectedChannels,dataDrawer.mode));
                if(dataSource.isDrawingFeatures() == false) {
                    Color color = dataDrawer.drawer.getBackground();
                    dataDrawer.drawer.setImage(crateAltImage(dataDrawer.drawer.getSize(), "Dane dla obrazu niedostępne", color));
                }
                enabled = true;
            }
        }
        if(enabled == false) {
            dataSource.disableFeatures();
            for (DataDrawer dataDrawer : dataDrawers) {
                Color color = dataDrawer.drawer.getBackground();
                dataDrawer.drawer.setImage(crateAltImage(dataDrawer.drawer.getSize(), "Dane nie dostępne", color));
            }
        }
    }

    public void update(JTabbedPane jTabbedPane) {
        boolean enabled = false;
        for(DataDrawer dataDrawer: dataDrawers) {
            if (dataDrawer.drawer.isShowing()) {
                dataSource.enableFeatures();
//                dataDrawer.drawer.setImage(drawImg(dataDrawer.drawer,dataSource.getLineProfileData(), dataDrawer.selectedChannels,dataDrawer.mode));
                if(dataSource.isDrawingFeatures() == false) {
                    Color color = dataDrawer.drawer.getBackground();
                    dataDrawer.drawer.setImage(crateAltImage(dataDrawer.drawer.getSize(), "Dane dla obrazu niedostępne", color));
                }
                enabled = true;
            }
        }
        if(enabled == false) {
            dataSource.disableFeatures();
            for (DataDrawer dataDrawer : dataDrawers) {
                Color color = dataDrawer.drawer.getBackground();
                dataDrawer.drawer.setImage(crateAltImage(dataDrawer.drawer.getSize(), "Dane dla obrazu niedostępne", color));
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for(DataDrawer dataDrawer: dataDrawers) {
                dataDrawer.drawer.setImage(drawImg(dataDrawer.drawer,dataSource.getLineProfileData(), dataDrawer.selectedChannels,dataDrawer.mode));
        }

    }
}