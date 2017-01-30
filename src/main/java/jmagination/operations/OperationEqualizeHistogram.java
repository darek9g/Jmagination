package jmagination.operations;

import jmagination.ConstantsInitializers;
import jmagination.histogram.Histogram;
import jmagination.util.ImageCursor;
import jmagination.util.PixelHood;
import jmagination.util.SimpleHSVBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static jmagination.ConstantsInitializers.BR;
import static jmagination.util.SimpleHSVBufferedImage.NORMALIZATION_MODE_CUTTING;
import static jmagination.util.SimpleHSVBufferedImage.NORMALIZATION_MODE_PROPORTIONAL;

/**
 * Created by darek on 30.11.2016.
 */

public class OperationEqualizeHistogram extends Operation {

    public static final int MODE_AVARAGES = 0;
    public static final int MODE_RANDOM = 1;
    public static final int MODE_NEIGHBORHOOD = 2;


    Parameters parameters;
    String[] runModes = { "Średnich", "Losowa", "Według sąsiedztwa", "Własna"};

    JComboBox<String> methodSelect = new JComboBox<>(runModes);
    String[] neighborhoodSizesStrings = { "3x3", "5x5", "7x7", "9x9", "11x11", "13x13"};
    JComboBox<String> neighborhoodSizeSelect = new JComboBox<>(neighborhoodSizesStrings);

