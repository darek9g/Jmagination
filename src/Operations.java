import com.sun.corba.se.spi.orb.Operation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by darek on 06.11.2016.
 */
public class Operations {


    public static class OperationManager {

        ArrayList<Operation> newOperations;
        ArrayList<Operation> doneOperations;

        public OperationManager(ImageServer imageServer, Jmagination master) {
            newOperations = new ArrayList<Operation>();
            doneOperations = new ArrayList<Operation>();

            newOperations.add(new duplicate(imageServer, master));
            newOperations.add(new convertToGray(imageServer, master));
            newOperations.add(new normalizeHistogram(imageServer, master));
            newOperations.add(new equalizeHistogram(imageServer, master));
        }

        public ArrayList<Operation> getNewOperations() {
            return newOperations;
        }
    }

    public static abstract class Operation {

        ImageServer srcImageServer;
        String label = "Dummy";
        Jmagination master;

        public Operation(Jmagination master) {
            this.master = master;
        }

        public Operation(ImageServer srcImageServer, Jmagination master) {
            this(master);
            this.srcImageServer = srcImageServer;
        }

        public String getLabel() {
            return label;
        }

        public abstract BufferedImage RunOperation(ImageServer srcImageServer);

        public abstract JPanel getConfiguratorPanel();

        public void Run() {
            master.addImage(new ImageServer(RunOperation(srcImageServer),"from ", master));
        }
    }

    public static class duplicate extends Operation{

        JPanel configurationPanel;

        public duplicate(ImageServer srcImageServer, Jmagination master) {
            super(srcImageServer, master);
            this.label = "Duplicate";
            configurationPanel = buildConfigurationPanel();

        }

        @Override
        public BufferedImage RunOperation(ImageServer srcImageServer) {

            BufferedImage srcImage = srcImageServer.getImg();

            ColorModel cm = srcImage.getColorModel();
            boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
            WritableRaster raster = srcImage.copyData(null);
            return new BufferedImage(cm, raster, isAlphaPremultiplied, null);

        }

        @Override
        public JPanel getConfiguratorPanel() {
            return configurationPanel;
        }

        private JPanel buildConfigurationPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_ALT_COLOR);
            JLabel title = new JLabel("Duplicate");

            int panelX = 0;
            int panelY = 0;

            panel.add(title, new GUIStyler.ParamsGrid(panelX,panelY++));

            JTextArea description = new JTextArea("Copies the image into new buffer");
            description.setEditable(false);
            panel.add(description, new GUIStyler.ParamsGrid(panelX,panelY++));

