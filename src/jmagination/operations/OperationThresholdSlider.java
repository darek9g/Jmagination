package jmagination.operations;

/**
 * Created by darek on 03.01.2017.
 */

import jmagination.ConstantsInitializers;
import jmagination.ImageServer;
import jmagination.histogram.Histogram;
import slider.RangeSlider;
import util.ImageCursor;
import util.PixelHood;
import util.SimpleHSVBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.WritableRaster;


/**
 * Created by darek on 30.11.2016.
 */

@Deprecated
public class OperationThresholdSlider extends Operation {

    Parameters parameters;
    JLabel thresholdRangeLowJLabel;
    JLabel thresholdRangeHighJLabel;
    RangeSlider thresholdJRangeSlider;

    ButtonGroup buttonGroupOperationMode;
    JRadioButton jRadioButtonOperationModeBinary;
    JRadioButton jRadioButtonOperationModeWithValues;


    public OperationThresholdSlider(ImageServer srcImageServer) {
        super();
        this.label = "Proguj piksele - slider";
        categories.add("LAB 2");
        categories.add("Punktowe jednoargumentowe");

        parameters = new Parameters();

        thresholdRangeLowJLabel = new JLabel(String.valueOf(parameters.thresholdRangeLow));
        thresholdRangeLowJLabel.setPreferredSize(new Dimension(25,10));
        thresholdRangeLowJLabel.setMinimumSize(new Dimension(25,10));
        thresholdRangeLowJLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        thresholdRangeHighJLabel = new JLabel(String.valueOf(parameters.thresholdRangeHigh));
        thresholdRangeHighJLabel.setPreferredSize(new Dimension(25,10));
        thresholdRangeHighJLabel.setMinimumSize(new Dimension(25,10));
        thresholdRangeLowJLabel.setHorizontalAlignment(SwingConstants.LEFT);
        thresholdJRangeSlider = new RangeSlider();
        thresholdJRangeSlider.setMinimum(0);
        thresholdJRangeSlider.setMaximum(255);
        thresholdJRangeSlider.setValue(0);
        thresholdJRangeSlider.setUpperValue(255);
        thresholdJRangeSlider.setMinimumSize(new Dimension(256,15));

        buttonGroupOperationMode = new ButtonGroup();

        jRadioButtonOperationModeWithValues = new JRadioButton("Zachowanie wartości");
        jRadioButtonOperationModeWithValues.setSelected(true);
        jRadioButtonOperationModeBinary = new JRadioButton("Binaryzacja");

        buttonGroupOperationMode.add(jRadioButtonOperationModeWithValues);
        buttonGroupOperationMode.add(jRadioButtonOperationModeBinary);

    }

    @Override
    public SimpleHSVBufferedImage RunOperationFunction(SimpleHSVBufferedImage bufferedImage, Histogram histogram) {
        if(jRadioButtonOperationModeWithValues.isSelected() == true) {
            parameters.mode = 0;
        }
        if(jRadioButtonOperationModeBinary.isSelected() == true) {
            parameters.mode = 1;
        }
        parameters.thresholdRangeLow = thresholdJRangeSlider.getValue();
        parameters.thresholdRangeHigh = thresholdJRangeSlider.getUpperValue();
        thresholdRangeLowJLabel.setText(String.valueOf(parameters.thresholdRangeLow));
        thresholdRangeHighJLabel.setText(String.valueOf(parameters.thresholdRangeHigh));
        return thresholdPixelsFunction(bufferedImage);
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
        JLabel title = new JLabel("Progowanie");
        panel.add(title, c);

        // opis
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 16;
        JTextArea description = new JTextArea("Ustawienie minimalnej jasności pikselom spoza określonego \nzakresu jasności");
        description.setEditable(false);
        panel.add(description, c);

        // wiersz wyboru trybu
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 8;
        panel.add(jRadioButtonOperationModeWithValues, c);

        c.gridx+= c.gridwidth;
        c.gridy = 2;
        c.gridwidth = 8;
        panel.add(jRadioButtonOperationModeBinary, c);

/*        c.gridx+= c.gridwidth;
        c.gridy = 2;
        c.gridwidth = 2;
        panel.add(thresholdRangeHighJLabel, c);*/


        // wiersz suwaka
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        panel.add(thresholdRangeLowJLabel, c);

        c.gridx+= c.gridwidth;
        c.gridy = 3;
        c.gridwidth = 12;

        thresholdJRangeSlider.addChangeListener(runOperationChangeTrigger);
        panel.add(thresholdJRangeSlider, c);

        c.gridx+= c.gridwidth;
        c.gridy = 3;
        c.gridwidth = 2;
        panel.add(thresholdRangeHighJLabel, c);

        // wiersz sterowania wykonaniem
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 4;
        panel.add(jLabelColorMode, c);

        c.gridx+= c.gridwidth;
        c.gridy = 5;
        c.gridwidth = 4;
        panel.add(jRadioButtonColorModeRGB, c);

        c.gridx+= c.gridwidth;
        c.gridy = 5;
        c.gridwidth = 4;
        panel.add(jRadioButtonColorModeHSV, c);

        c.gridx+= c.gridwidth;
        c.gridy = 5;
        c.gridwidth = 4;
        panel.add(jButtonApply, c);


        configureColorModeControls();
    }

    @Override
    public Operation Clone() {
        return new OperationThresholdSlider(null);
    }

    public SimpleHSVBufferedImage thresholdPixelsFunction(SimpleHSVBufferedImage inImage) {
        int width = inImage.getWidth();
        int height = inImage.getHeight();
        SimpleHSVBufferedImage outImage = new SimpleHSVBufferedImage(width, height, inImage.getType());
        WritableRaster raster = inImage.getRaster();
        WritableRaster outRaster = outImage.getRaster();

        PixelHood<int[]> pixelHood = new PixelHood<>(0,0, new int[raster.getNumBands()]);
        ImageCursor imageCursor = new ImageCursor(inImage);

        do {
            imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_MIN);
            int[] pixel = thresholdPixel(parameters.mode, parameters.thresholdRangeLow, parameters.thresholdRangeHigh, pixelHood.getPixel(0,0));
            outRaster.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), pixel);

        } while (imageCursor.forward());

        return outImage;
    }

    public static int[] thresholdPixel(int mode, int lowValue, int highValue, int... pixel){
        int[] newPixel = new int[pixel.length];
        for (int i = 0; i < pixel.length; i++) {

            if(pixel[i]<=lowValue) {
                newPixel[i] = 0;
            } else {
                if (pixel[i]>highValue) { newPixel[i] = 0; } else {
                    switch(mode) {
                        case 0:
                            newPixel[i] = pixel[i];
                            break;
                        case 1:
                            newPixel[i] = 255;
                            break;
                    }
                }
            }
        }
        return newPixel;
    }

    private class Parameters {
        int thresholdRangeLow = 0;
        int thresholdRangeHigh = 255;

        int mode = 0;

        public Parameters() {}
    }
}