    public OperationEqualizeHistogram() {
        super();
        this.label = "Wyrównaj histogram";
        categories.add("Histogramowe");

        parameters = new Parameters();

        methodSelect.setSelectedIndex(0);
        neighborhoodSizeSelect.setEnabled(false);

        methodSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> jcb = (JComboBox<String>) e.getSource();
                if((String)jcb.getSelectedItem() == "Według sąsiedztwa") {
                    neighborhoodSizeSelect.setEnabled(true);
                } else {
                    neighborhoodSizeSelect.setEnabled(false);
                }
            }
        });
    }

    @Override
    public SimpleHSVBufferedImage RunOperationFunction(SimpleHSVBufferedImage bufferedImage, Histogram histogram) {

        String method = (String) methodSelect.getSelectedItem();
        parameters.method = method;

        parameters.neighborhoodBorderSize = 3 + 2 * neighborhoodSizeSelect.getSelectedIndex();

        return normalizeHistogramFunction(bufferedImage, histogram);
    }

    @Override
    public void drawConfigurationPanel(JPanel panel) {
        panel.setLayout(new GridBagLayout());
        panel.setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2,2, 2, 2);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0f;
        c.weighty = 1.0f;

        //tytuł
        c.gridx =0;
        c.gridy =0;
        c.gridwidth = 16;
        JLabel title = new JLabel("Wyrównanie histogramu");
        panel.add(title, c);

        // opis
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 16;
        JTextArea description = new JTextArea("Nadaje pikselom nowe wartości" + BR + "bliższe średniej wartości");
        description.setEditable(false);
        panel.add(description, c);

        // parametryzacja
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 4;
        JLabel jLabelMethodSelect = new JLabel("Metoda:");
        panel.add(jLabelMethodSelect, c);

        c.gridx = 4;
        c.gridy = 2;
        c.gridwidth = 4;
        panel.add(methodSelect, c);

        c.gridx = 8;
        c.gridy = 2;
        c.gridwidth = 4;
        JLabel jLabelNeighborHoodSelect = new JLabel("Sąsiedztwo:");
        panel.add(jLabelNeighborHoodSelect, c);

        c.gridx = 12;
        c.gridy = 2;
        c.gridwidth = 4;
        panel.add(neighborhoodSizeSelect, c);

        // wiersz sterowania wykonaniem
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 4;
        panel.add(jLabelColorMode, c);

        c.gridx+= c.gridwidth;
        c.gridy = 3;
        c.gridwidth = 4;
        panel.add(jRadioButtonColorModeRGB, c);

        c.gridx+= c.gridwidth;
        c.gridy = 3;
        c.gridwidth = 4;
        panel.add(jRadioButtonColorModeHSV, c);

        c.gridx+= c.gridwidth;
        c.gridy = 3;
        c.gridwidth = 4;
        panel.add(jButtonApply, c);


        configureColorModeControls();
    }

    @Override
    public Operation Clone() {
        return new OperationEqualizeHistogram();
    }



    public SimpleHSVBufferedImage normalizeHistogramFunction(SimpleHSVBufferedImage inImage, Histogram histogram) {

        int width = inImage.getWidth();
        int height = inImage.getHeight();

        SimpleHSVBufferedImage outImage = new SimpleHSVBufferedImage(width, height, inImage.getType());
        WritableRaster raster = inImage.getRaster();
        WritableRaster outRaster = outImage.getRaster();

        int bands = histogram.getData().size();

        ArrayList<Integer[]> leftLevelLimits = new ArrayList<>();
        ArrayList<Integer[]> rightLevelLimits = new ArrayList<>();
        ArrayList<Integer[]> newLevels = new ArrayList<>();
        int[] histogramAverages = new int[raster.getNumBands()];

        //obliczanie statystyk

        fillStatisticsTables(parameters.method, inImage, histogram, leftLevelLimits, rightLevelLimits, newLevels, histogramAverages);

        if(parameters.method == "Własna") {
            SimpleHSVBufferedImage firstImage = new SimpleHSVBufferedImage(width, height, inImage.getType());
            SimpleHSVBufferedImage secondImage = new SimpleHSVBufferedImage(width, height, inImage.getType());

            PixelHood<int[]> pixelHood = new PixelHood<>(0, 0, new int[raster.getNumBands()]);
            ImageCursor imageCursor = new ImageCursor(inImage);
            do {
                imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_COPY);

                int[] pixel = pixelHood.getPixel(0,0);
                int[] newFirstPixel = new int[pixel.length];
                int[] newSecondPixel = new int[pixel.length];

                for(int i = 0; i<pixel.length; ++i) {

                    newSecondPixel[i] = 0;

                    if(pixel[i] > histogramAverages[i]) {
                        newFirstPixel[i] = 0;
                        newSecondPixel[i] = pixel[i];
                    } else {
                        newFirstPixel[i] = pixel[i];
                        newSecondPixel[i] = 0;
                    }

                    firstImage.getRaster().setPixel(imageCursor.getPosX(), imageCursor.getPosY(), newFirstPixel);
                    secondImage.getRaster().setPixel(imageCursor.getPosX(), imageCursor.getPosY(), newSecondPixel);
                }

//            outRaster.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), newPixel);

            } while (imageCursor.forward());

            parameters.method = "Średnich";

            firstImage = normalizeHistogramFunction(firstImage, new Histogram(firstImage));
            firstImage.normalize(NORMALIZATION_MODE_CUTTING);
            secondImage = normalizeHistogramFunction(secondImage, new Histogram(secondImage));
            secondImage.normalize(NORMALIZATION_MODE_CUTTING);

            parameters.method = "Własna";

            PixelHood<int[]> firstPixelHood = new PixelHood<>(0,0, new int[firstImage.getRaster().getNumBands()]);
            PixelHood<int[]> secondPixelHood = new PixelHood<>(0,0, new int[secondImage.getRaster().getNumBands()]);
            ImageCursor firstImageCursor = new ImageCursor(firstImage);
            ImageCursor secondImageCursor = new ImageCursor(secondImage);
            for(int i = 0; i<height; ++i) {
                for(int j = 0; j < width; ++j) {

                    try {
                        firstImageCursor.goTo(j, i);
                    } catch (Exception e)
                    {
                        throw new IllegalStateException("Współrzędne (" + j + "," + i + ") spoza zakresu obrazka leftImage");
                    }
                    firstImageCursor.fillPixelHood(firstPixelHood, ImageCursor.COMPLETE_COPY);
                    int[] firstPixel = firstPixelHood.getPixel(0);

                    try {
                        secondImageCursor.goTo(j,i);
                    } catch (Exception e)
                    {
                        throw new IllegalStateException("Współrzędne (" + j + "," + i + ") spoza zakresu obrazka rightImage");
                    }
                    secondImageCursor.fillPixelHood(secondPixelHood, ImageCursor.COMPLETE_COPY);
                    int[] secondPixel = secondPixelHood.getPixel(0);

                    int[] pixel = new int[firstPixel.length];
                    for(int b=0; b<pixel.length; ++b) {
                        pixel[b] = firstPixel[b] + secondPixel[b];
                    }
                    outImage.setPixel(j,i,pixel);
                }
            }

            outImage.normalize(NORMALIZATION_MODE_PROPORTIONAL);

            return outImage;

        }

        int neighborhoodBorderSize = 0;
        switch(parameters.method) {
            case "Według sąsiedztwa":
                neighborhoodBorderSize = parameters.neighborhoodBorderSize;
        }

        //applying changes

        PixelHood<int[]> pixelHood = new PixelHood<>(neighborhoodBorderSize, neighborhoodBorderSize, new int[raster.getNumBands()]);
        ImageCursor imageCursor = new ImageCursor(inImage);

        do {
            imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_COPY);

            int[] pixel = pixelHood.getPixel(0,0);
            int[] newPixel = new int[pixel.length];

            for(int i = 0; i<pixel.length; ++i) {

                Integer[] leftLevelLimitsChannel = leftLevelLimits.get(i);
                Integer[] rightLevelLimitsChannel = rightLevelLimits.get(i);
                Integer[] newLevelsChannel = newLevels.get(i);

                switch(parameters.method) {
                    case "Średnich":
//                        if(pixel[i] == newLevelsChannel[pixel[i]]) { continue; };
                        newPixel[i] = newLevelsChannel[pixel[i]];
                        break;
                    case "Losowa":
//                        if(pixel[i] == newLevelsChannel[pixel[i]]) { continue; };
                        int r = ThreadLocalRandom.current().nextInt(0, newLevelsChannel[pixel[i]]+1);
                        newPixel[i] = leftLevelLimitsChannel[pixel[i]] + r;
                        break;
                    case "Według sąsiedztwa":

                        //get neighborhood average, excluding self

                        double neighborhoodLevel = 0.0d;
                        int neighborhoodCount = 0;

                        for(int x=-neighborhoodBorderSize; x<=neighborhoodBorderSize; ++x) {
                            for(int y=-neighborhoodBorderSize; y<=neighborhoodBorderSize; ++y) {
                                if(x==0 && y==0) continue;

                                int[] neighborPixel = pixelHood.getPixel(x,y);

                                neighborhoodLevel += neighborPixel[i];
                                ++neighborhoodCount;
                            }
                        }
                        if(neighborhoodCount>0) {
                            neighborhoodLevel = neighborhoodLevel / ( neighborhoodCount + 0.0d );
                        }

                        if(neighborhoodLevel>rightLevelLimitsChannel[pixel[i]]) {
                            newPixel[i] = rightLevelLimitsChannel[pixel[i]];
                        } else {
                            if (neighborhoodLevel < leftLevelLimitsChannel[pixel[i]]) {
                                newPixel[i] = leftLevelLimitsChannel[pixel[i]];
                            } else {
                                newPixel[i] = (int) Math.round(neighborhoodLevel);
                            }
                        }
                        break;
                }
            }

            outImage.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), newPixel);

