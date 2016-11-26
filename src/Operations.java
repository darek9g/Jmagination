import com.sun.corba.se.spi.orb.Operation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by darek on 06.11.2016.
 */
public class Operations {


    public static ArrayList<Operation> registerOperationsForImageServer(ImageServer imageServer, Jmagination jmagination) {
        ArrayList<Operation>availableOperations = new ArrayList<Operation>();

        availableOperations.add(new duplicate(imageServer, jmagination));
        availableOperations.add(new convertToGray(imageServer, jmagination));
        availableOperations.add(new normalizeHistogram(imageServer, jmagination));
        availableOperations.add(new equalizeHistogram(imageServer, jmagination));

        return availableOperations;
    }

    public static class OperationManager {

        ArrayList<Operation> doneOperations;

        public OperationManager() {

            doneOperations = new ArrayList<Operation>();
        }
    }

    public static abstract class Operation {

        ImageServer srcImageServer;
        String label = "Dummy";
        Jmagination jmagination;

        public Operation(Jmagination jmagination) {
            this.jmagination = jmagination;
        }

        public Operation(ImageServer srcImageServer, Jmagination jmagination) {
            this(jmagination);
            this.srcImageServer = srcImageServer;
        }

        public String getLabel() {
            return label;
        }

        public abstract BufferedImage RunOperation(ImageServer srcImageServer);

        public abstract JPanel getConfiguratorPanel();

        public void Run() {
            jmagination.addImage(new ImageServer(RunOperation(srcImageServer),srcImageServer, jmagination));
        }
    }

    public static class duplicate extends Operation{

        JPanel configurationPanel;

        public duplicate(ImageServer srcImageServer, Jmagination jmagination) {
            super(srcImageServer, jmagination);
            this.label = "Duplicate";
            configurationPanel = buildConfigurationPanel();

        }

        @Override
        public BufferedImage RunOperation(ImageServer srcImageServer) {

            BufferedImage srcImage = srcImageServer.getImg();

            return duplicateImageFunction(srcImage);
        }

        @Override
        public JPanel getConfiguratorPanel() {
            return configurationPanel;
        }

        private JPanel buildConfigurationPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_COLOR);
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

