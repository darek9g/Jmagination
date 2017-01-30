package jmagination.operations;

import jmagination.ConstantsInitializers;
import jmagination.histogram.Histogram;
import jmagination.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import static jmagination.ConstantsInitializers.BR;
import static jmagination.operations.OperationConstants.*;
import static jmagination.operations.OperationDuplicate.duplicateImageFunction;

/**
 * Created by darek on 30.11.2016.
 */

public class OperationSharpening extends Operation {

    Parameters parameters;


    JComboBox<String> maskSelect;
    JComboBox<String> edgeNeighborModeSelect;
    JComboBox<String> normalizationSelect;
    JTableFilterMask jTableMask;
    ItemListener itemListener =  null;



    {
        label = "Wyostrzanie";
        header = "Wyostrzanie";
        description = "Wartość piksela jest zmieniana według" + BR + "maski gradientowej wzmacniającej" + BR + "zmiany poziomów wartości w sąsiedztwie";

        hsvModeAllowed = true;
        hsvSpecificModeAllowed = true;

        parameters = new Parameters();

        parameters.serializedMask = parameters.maskValues[0];
        parameters.edgeModeIndex = 0;
        parameters.normalizationModeIndex = 0;
    }

    public OperationSharpening() {
        super();

        categories.add("Sąsiedztwa");
        categories.add("Gradientowe");
        categories.add("Filtry górnoprzepustowe");

        itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateMask();
                }
            }
        };


        maskSelect = new JComboBox<>(parameters.maskStrings);
        maskSelect.setSelectedIndex(0);
        maskSelect.addItemListener(itemListener);

        edgeNeighborModeSelect = new JComboBox<>(parameters.edgeModeStrings);
        edgeNeighborModeSelect.setSelectedIndex(0);

        normalizationSelect = new JComboBox<>(parameters.normalizationModeStrings);
        normalizationSelect.setSelectedIndex(0);

        if(parameters.hsvChangeMatrix[0] == true) {
            jCheckBoxHue.setSelected(true);
        } else {
            jCheckBoxHue.setSelected(false);
        }

        if(parameters.hsvChangeMatrix[1] == true) {
            jCheckBoxSaturation.setSelected(true);
        } else {
            jCheckBoxSaturation.setSelected(false);
        }

        if(parameters.hsvChangeMatrix[2] == true) {
            jCheckBoxValue.setSelected(true);
        } else {
            jCheckBoxValue.setSelected(false);
        }

        jTableMask = new JTableFilterMask(380);
        jTableMask.allowNonZeroSum = true;


        updateMask();

    }

    protected void updateMask() {

        jTableMask.fillMask(parameters.maskValues[maskSelect.getSelectedIndex()].length, parameters.maskValues[maskSelect.getSelectedIndex()]);
        jTableMask.repaint();
    }

    @Override
    public SimpleHSVBufferedImage RunOperationFunction(SimpleHSVBufferedImage bufferedImage, Histogram histogram) {

        parameters.serializedMask = jTableMask.getMaskMatrix();
        parameters.edgeModeIndex = edgeNeighborModeSelect.getSelectedIndex();
        parameters.normalizationModeIndex = normalizationSelect.getSelectedIndex();

        if (jRadioButtonColorModeRGB.isSelected()) {
            parameters.colorMode = OP_MODE_RGB;
        } else {
            parameters.colorMode = OP_MODE_HSV;

            if(jCheckBoxHue.isSelected() == true) {
                parameters.hsvChangeMatrix[0] = true;
            } else {
                parameters.hsvChangeMatrix[0] = false;
            }

            if(jCheckBoxSaturation.isSelected() == true) {
                parameters.hsvChangeMatrix[1] = true;
            } else {
                parameters.hsvChangeMatrix[1] = false;
            }

            if(jCheckBoxValue.isSelected() == true) {
                parameters.hsvChangeMatrix[2] = true;
            } else {
                parameters.hsvChangeMatrix[2] = false;
            }
        }

        return maskFunctionInterface(bufferedImage, histogram);
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
        JTextArea jTextAreaDescription = new JTextArea(description);
        jTextAreaDescription.setEditable(false);
        panel.add(jTextAreaDescription, c);

        // parametryzacja

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 5;
        JLabel jLabelMaskSelect = new JLabel("Maska operacji:");
        panel.add(jLabelMaskSelect, c);

        c.gridx += c.gridwidth;
        c.gridy = 2;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(maskSelect, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 16;
        panel.add(jTableMask, c);

        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = GridBagConstraints.REMAINDER;
        JLabel jLabelEdgeNeighborModeSelect = new JLabel("Sąsiedztwo pikseli brzegowch:");
        panel.add(jLabelEdgeNeighborModeSelect, c);

        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(edgeNeighborModeSelect, c);

        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = GridBagConstraints.REMAINDER;
        JLabel jLabelNormalizationSelect = new JLabel("Metoda normalizacji:");
        panel.add(jLabelNormalizationSelect, c);

        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(normalizationSelect, c);

        // wiersz sterowania wykonaniem
        c.gridx = 0;
        c.gridy = 10;
        c.gridwidth = 4;
        panel.add(jLabelColorMode, c);

        c.gridx+= c.gridwidth;
        c.gridy = 10;
        c.gridwidth = 4;
        panel.add(jRadioButtonColorModeRGB, c);

        c.gridx+= c.gridwidth;
        c.gridy = 10;
        c.gridwidth = 4;
        panel.add(jRadioButtonColorModeHSV, c);

        c.gridx+= c.gridwidth;
        c.gridy = 10;
        c.gridwidth = 4;
        panel.add(jButtonApply, c);

        // ruchomy wiersz sterowania wykonaniem HSV
        c.gridx = 0;
        c.gridy = 11;
        c.gridwidth = 4;
        panel.add(jLabelHSVComponentsSelet, c);

        c.gridx += c.gridwidth;
        c.gridy = 11;
        c.gridwidth = 4;
        panel.add(jCheckBoxHue, c);

        c.gridx += c.gridwidth;
        c.gridy = 11;
        c.gridwidth = 4;
        panel.add(jCheckBoxSaturation, c);

        c.gridx += c.gridwidth;
        c.gridy = 11;
        c.gridwidth = 4;
        panel.add(jCheckBoxValue, c);


        configureColorModeControls();
    }

    @Override
    public Operation Clone() {
        return new OperationSharpening();
    }

    public SimpleHSVBufferedImage maskFunctionInterface(SimpleHSVBufferedImage inImage, Histogram histogram) {
        return maskFunction(inImage, parameters.serializedMask);
    }

    public SimpleHSVBufferedImage maskFunction(SimpleHSVBufferedImage inImage, int[][] serializedMask) {
        switch(parameters.colorMode) {
            case OP_MODE_RGB:
                return maskFunctionRGB(inImage);
            case OP_MODE_HSV:
                return maskFunctionHSV(inImage);
            default:
                throw new IllegalStateException("Nieobsłużona tryb koloru.");
        }
    }

    public SimpleHSVBufferedImage maskFunctionRGB(SimpleHSVBufferedImage inImage) {

        SimpleHSVBufferedImage outImage = duplicateImageFunction(inImage);

        int bands = outImage.getRaster().getNumBands();


        ImageCursor imageCursor = new ImageCursor(outImage);

        PixelMask<int[]> pixelMask = new PixelMask<>(parameters.serializedMask, new int[bands]);
        PixelHood<int[]> pixelHood = new PixelHood<>(pixelMask.getHorizontalBorderSize(), pixelMask.getVerticalBorderSize(), new int[bands]);

        do {
            if(parameters.edgeModeIndex == ImageCursor.COMPLETE_SKIP) {
                int x = imageCursor.getPosX();
                int y = imageCursor.getPosY();

                if(x==0 || x == outImage.getWidth() -1 || y == 0 || y == outImage.getHeight() - 1) {
                    copyRGBPixel(outImage, 0, 1, imageCursor.getPosX(), imageCursor.getPosY());
                    continue;
                }
            }
            imageCursor.fillPixelHood(pixelHood, 0, parameters.edgeModeIndex);

            int[] pixel = pixelHood.getPixel(0,0);


            for(int b = 0; b< bands; ++b) {
                    int newValue = 0;

                for (int i = -pixelHood.getVerticalBorderSize(); i <= pixelHood.getVerticalBorderSize(); i++) {
                    for (int j = -pixelHood.getHorizontalBorderSize(); j <= pixelHood.getHorizontalBorderSize(); j++) {
                            newValue += pixelMask.getPixel(j,i)[b] * pixelHood.getPixel(j,i)[b];
                        }
                    }

                    pixel[b] = newValue;
            }

            outImage.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), pixel);

        } while (imageCursor.forward());


        outImage.normalize(parameters.normalizationModeIndex);

        return outImage;
    }

    public SimpleHSVBufferedImage maskFunctionHSV(SimpleHSVBufferedImage inImage) {

        int width = inImage.getWidth();
        int height = inImage.getHeight();

        float hsvOutMatrix[][][] = new float[width][height][3];
        HSVImageCursor imageCursor = new HSVImageCursor(inImage.getHsv(), width, height);

        PixelMask<int[]> pixelMask = new PixelMask<>(parameters.serializedMask, new int[3]);
        PixelHood<float[]> pixelHood = new PixelHood<>(pixelMask.getHorizontalBorderSize(), pixelMask.getVerticalBorderSize(), new float[3]);

        do {

            imageCursor.fillPixelHood(pixelHood, parameters.edgeModeIndex);
            float[] pixel = pixelHood.getPixel(0,0);
            float[] newPixel = new float[3];
            if(parameters.edgeModeIndex == ImageCursor.COMPLETE_SKIP) {
                int x = imageCursor.getPosX();
                int y = imageCursor.getPosY();

                if(x==0 || x == inImage.getWidth() -1 || y == 0 || y == inImage.getHeight() - 1) {
                    hsvOutMatrix[imageCursor.getPosX()][imageCursor.getPosY()] = pixel;
                    continue;
                }
            }

            for(int b = 0; b<3; b++) {

                if(parameters.hsvChangeMatrix[b] == true) {

                    double newValue = 0.0d;
                    for (int i = -pixelHood.getVerticalBorderSize(); i <= pixelHood.getVerticalBorderSize(); i++) {
                        for (int j = -pixelHood.getHorizontalBorderSize(); j <= pixelHood.getHorizontalBorderSize(); j++) {
                            newValue += pixelMask.getPixel(j, i)[b] * pixelHood.getPixel(j, i)[b];
                        }
                    }

                    newPixel[b] = (float) newValue;
                } else {
                    newPixel[b] = pixel[b];
                }
            }

            hsvOutMatrix[imageCursor.getPosX()][imageCursor.getPosY()] = newPixel;

        } while (imageCursor.forward());

        return new SimpleHSVBufferedImage(width, height, inImage.getType(), hsvOutMatrix, parameters.normalizationModeIndex, parameters.hsvChangeMatrix);
    }

    protected static class Parameters {



        public  String[] maskStrings = MASKS_NAMES_SHARPENING;
        public  int[][][] maskValues = MASKS_SHARPENING;

        public static final String[] edgeModeStrings = ImageCursor.edgeModeStrings;

        public static final String[] normalizationModeStrings = SimpleHSVBufferedImage.normalizationModeStrings;

        int[][] serializedMask;
        int edgeModeIndex;
        int normalizationModeIndex;

        int colorMode;
        boolean[] hsvChangeMatrix;

        public Parameters() {
            serializedMask = maskValues[0];
            edgeModeIndex = 0;
            normalizationModeIndex = 0;

            colorMode = OP_MODE_RGB;
            hsvChangeMatrix = new boolean[]{false,true,true};
        }

    }
}
