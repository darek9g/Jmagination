package jmagination.operations;

import jmagination.ConstantsInitializers;
import jmagination.ImageServer;
import jmagination.guitools.LineEditor;
import jmagination.histogram.Histogram;
import slider.RangeSlider;
import util.ImageCursor;
import util.PixelHood;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;


/**
 * Created by darek on 30.11.2016.
 */

public class OperationThreshold extends Operation {

    Parameters parameters;

    LineEditor thresholdLineEditor;

    ButtonGroup buttonGroupOperationMode;
    JRadioButton jRadioButtonOperationModeBinary;
    JRadioButton jRadioButtonOperationModeWithValues;


    public OperationThreshold(ImageServer srcImageServer) {
        super();
        this.label = "Proguj piksele";
        categories.add("LAB 2");
        categories.add("Punktowe jednoargumentowe");

        parameters = new Parameters();

        thresholdLineEditor = new LineEditor(LineEditor.MIN_MAX_MODE, 0, 255, 0, 255);
        thresholdLineEditor.addActionListener(runOperationTrigger);

        buttonGroupOperationMode = new ButtonGroup();

        jRadioButtonOperationModeWithValues = new JRadioButton("Zachowanie wartości");
        jRadioButtonOperationModeWithValues.setSelected(true);
        jRadioButtonOperationModeBinary = new JRadioButton("Binaryzacja");

        buttonGroupOperationMode.add(jRadioButtonOperationModeWithValues);
        buttonGroupOperationMode.add(jRadioButtonOperationModeBinary);

    }

    @Override
    public BufferedImage RunOperationFunction(BufferedImage bufferedImage, Histogram histogram) {
        if(jRadioButtonOperationModeWithValues.isSelected() == true) {
            parameters.mode = 0;
        }
        if(jRadioButtonOperationModeBinary.isSelected() == true) {
            parameters.mode = 1;
        }

        parameters.operationMap = thresholdLineEditor.getOutputPoints();

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

        // wiersz edytora linii
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(thresholdLineEditor, c);

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
        return new OperationThreshold(null);
    }

    public BufferedImage thresholdPixelsFunction(BufferedImage inImage) {
        int width = inImage.getWidth();
        int height = inImage.getHeight();
        BufferedImage outImage = new BufferedImage(width, height, inImage.getType());
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

        ArrayList<Point> operationMap;

        int mode = 0;

        {
            operationMap = new ArrayList<>();
        }

        public Parameters() {}
    }
}