    public static BufferedImage duplicateImageFunction(BufferedImage srcImage) {
        ColorModel cm = srcImage.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = srcImage.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static class convertToGray extends Operation{

        JPanel configurationPanel;

        public convertToGray(ImageServer srcImageServer, Jmagination jmagination) {
            super(srcImageServer, jmagination);
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
            panel.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_COLOR);

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

        public equalizeHistogram(ImageServer srcImageServer, Jmagination jmagination) {
            super(srcImageServer, jmagination);
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
            panel.setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);
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

        public normalizeHistogram(ImageServer srcImageServer, Jmagination jmagination) {
            super(srcImageServer, jmagination);
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
            panel.setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);
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

        //calculating average histogram value per channel
        {
            for (int ch = 0; ch < channels; ++ch) {
                int levels = srcHistogramData.get(ch).length;
                Integer[] srcHistogramDataChannel = srcHistogramData.get(ch);

                for (int level = 0; level < levels; ++level) {
                    histogramAverages[ch] += level * srcHistogramDataChannel[level];
                }
                histogramAverages[ch] =  (int) ( ( histogramAverages[ch] + 0.0 ) / ( width * height ) );
            }
        }

        //calculating ranges for new levels: left, right
        ArrayList<Integer[]> leftLevelLimits = new ArrayList<>();
        ArrayList<Integer[]> rightLevelLimits = new ArrayList<>();
        ArrayList<Integer[]> histogramIntegral = new ArrayList<>();
        ArrayList<Integer[]> newLevels = new ArrayList<>();

        for(int ch=0; ch<channels; ++ch) {
            int levels = srcHistogramData.get(ch).length;
            leftLevelLimits.add(new Integer[levels]);
            rightLevelLimits.add(new Integer[levels]);
            histogramIntegral.add(new Integer[levels]);
            newLevels.add(new Integer[levels]);
        }

        for(int ch=0; ch<channels; ++ch) {
            int levels = srcHistogramData.get(ch).length;
            Integer[] srcHistogramDataChannel = srcHistogramData.get(ch);

            int R = 0;
            int Hint = 0;

            for(int level=0; level<levels; ++level) {
                leftLevelLimits.get(ch)[level] = R;
                Hint += srcHistogramDataChannel[level];

                while (Hint>histogramAverages[ch]) {
                    Hint -= histogramAverages[ch];
                    ++R;
                }
                rightLevelLimits.get(ch)[level] = R;

                switch(runMode) {
                    case "Average":
                        newLevels.get(ch)[level] = (int) ( ( leftLevelLimits.get(ch)[level] + rightLevelLimits.get(ch)[level]) / 2.0 );
                        break;
                    case "Random":
                        newLevels.get(ch)[level] = rightLevelLimits.get(ch)[level] - leftLevelLimits.get(ch)[level];
                }

            }
        }


        //applying changes


//        Random random = new Random();

        for(int ch=0; ch<channels; ++ch) {
            int levels = srcHistogramData.get(ch).length;
            Integer[] leftLevelLimitsChannel = leftLevelLimits.get(ch);
            Integer[] rightLevelLimitsChannel = rightLevelLimits.get(ch);
            Integer[] newLevelsChannel = newLevels.get(ch);

            int shift = 0;
            int mask = 0x000000ff;

            for(int w=0; w<width; ++w) {
                for(int h=0; h<height; ++h) {
                    int colorStripe = srcImage.getRGB(w, h);
                    int level = ( colorStripe & mask ) >> shift;

                    //selecting new level

                    int newLevel = level;
                    int newColorStripe = colorStripe & (~mask);

                    switch(runMode) {
                        case "Average":
                            if(level == newLevelsChannel[level]) { continue; };
                            newLevel = newLevelsChannel[level];
                            break;
                        case "Random":
                            if(level == newLevelsChannel[level]) { continue; };
                            int r = ThreadLocalRandom.current().nextInt(0, newLevelsChannel[level]+1);
                            newLevel = leftLevelLimitsChannel[level] + r;
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

                            if(neighborhoodLevel>rightLevelLimitsChannel[level]) {
                                newLevel = rightLevelLimitsChannel[level];
                            } else {
                                if (neighborhoodLevel < leftLevelLimitsChannel[level]) {
                                    newLevel = leftLevelLimitsChannel[level];
                                } else {
                                    newLevel = (int) Math.round(neighborhoodLevel);
                                }
                            }
                            break;
                    }

                    newColorStripe = newColorStripe | ( newLevel << shift );
                    resultImg.setRGB(w,h,newColorStripe);
                }
            }

            shift+=8;
            mask*=0x100;
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

        int channels = colorImg.getColorModel().getNumColorComponents();

        for(int x=0; x<colorImg.getWidth(); ++x) {
            for(int y=0;y<colorImg.getHeight(); ++y) {

                int colorStripe = colorImg.getRGB(x,y);
                int grayStripe = 0xff000000 & colorStripe;
                int grayLevel = 0;

                {
                    int shift = 0;
                    int mask = 0x000000ff;
                    for (int ch = 0; ch < channels; ++ch) {
                        int level = (colorStripe & mask) >> shift;
                        //setting new level
                        grayLevel += level;

                        shift += 8;
                        mask *= 0x100;
                    }
                    grayLevel = (int) Math.round(grayLevel / (channels + 0.0));
                }

                {
                    int shift = 0;
                    for (int ch = 0; ch < channels; ++ch) {
                        int level = grayLevel << shift;
                        //setting new level
                        grayStripe = grayStripe | level;

                        shift += 8;
                    }
                }

                grayImg.setRGB(x,y,grayStripe);

            }
        }

        return grayImg;
    }




    private Operations() {
        throw new AssertionError();
    }
}