            JButton apply  = new JButton("Apply");
            panel.add(apply, new GUIStyler.ParamsGrid(panelX,panelY++));
            apply.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Run();
                }
            });

            return panel;
        }
    }

    public static class convertToGray extends Operation{

        JPanel configurationPanel;

        public convertToGray(ImageServer srcImageServer, Jmagination master) {
            super(srcImageServer, master);
            this.label = "Convert to gray";
            configurationPanel = buildConfigurationPanel();

        }

        @Override
        public BufferedImage RunOperation(ImageServer srcImageServer) {
            BufferedImage srcImage = srcImageServer.getImg();
            return convertToGrayFunction(srcImage);
        }

        @Override
        public JPanel getConfiguratorPanel() {
            return configurationPanel;
        }

        private JPanel buildConfigurationPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_ALT_COLOR);

            int panelX = 0;
            int panelY = 0;

            JLabel title = new JLabel("Convert to gray");
            panel.add(title, new GUIStyler.ParamsGrid(panelX,panelY++));

            JTextArea description = new JTextArea("Combines each pixel color values into grey level");
            description.setEditable(false);
            panel.add(description, new GUIStyler.ParamsGrid(panelX,panelY++));

            JButton apply  = new JButton("Apply");
            panel.add(apply, new GUIStyler.ParamsGrid(panelX,panelY++));
            apply.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Run();
                }
            });

            return panel;
        }
    }

    public static class equalizeHistogram extends Operation{

        JPanel configurationPanel;

        public equalizeHistogram(ImageServer srcImageServer, Jmagination master) {
            super(srcImageServer, master);
            this.label = "Equalize Histogram";
            configurationPanel = buildConfigurationPanel();

        }

        @Override
        public BufferedImage RunOperation(ImageServer srcImageServer) {

            BufferedImage srcImage = srcImageServer.getImg();
            Histogram histogram = srcImageServer.getHistogram();
            return equalizeHistogramFunction(srcImage, histogram);


        }

        @Override
        public JPanel getConfiguratorPanel() {
            return configurationPanel;
        }

        private JPanel buildConfigurationPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_ALT_COLOR);
            JLabel title = new JLabel("Equalize histogram");

            int panelX = 0;
            int panelY = 0;

            panel.add(title, new GUIStyler.ParamsGrid(panelX,panelY++));

            JTextArea description = new JTextArea("Equalize histogram description");
            description.setEditable(false);
            panel.add(description, new GUIStyler.ParamsGrid(panelX,panelY++));

            JButton apply  = new JButton("Apply");
            panel.add(apply, new GUIStyler.ParamsGrid(panelX,panelY++));
            apply.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Run();
                }
            });

            return panel;
        }
    }


    public static class normalizeHistogram extends Operation{

        JPanel configurationPanel;
        String[] runModes = { "Average", "Random", "By neighborhood(3x3)"};
        JComboBox<String> modeSelect = new JComboBox<>(runModes);

        public normalizeHistogram(ImageServer srcImageServer, Jmagination master) {
            super(srcImageServer, master);
            this.label = "Normalize Histogram";
            configurationPanel = buildConfigurationPanel();

        }

        @Override
        public BufferedImage RunOperation(ImageServer srcImageServer) {

            BufferedImage srcImage = srcImageServer.getImg();
            Histogram histogram = srcImageServer.getHistogram();
            return normalizeHistogramFunction(srcImage, histogram, (String) modeSelect.getSelectedItem());


        }

        @Override
        public JPanel getConfiguratorPanel() {
            return configurationPanel;
        }

        private JPanel buildConfigurationPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_ALT_COLOR);
            JLabel title = new JLabel("Normalize histogram");

            int panelX = 0;
            int panelY = 0;

            panel.add(title, new GUIStyler.ParamsGrid(panelX,panelY++));

            JTextArea description = new JTextArea("Normalize histogram description - TODO");
            description.setEditable(false);
            panel.add(description, new GUIStyler.ParamsGrid(panelX,panelY++));



            panel.add(modeSelect, new GUIStyler.ParamsGrid(panelX,panelY++));



            JButton apply  = new JButton("Apply");
            panel.add(apply, new GUIStyler.ParamsGrid(panelX,panelY++));
            apply.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Run();
                }
            });

            return panel;
        }
    }


    public static BufferedImage normalizeHistogramFunction(BufferedImage srcImage, Histogram histogram, String runMode) {

        BufferedImage resultImg;

        {
            ColorModel cm = srcImage.getColorModel();
            boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
            WritableRaster raster = srcImage.copyData(null);
            resultImg = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
        }

        int width = srcImage.getWidth();
        int height = srcImage.getHeight();

        ArrayList<Integer[]> srcHistogramData = histogram.getData();

        int channels = srcHistogramData.size();
        int[] histogramAverages = new int[channels];
        for(int ch=0; ch<channels; ++ch) {
            histogramAverages[ch] = 0;
        }

        int levels = srcHistogramData.get(0).length;

        //calculating average histogram value per channel
        {
            for (int level = 0; level < levels; ++level) {
                for (int ch = 0; ch < channels; ++ch) {
                    histogramAverages[ch] += level * srcHistogramData.get(ch)[level];
                }
            }

            for (int ch = 0; ch < channels; ++ch) {
                histogramAverages[ch] /= width * height;
            }
        }

        //applying changes


//        Random random = new Random();

        for(int w=0; w<width; ++w) {
            for(int h=0; h<height; ++h) {
                int colorStripe = srcImage.getRGB(w, h);
                int newColorStripe = colorStripe & 0xff000000;

                int shift = 0;
                int mask = 0x000000ff;
                for(int ch=0; ch<channels; ++ch) {
                    int level = ( colorStripe & mask ) >> shift;

                    //offset levels
                    int offsetLevelsCount = 1;
                    int[] offsetLevels;
                    int base;
                    int delta;

//                    int levelFrequency = srcHistogramData.get(ch)[level];

/*                    if(level != 0 && histogramAverages[ch] != 0) {

                        if (level - histogramAverages[ch] > 0) {
                            offsetLevelsCount = level / histogramAverages[ch];
                            base = level;
                            delta = -histogramAverages[ch];
                        } else {
                            offsetLevelsCount = histogramAverages[ch] / level;
                            base = histogramAverages[ch];
                            delta = -level;
                        }
                        offsetLevels = new int[offsetLevelsCount];
                        for (int i = 0; i < offsetLevelsCount; ++i) {
                            offsetLevels[i] = base + i * delta;
                        }
                    } else {
                        offsetLevels = new int[offsetLevelsCount];
                        offsetLevels[0] = level;
                    }*/

                    if(histogramAverages[ch] != 0) {
                        if (level - histogramAverages[ch] > 0) {
                            offsetLevelsCount = level / histogramAverages[ch];
                            base = histogramAverages[ch];
                            delta = histogramAverages[ch];

                            offsetLevels = new int[offsetLevelsCount];
                            for (int i = 0; i < offsetLevelsCount; ++i) {
                                offsetLevels[i] = base + i * delta;
                            }
                        } else {
                            offsetLevels = new int[offsetLevelsCount];
                            offsetLevels[0] = level;
                        }

                    } else {
                        offsetLevels = new int[offsetLevelsCount];
                        offsetLevels[0] = level;
                    }

                    //selecting new level
                    int newLevel = level;

                    switch(runMode) {
                        case "Average":
                            newLevel = ( offsetLevels[0] + offsetLevels[offsetLevelsCount-1] ) / 2;
                            break;
                        case "Random":
                            Random random = new Random();
                            //int r = random.nextInt(offsetLevelsCount);
                            int r = ThreadLocalRandom.current().nextInt(0, offsetLevelsCount);
                            //int r = random.nextInt(offsetLevelsCount);
                            if(r!=offsetLevelsCount) { System.out.printf("Random r=>%d\n", r); }
                            newLevel = offsetLevels[r];
//                            System.out.println("Ran" + offsetLevelsCount);
                            break;
                        case "By neighborhood(3x3)":

                            //get 3x3 neighborhood average

                            double neighborhoodLevel = 0.0;
                            int neighborhoodCount = 0;

                            for(int i=-1; i<2; ++i) {
                                if(w+i<0 || w+i>width-1) continue;
                                for(int j=-1; j<2; ++j) {
                                    if(h+j<0 || h+j>height-1) continue;
                                    if(i==0 && j==0) continue;
                                        int neighborColorStripe = srcImage.getRGB(w+i, h+j);

                                        neighborhoodLevel += ( ( neighborColorStripe & mask ) >> shift );
                                        ++neighborhoodCount;
                                }
                            }
                            if(neighborhoodCount>0) neighborhoodLevel = neighborhoodLevel / neighborhoodCount;

                            int offsetLevelsBestMatch = 0;
                            for(int m=0; m<offsetLevelsCount; ++m) {
                                if( Math.abs(offsetLevels[m]-neighborhoodLevel) < Math.abs(offsetLevels[offsetLevelsBestMatch]-neighborhoodLevel)) offsetLevelsBestMatch = m;
                            }
                            newLevel = offsetLevels[offsetLevelsBestMatch];
                            break;
                    }
//                    System.out.printf("Old level=>New Level %d=>%d\n",level, newLevel);


                    newColorStripe = newColorStripe | ( newLevel << shift );
                    shift+=8;
                    mask*=0x100;
                }
                resultImg.setRGB(w,h,newColorStripe);
            }
        }

        return resultImg;
    }

    public static BufferedImage equalizeHistogramFunction(BufferedImage srcImage, Histogram histogram) {

        BufferedImage resultImg;

        {
            ColorModel cm = srcImage.getColorModel();
            boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
            WritableRaster raster = srcImage.copyData(null);
            resultImg = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
        }

        int width = srcImage.getWidth();
        int height = srcImage.getHeight();

        int pixels = width * height;

        int channels = histogram.getData().size();
        int levels = histogram.getData().get(0).length;

        ArrayList<Integer[]> intergatedHistogramData = new ArrayList<>(channels);
        for(int ch=0; ch<channels; ++ch) {
            Integer[] channelSrc =  histogram.getData().get(ch);
            Integer[] channelData = new Integer[levels];
            intergatedHistogramData.add(channelData);

            if(channelData.length>0) channelData[0] = channelSrc[0];
            for(int level=1; level<levels; ++level) {
                channelData[level] = channelData[level-1] + channelSrc[level];
            }

            for(int level=0; level<levels; ++level) {
                channelData[level] = (int) Math.floor( (levels - 1) * ( channelData[level] + 0.0 ) / pixels);
//                System.out.printf("Level %d value = %d\n", level, (int) channelData[level]);
            }


        }

        //applying changes

        for(int w=0; w<width; ++w) {
            for(int h=0; h<height; ++h) {
                int colorStripe = srcImage.getRGB(w, h);
                int newColorStripe = colorStripe & 0xff000000;

                int shift = 0;
                int mask = 0x000000ff;
                for(int ch=0; ch<channels; ++ch) {
                    int level = ( colorStripe & mask ) >> shift;

                    //setting new level
                    int newLevel = intergatedHistogramData.get(ch)[level];

//                  System.out.printf("Old level=>New Level %d=>%d\n",level, newLevel);

                    newColorStripe = newColorStripe | ( newLevel << shift );
                    shift+=8;
                    mask*=0x100;
                }
                resultImg.setRGB(w,h,newColorStripe);
            }
        }

        return resultImg;
    }

    public static BufferedImage convertToGrayFunction(BufferedImage colorImg) {

        BufferedImage grayImg = new BufferedImage(colorImg.getWidth(), colorImg.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        for(int x=0; x<colorImg.getWidth(); ++x) {
            for(int y=0;y<colorImg.getHeight(); ++y) {
                int colorCompositeValue = colorImg.getRGB(x,y);
                int colorValueRed = ( colorCompositeValue & ConstantsInitializers.RGB_RED_MASK ) >> ConstantsInitializers.RGB_RED_SHIFT;
                int colorValueGreen = ( colorCompositeValue & ConstantsInitializers.RGB_GREEN_MASK ) >> ConstantsInitializers.RGB_GREEN_SHIFT;
                int colorValueBlue = ( colorCompositeValue & ConstantsInitializers.RGB_BLUE_MASK ) >> ConstantsInitializers.RGB_BLUE_SHIFT;

                int colorCompositeGrey = (int) ( colorValueRed + colorValueGreen + colorValueBlue ) / 3;

                Color colorGrey = new Color(colorCompositeGrey, colorCompositeGrey, colorCompositeGrey);
                grayImg.setRGB(x,y,colorGrey.getRGB());

            }
        }

        return grayImg;
    }


    public static class Histogram {

        BufferedImage img;
        int scaleWidth;
        int channels;

        int maxLevelsValue;

        ArrayList<Integer[]> data;


        public Histogram(BufferedImage img) {
            this.img = img;
            this.scaleWidth = 256;

            this.channels = img.getColorModel().getNumColorComponents();
            this.maxLevelsValue = img.getHeight();

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
                        if ( data.size() > 1 ) { channelColor = Color.RED; };
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

            return hist;

        }


    }

    private Operations() {
        throw new AssertionError();
    }
}
