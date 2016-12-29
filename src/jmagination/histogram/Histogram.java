package jmagination.histogram;

import jmagination.ConstantsInitializers;
import util.ImageCursor;
import util.PixelHood;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by darek on 19.11.2016.
 */

public class Histogram {

    BufferedImage img;
    WritableRaster raster;
    int scaleWidth;
    int bands;

    int maxLevelsValue;

    ArrayList<Integer[]> data;


    public Histogram(BufferedImage img) {
        this.img = img;
        this.raster = img.getRaster();
        this.scaleWidth = 256;

        this.bands = raster.getNumBands();
        this.maxLevelsValue = 0;

        data = new ArrayList<>(bands);
        for(int i=0;i<bands;++i) {
            data.add(new Integer[scaleWidth]);
        }
        fillZero();
        update();

    }

    private void fillZero() {
        for(Integer[] iT: data) {
            for(int level=0; level<iT.length; ++level) {
                iT[level] = 0;
            }
        }
    }

    public ArrayList<Integer[]> getData() {
        return data;
    }

    private void update() {

        fillZero();

        int width = img.getWidth();
        int height = img.getHeight();

        maxLevelsValue = 0;

        PixelHood<int[]> pixelHood = new PixelHood<>(0,0, new int[raster.getNumBands()]);
        ImageCursor imageCursor = new ImageCursor(img);

        do {
            imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_MIN);
            int[] pixel = pixelHood.getPixel(0,0);
            for(int i=0; i<bands; ++i) {
                ++data.get(i)[pixel[i]];
                if( data.get(i)[pixel[i]] > maxLevelsValue ) {
                    maxLevelsValue = data.get(i)[pixel[i]];
                }


            }

        } while (imageCursor.forward());
    }

    public BufferedImage createImg(String model, Dimension dimension) {

        int mariginLeft = ConstantsInitializers.GUI_CHART_MARIGIN_LEFT_SIZE_PX;
        int mariginRight = ConstantsInitializers.GUI_CHART_MARIGIN_RIGHT_SIZE_PX;
        int mariginTop = ConstantsInitializers.GUI_CHART_MARIGIN_TOP_SIZE_PX;
        int mariginBottom = ConstantsInitializers.GUI_CHART_MARIGIN_BOTTOM_SIZE_PX;

        int histImageWidth = scaleWidth + mariginLeft + mariginRight;
        int histImageHeight = (int) dimension.getHeight();



        switch(model) {
            case "INTERLACED":
                return drawImg2(histImageWidth, histImageHeight, mariginLeft, mariginRight, mariginTop, mariginBottom);
            default:
                return drawImg(histImageWidth, histImageHeight, mariginLeft, mariginRight, mariginTop, mariginBottom);
        }
    }

    public BufferedImage drawImg(int histImageWidth, int histImageHeight, int mariginLeft, int mariginRight,int mariginTop, int mariginBottom) {


        BufferedImage hist = new BufferedImage(histImageWidth,histImageHeight,BufferedImage.TYPE_INT_ARGB);
        Graphics hgr = hist.createGraphics();

        int chartAreaWidth = histImageWidth - ( mariginLeft + mariginRight );
        int chartAreaHeight = histImageHeight - ( mariginTop + mariginBottom);

        // draw background and frame
        hgr.setColor(ConstantsInitializers.GUI_CHARTS_BG_COLOR);
        hgr.fillRect(0,0,histImageWidth,histImageHeight);
        hgr.setColor(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR);
        hgr.drawRect(0,0,histImageWidth-1,histImageHeight-1);

        // draw chart area

        hgr.setColor(ConstantsInitializers.GUI_CHARTS_FG_COLOR);
        hgr.fillRect(mariginLeft, mariginTop, chartAreaWidth, chartAreaHeight);

        hgr.setColor(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR);
        hgr.drawRect(mariginLeft, mariginTop, chartAreaWidth, chartAreaHeight);

        Color channelColor = Color.DARK_GRAY;

        for(int ch=0; ch<data.size(); ++ch) {
            switch(ch) {
                case 0:
                    if ( data.size() > 1 ) { channelColor = new Color(255,0,0,240); }
                    break;
                case 1:
                    channelColor = new Color(0,255,0,190);
                    break;
                case 2:
                    channelColor = new Color(0,0,255,140);
                    break;
            }
            hgr.setColor(channelColor);

            Integer[] series = data.get(ch);

            double yScale = ( chartAreaHeight + 0.0 ) / maxLevelsValue;

            for(int level = 0; level < series.length; ++level) {
                int barWidth = 1;
                int barHeight = (int) ( series[level] * yScale );
                int barX = mariginLeft + level;
                int barY = mariginTop + chartAreaHeight - barHeight;


                hgr.setColor(channelColor);
                hgr.fillRect( barX, barY, barWidth, barHeight );

//                    System.out.printf("Level:Value %d:%d\n", barX, series[level]);
            }

            //draw labels
            if( ch == data.size() - 1 ) {


                //draw horizontal axis labels
                {
                    int minLevel = 0;
                    int maxLevel = series.length - 1;

                    int minDrawingLevel = mariginLeft;
                    int maxDrawingLevel = mariginLeft + chartAreaWidth;

                    int levelLabelX = mariginLeft;
                    int levelLabelY = mariginTop + chartAreaHeight + hgr.getFont().getSize();
                    int levelTickY = mariginTop + chartAreaHeight;
                    int levelTickSize = hgr.getFont().getSize() / 2;

                    int divPoints = ConstantsInitializers.GUI_CHART_X_GRID_POINTS;
                    int valueDelta = (maxLevel - minLevel) / ( divPoints + 1 );
                    int drawingDelta = ( maxDrawingLevel - minDrawingLevel) / ( divPoints + 1);

                    hgr.setColor(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR);

                    for (int step = 0; step <= divPoints; ++step) {
                        hgr.drawString(String.valueOf(minLevel + step * valueDelta), levelLabelX + 1,  levelLabelY);
                        hgr.drawRect( levelLabelX, levelTickY, 1, levelTickSize);

                        levelLabelX += drawingDelta;
                    }

                    //last label
                    hgr.drawString(String.valueOf(maxLevel), maxDrawingLevel + 1, levelLabelY);
                    hgr.drawRect( maxDrawingLevel, levelTickY, 1, levelTickSize);
                }

                //draw vertical axis labels
                {
                    int minLevel = 0;
                    int maxLevel = maxLevelsValue;

                    int minDrawingLevel = mariginTop + chartAreaHeight;
                    int maxDrawingLevel = mariginTop;

                    int levelLabelX = mariginLeft + chartAreaWidth + hgr.getFont().getSize();
                    int levelLabelY = minDrawingLevel;
                    int levelTickX = mariginLeft + chartAreaWidth;
                    int levelTickSize = hgr.getFont().getSize() / 2;

                    int divPoints = ConstantsInitializers.GUI_CHART_Y_GRID_POINTS;
                    int valueDelta = (maxLevel - minLevel) / ( divPoints + 1 );
                    int drawingDelta = ( maxDrawingLevel - minDrawingLevel) / ( divPoints + 1 );

                    hgr.setColor(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR);

                    for (int step = 0; step <= divPoints; ++step) {
                        hgr.drawString(String.valueOf(minLevel + step * valueDelta), levelLabelX,  levelLabelY + 1);
                        hgr.drawRect( levelTickX, levelLabelY, levelTickSize, 1);

                        levelLabelY += drawingDelta;
                    }

                    //last label
                    hgr.drawString(String.valueOf(maxLevel), levelLabelX, maxDrawingLevel + 1);
                    hgr.drawRect( levelTickX, maxDrawingLevel, levelTickSize , 1);
                }

            }
        }

        return hist;

    }


    public BufferedImage drawImg2(int histImageWidth, int histImageHeight, int mariginLeft, int mariginRight,int mariginTop, int mariginBottom) {

        int chartAreaWidth = histImageWidth - ( mariginLeft + mariginRight );
        int chartAreaHeight = histImageHeight - ( mariginTop + mariginBottom);

        BufferedImage hist = new BufferedImage(histImageWidth,histImageHeight,BufferedImage.TYPE_INT_ARGB);
        Graphics hgr = hist.createGraphics();

        // draw background and frame
        hgr.setColor(ConstantsInitializers.GUI_CHARTS_BG_COLOR);
        hgr.fillRect(0,0,histImageWidth,histImageHeight);
        hgr.setColor(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR);
        hgr.drawRect(0,0,histImageWidth-1,histImageHeight-1);

        // draw chart area

        hgr.setColor(ConstantsInitializers.GUI_CHARTS_FG_COLOR);
        hgr.fillRect(mariginLeft, mariginTop, chartAreaWidth, chartAreaHeight);

        hgr.setColor(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR);
        hgr.drawRect(mariginLeft, mariginTop, chartAreaWidth, chartAreaHeight);



        int seriesLength = data.get(0).length;

        //draw horizontal axis labels
        {
            int minLevel = 0;
            int maxLevel = seriesLength - 1;

            int minDrawingLevel = mariginLeft;
            int maxDrawingLevel = mariginLeft + chartAreaWidth;

            int levelLabelX = mariginLeft;
            int levelLabelY = mariginTop + chartAreaHeight + hgr.getFont().getSize();
            int levelTickY = mariginTop + chartAreaHeight;
            int levelTickSize = hgr.getFont().getSize() / 2;

            int divPoints = ConstantsInitializers.GUI_CHART_X_GRID_POINTS;
            int valueDelta = (maxLevel - minLevel) / ( divPoints + 1 );
            int drawingDelta = ( maxDrawingLevel - minDrawingLevel) / ( divPoints + 1);

            hgr.setColor(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR);

            for (int step = 0; step <= divPoints; ++step) {
                hgr.drawString(String.valueOf(minLevel + step * valueDelta), levelLabelX + 1,  levelLabelY);
                hgr.drawRect( levelLabelX, levelTickY, 1, levelTickSize);

                levelLabelX += drawingDelta;
            }

            //last label
            hgr.drawString(String.valueOf(maxLevel), maxDrawingLevel + 1, levelLabelY);
            hgr.drawRect( maxDrawingLevel, levelTickY, 1, levelTickSize);
        }

        //draw vertical axis labels
        {
            int minLevel = 0;
            int maxLevel = maxLevelsValue;

            int minDrawingLevel = mariginTop + chartAreaHeight;
            int maxDrawingLevel = mariginTop;

            int levelLabelX = mariginLeft + chartAreaWidth + hgr.getFont().getSize();
            int levelLabelY = minDrawingLevel;
            int levelTickX = mariginLeft + chartAreaWidth;
            int levelTickSize = hgr.getFont().getSize() / 2;

            int divPoints = ConstantsInitializers.GUI_CHART_Y_GRID_POINTS;
            int valueDelta = (maxLevel - minLevel) / (divPoints + 1);
            int drawingDelta = ( maxDrawingLevel - minDrawingLevel) / (divPoints + 1) ;

            hgr.setColor(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR);

            for (int step = 0; step <= divPoints; ++step) {
                hgr.drawString(String.valueOf(minLevel + step * valueDelta), levelLabelX,  levelLabelY + 1);
                hgr.drawRect( levelTickX, levelLabelY, levelTickSize, 1);

                levelLabelY += drawingDelta;
            }

            //last label
            hgr.drawString(String.valueOf(maxLevel), levelLabelX, maxDrawingLevel + 1);
            hgr.drawRect( levelTickX, maxDrawingLevel, levelTickSize , 1);
        }

        double yScale = ( chartAreaHeight + 0.0 ) / maxLevelsValue;

        for(int level = 0; level < seriesLength; ++level) {

            Color channelColor = Color.DARK_GRAY;

            ArrayList<ColorStrip> bar = new ArrayList<>();

            for(int ch=0; ch<data.size(); ++ch) {
                switch (ch) {
                    case 0:
                        if(data.size()>1) {
                            channelColor = new Color(200, 0, 0, 255);
                        }
                        break;
                    case 1:
                        channelColor = new Color(0, 200, 0, 255);
                        break;
                    case 2:
                        channelColor = new Color(0, 0, 200, 255);
                        break;
                }
                hgr.setColor(channelColor);
                bar.add(new ColorStrip(data.get(ch)[level], channelColor));
            }

            Collections.sort(bar);
            Collections.reverse(bar);

            int barWidth = 1;

            for(ColorStrip cs: bar) {
                int barHeight = (int) ( cs.size * yScale );
                int barX = mariginLeft + level;
                int barY = mariginTop + chartAreaHeight - barHeight;

                hgr.setColor(cs.color);
                hgr.fillRect( barX, barY, barWidth, barHeight );
            }


        }


        return hist;

    }
}

class ColorStrip implements Comparable<ColorStrip>{
    int size;
    Color color;

    public ColorStrip(int size, Color color) {
        this.size = size;
        this.color = color;
    }


    @Override
    public int compareTo(ColorStrip o) {
        if(size==o.size) {
            return 0;
        }
        else {
            if(size>o.size) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}