package jmagination.operations;

import jmagination.ConstantsInitializers;
import jmagination.ImageServer;
import jmagination.guitools.LineEditor;
import jmagination.histogram.Histogram;
import slider.RangeSlider;
import util.ImageCursor;
import util.PixelHood;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;


/**
 * Created by darek on 30.11.2016.
 */

public class OperationLUTPointOperation extends Operation {

    Parameters parameters;

    LineEditor thresholdLineEditor;

    {
        label = "Uniwersalna operacja punktowa";
        header = "Uniwersalna operacja punktowa - UOP";
        description = "Skalowanie wartości z według lini skalowania.";

        parameters = new Parameters(256);

        thresholdLineEditor = new LineEditor(LineEditor.FREE_MODE, 0, 255, 0, 255);
        thresholdLineEditor.addActionListener(runOperationTrigger);

    }


    public OperationLUTPointOperation() {
        super();
        categories.add("LAB 2");
        categories.add("Punktowe jednoargumentowe");

    }

    @Override
    public BufferedImage RunOperationFunction(BufferedImage bufferedImage, Histogram histogram) {


        parameters.operationMap = thresholdLineEditor.getOutputMap();

/*        System.out.printf("Mapa: \n");
        for(int i=0;i<parameters.operationMap.length; ++i) {
            System.out.printf("%d ",parameters.operationMap[i]);
        }
        System.out.printf("\n");*/

        return remapPixelsFunction(bufferedImage);
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
        JLabel title = new JLabel(header);
        panel.add(title, c);

        // opis
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 16;
        JTextArea jTextAreadescription = new JTextArea(description);
        jTextAreadescription.setEditable(false);
        panel.add(jTextAreadescription, c);

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
        return new OperationLUTPointOperation();
    }

    public BufferedImage remapPixelsFunction(BufferedImage inImage) {
        int width = inImage.getWidth();
        int height = inImage.getHeight();
        BufferedImage outImage = new BufferedImage(width, height, inImage.getType());
        WritableRaster raster = inImage.getRaster();
        WritableRaster outRaster = outImage.getRaster();

        PixelHood<int[]> pixelHood = new PixelHood<>(0,0, new int[raster.getNumBands()]);
        ImageCursor imageCursor = new ImageCursor(inImage);

        do {
            imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_MIN);

            int[] pixel = remapPixel(parameters.operationMap, pixelHood.getPixel(0,0));
            outRaster.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), pixel);

        } while (imageCursor.forward());

        return outImage;
    }

    public static int[] remapPixel(int[] map, int... pixel){
        int[] newPixel = new int[pixel.length];
        for (int i = 0; i < pixel.length; i++) {
            newPixel[i] = map[pixel[i]];
        }
        return newPixel;
    }

    protected class Parameters {

        int[] operationMap;

        public Parameters(int operationMapLength) {
            operationMap = new int[operationMapLength];
        }
    }
}
