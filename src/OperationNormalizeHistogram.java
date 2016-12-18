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
 * Created by darek on 30.11.2016.
 */

public class OperationNormalizeHistogram extends Operation {

    Parameters parameters;
    String[] runModes = { "Średnich", "Losowa", "Według sąsiedztwa 3x3"};
    JComboBox<String> methodSelect = new JComboBox<>(runModes);

    public OperationNormalizeHistogram(ImageServer srcImageServer) {
        super();
        this.label = "Wyrównaj histogram";
        categories.add("LAB 1");
        categories.add("Wielopunktowe");

        parameters = new Parameters();
    }

    @Override
    public BufferedImage RunOperationFunction(BufferedImage bufferedImage) {

        String method = (String) methodSelect.getSelectedItem();
        parameters.method = method;

        Histogram histogram = new Histogram(bufferedImage);
        return normalizeHistogramFunction(bufferedImage, histogram);
    }

    @Override
    public void drawConfigurationPanel(JPanel panel) {
        panel.setLayout(new GridBagLayout());
        panel.setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);
        JLabel title = new JLabel("Wyrównaj histogram");

        int panelX = 0;
        int panelY = 0;

        panel.add(title, new GUIStyler.ParamsGrid(panelX,panelY++));

        JTextArea description = new JTextArea("Opis - UZUPEŁNIĆ");
        description.setEditable(false);
        panel.add(description, new GUIStyler.ParamsGrid(panelX,panelY++));

        methodSelect.addActionListener(runOperationTrigger);

        panel.add(methodSelect, new GUIStyler.ParamsGrid(panelX,panelY++));

/*        JButton apply  = new JButton("Wykonaj");
        panel.add(apply, new GUIStyler.ParamsGrid(panelX,panelY++));
        apply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Run();
            }
        });*/

    }

    @Override
    public Operation Clone() {
        return new OperationNormalizeHistogram(null);
    }

    public BufferedImage normalizeHistogramFunction(BufferedImage srcImage, Histogram histogram) {

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

                switch(parameters.method) {
                    case "Średnich":
                        newLevels.get(ch)[level] = (int) ( ( leftLevelLimits.get(ch)[level] + rightLevelLimits.get(ch)[level]) / 2.0 );
                        break;
                    case "Losowa":
                        newLevels.get(ch)[level] = rightLevelLimits.get(ch)[level] - leftLevelLimits.get(ch)[level];
                }

            }
        }


        //applying changes


//        Random random = new Random();

        int shift = 0;
        int mask = 0x000000ff;

        for(int ch=0; ch<channels; ++ch) {
            int levels = srcHistogramData.get(ch).length;
            Integer[] leftLevelLimitsChannel = leftLevelLimits.get(ch);
            Integer[] rightLevelLimitsChannel = rightLevelLimits.get(ch);
            Integer[] newLevelsChannel = newLevels.get(ch);


            for(int w=0; w<width; ++w) {
                for(int h=0; h<height; ++h) {
                    int colorStripe = srcImage.getRGB(w, h);
                    int level = ( colorStripe & mask ) >> shift;

                    //selecting new level

                    int newLevel = level;
                    int newColorStripe = colorStripe & (~mask);

                    switch(parameters.method) {
                        case "Średnich":
                            if(level == newLevelsChannel[level]) { continue; };
                            newLevel = newLevelsChannel[level];
                            break;
                        case "Losowa":
                            if(level == newLevelsChannel[level]) { continue; };
                            int r = ThreadLocalRandom.current().nextInt(0, newLevelsChannel[level]+1);
                            newLevel = leftLevelLimitsChannel[level] + r;
                            break;
                        case "Według sąsiedztwa 3x3":

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

    private class Parameters {
        String method = "Średnich";

        public Parameters() {}
    }

}