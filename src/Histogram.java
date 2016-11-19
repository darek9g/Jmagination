import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by darek on 19.11.2016.
 */

public class Histogram {

    BufferedImage img;
    int scaleWidth;
    int channels;

    int maxLevelsValue;

    ArrayList<Integer[]> data;


    public Histogram(BufferedImage img) {
        this.img = img;
        this.scaleWidth = 256;

        this.channels = img.getColorModel().getNumColorComponents();
        this.maxLevelsValue = 0;

        data = new ArrayList<>(channels);
        for(int ch=0;ch<channels;++ch) {
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

        for(int w=0; w<width; ++w) {
            for(int h=0; h<height; ++h) {
                int colorStripe = img.getRGB(w, h);

                int shift = 0;
                int mask = 0x000000ff;
                for(int ch=0; ch<channels; ++ch) {
                    int level = ( colorStripe & mask ) >> shift;
                    shift+=8;
                    mask*=0x100;
                    ++data.get(ch)[level];
                    if( data.get(ch)[level] > maxLevelsValue ) {
                        maxLevelsValue = data.get(ch)[level];
//                            System.out.println("Increasing max level record at " + level);
                    }

                }
            }
        }
    }

    public BufferedImage createImg() {

        int mariginLeft = ConstantsInitializers.GUI_CHART_MARIGIN_LEFT_SIZE_PX;
        int mariginRight = ConstantsInitializers.GUI_CHART_MARIGIN_RIGHT_SIZE_PX;
        int mariginTop = ConstantsInitializers.GUI_CHART_MARIGIN_TOP_SIZE_PX;
        int mariginBottom = ConstantsInitializers.GUI_CHART_MARIGIN_BOTTOM_SIZE_PX;

        int histImageWidth = scaleWidth + mariginLeft + mariginRight;
        int histImageHeight = img.getHeight();

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

        Color channelColor = Color.DARK_GRAY;

        for(int ch=0; ch<data.size(); ++ch) {
            switch(ch) {
                case 0:
                    if ( data.size() > 1 ) { channelColor = Color.RED; hgr.setXORMode(Color.RED); }
                    break;
                case 1:
                    channelColor = Color.GREEN;
                    hgr.setXORMode(Color.RED);
                    break;
                case 2:
                    channelColor = Color.BLUE;
                    hgr.setXORMode(Color.GREEN);
                    break;
            }
            hgr.setColor(channelColor);

            Integer[] series = data.get(ch);

            double yScale = ( chartAreaHeight + 0.0 ) / maxLevelsValue;
//                System.out.println("Scale " + yScale + ", Image height " + histImageHeight + ", maxLevels " + maxLevelsValue);

            for(int level = 0; level < series.length; ++level) {
                int barWidth = 1;
                int barHeight = (int) ( series[level] * yScale );
                int barX = mariginLeft + level;
                int barY = mariginTop + chartAreaHeight - barHeight;

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
                    int valueDelta = (maxLevel - minLevel) / divPoints;
                    int drawingDelta = ( maxDrawingLevel - minDrawingLevel) / divPoints;

                    hgr.setColor(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR);

                    for (int step = 0; step < divPoints; ++step) {
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
                    int valueDelta = (maxLevel - minLevel) / divPoints;
                    int drawingDelta = ( maxDrawingLevel - minDrawingLevel) / divPoints;

                    hgr.setColor(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR);

                    for (int step = 0; step < divPoints; ++step) {
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

        for(int ch=0; ch<data.size(); ++ch) {
            switch (ch) {
                case 0:
                    if (data.size() > 1) {
                        channelColor = Color.RED;
                    }
                    break;
                case 1:
                    channelColor = Color.GREEN;
                    break;
                case 2:
                    channelColor = Color.BLUE;
                    break;
            }
            hgr.setColor(channelColor);

            Integer[] series = data.get(ch);

            double yScale = (chartAreaHeight + 0.0) / maxLevelsValue;
//                System.out.println("Scale " + yScale + ", Image height " + histImageHeight + ", maxLevels " + maxLevelsValue);

            int oldbarX = 0;
            int oldbarY = 0;

            for (int level = 0; level < series.length; ++level) {
                int barWidth = 1;
                int barHeight = (int) (series[level] * yScale);
                int barX = mariginLeft + level;
                int barY = mariginTop + chartAreaHeight - barHeight;

                if(level==0) {
                    hgr.drawOval(barX, barY, 1, 1);
                } else {
                    hgr.drawLine(oldbarX, oldbarY, barX, barY);
                }
                oldbarX = barX;
                oldbarY = barY;

//                    System.out.printf("Level:Value %d:%d\n", barX, series[level]);
            }
        }

        return hist;

    }


}