//            outRaster.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), newPixel);

        } while (imageCursor.forward());

        outImage.normalize(NORMALIZATION_MODE_PROPORTIONAL);

        return outImage;
    }

    private void fillStatisticsTables(String method, SimpleHSVBufferedImage inImage, Histogram histogram, ArrayList<Integer[]> leftLevelLimits, ArrayList<Integer[]> rightLevelLimits, ArrayList<Integer[]> newLevels, int[] histogramAverages) {
        int width = inImage.getWidth();
        int height = inImage.getHeight();

        int bands = histogram.getData().size();

        ArrayList<Integer[]> srcHistogramData = histogram.getData();

//        int[] histogramAverages = new int[bands];
        for(int i=0; i<bands; ++i) {
            histogramAverages[i] = 0;
        }

        //calculating average histogram value per channel
        {
            for (int i = 0; i < bands; ++i) {
                int levels = srcHistogramData.get(i).length;
                Integer[] srcHistogramDataChannel = srcHistogramData.get(i);

                for (int level = 0; level < levels; ++level) {
                    histogramAverages[i] += level * srcHistogramDataChannel[level];
                }
                histogramAverages[i] =  (int) ( ( histogramAverages[i] + 0.0 ) / ( width * height ) );
            }
        }

        //calculating ranges for new levels: left, right

        for(int i=0; i<bands; ++i) {
            int levels = srcHistogramData.get(i).length;
            leftLevelLimits.add(new Integer[levels]);
            rightLevelLimits.add(new Integer[levels]);
            newLevels.add(new Integer[levels]);
        }

        for(int i=0; i<bands; ++i) {
            int levels = srcHistogramData.get(i).length;
            Integer[] srcHistogramDataChannel = srcHistogramData.get(i);

            int R = 0;
            int Hint = 0;

            for(int level=0; level<levels; ++level) {
                leftLevelLimits.get(i)[level] = R;
                Hint += srcHistogramDataChannel[level];

                while (Hint>histogramAverages[i]) {
                    Hint -= histogramAverages[i];
                        ++R;
                }
                rightLevelLimits.get(i)[level] = R;

                switch(method) {
                    case "Średnich":
                        newLevels.get(i)[level] = (int) Math.round( ( leftLevelLimits.get(i)[level] + rightLevelLimits.get(i)[level]) / 2.0d );
                        break;
                    case "Losowa":
                        newLevels.get(i)[level] = rightLevelLimits.get(i)[level] - leftLevelLimits.get(i)[level];
                        break;
                    default:
                        newLevels.get(i)[level] = 0;
                }

            }
        }
    }

    private class Parameters {
        String method = "Średnich";
        int neighborhoodBorderSize = 3;

        public Parameters() {}
    }

}