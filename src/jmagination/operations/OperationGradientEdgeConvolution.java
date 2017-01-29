package jmagination.operations;

import jmagination.ConstantsInitializers;
import jmagination.histogram.Histogram;
import util.*;

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

public class OperationGradientEdgeConvolution extends Operation {

    Parameters parameters;


    JComboBox<String> maskSelect;
    JComboBox<String> directionSelect;
    JComboBox<String> edgeNeighborModeSelect;
    JComboBox<String> normalizationSelect;
    JTableFilterMask jTableMask;
    ItemListener itemListener =  null;


    {
        label = "Dopasowanie maski krawędzi";
        header = "Konwolucyjne dopasowanie maski krawędzi";
        description = "Wartość piksela jest zmieniana według" + BR + "dopasowania sąsiedztwa piksela do maski specjalnej" + BR + "odwzorowującej krawędź o wybranej orientacji";

        hsvModeAllowed = true;
        hsvSpecificModeAllowed = true;

        parameters = new Parameters();
    }

    public OperationGradientEdgeConvolution() {
        super();

        categories.add("Sąsiedztwa");
        categories.add("Gradientowe");
        categories.add("Detekcja krawędzi");
        categories.add("Filtry górnoprzepustowe");

        itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateMask();
                }
            }
        };

        maskSelect = new JComboBox<>(parameters.maskClassStrings);
        maskSelect.setSelectedIndex(parameters.maskClassIndex);
        maskSelect.addItemListener(itemListener);

        directionSelect = new JComboBox<>(parameters.maskSubClassStrings);
        directionSelect.setSelectedIndex(parameters.maskSubClassIndex);
        directionSelect.addItemListener(itemListener);

        edgeNeighborModeSelect = new JComboBox<>(parameters.edgeModeStrings);
        edgeNeighborModeSelect.setSelectedIndex(parameters.edgeModeIndex);

        normalizationSelect = new JComboBox<>(parameters.normalizationModeStrings);
        normalizationSelect.setSelectedIndex(parameters.normalizationModeIndex);

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

        jTableMask = new JTableFilterMask(80);
        jTableMask.allowNonZeroSum = true;
        updateMask();
    }

    private void updateMask() {

        parameters.maskClassIndex = maskSelect.getSelectedIndex();
        parameters.maskSubClassIndex = directionSelect.getSelectedIndex();

        jTableMask.fillMask(parameters.maskValues[parameters.maskClassIndex][parameters.maskSubClassIndex].length,
                parameters.maskValues[parameters.maskClassIndex][parameters.maskSubClassIndex]);
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
        c.gridwidth = 4;
        JLabel jLabelMaskSelect = new JLabel("Wybór metody operatora:");
        panel.add(jLabelMaskSelect, c);

        c.gridx += c.gridwidth;
        c.gridy = 2;
        c.gridwidth = 4;
        panel.add(maskSelect, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        JLabel jLabelDirectionSelect = new JLabel("Kierunek maski:");
        panel.add(jLabelDirectionSelect, c);

        c.gridx += c.gridwidth;
        c.gridy = 3;
        c.gridwidth = 2;
        panel.add(directionSelect, c);

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        JLabel jLabelMaskaFiltru = new JLabel("Maska filtru:");
        panel.add(jLabelMaskaFiltru, c);

        c.gridx += c.gridwidth;
        c.gridy = 4;
        c.gridwidth = 14;
        panel.add(jTableMask, c);

        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 8;
        JLabel jLabelEdgeNeighborModeSelect = new JLabel("Sąsiedztwo pikseli brzegowch:");
        panel.add(jLabelEdgeNeighborModeSelect, c);

        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(edgeNeighborModeSelect, c);

        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = GridBagConstraints.REMAINDER;
        JLabel jLabelNormalizationSelect = new JLabel("Metoda normalizacji:");
        panel.add(jLabelNormalizationSelect, c);

        c.gridx = 0;
        c.gridy = 9;
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
        return new OperationGradientEdgeConvolution();
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

        PixelMask<int[]> pixelMask = new PixelMask<>(parameters.serializedMask, new int[bands]);

            ImageCursor imageCursor = new ImageCursor(outImage);

            PixelHood<int[]> pixelHood = new PixelHood<>(1, 1, new int[bands]);

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

                        for(int i=-1; i<2; i++) {
                            for(int j=-1; j<2; j++) {
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
        PixelHood<float[]> pixelHood = new PixelHood<>(1, 1, new float[3]);
        HSVImageCursor imageCursor = new HSVImageCursor(inImage.getHsv(), width, height);

        PixelMask<int[]> pixelMask = new PixelMask<>(parameters.serializedMask, new int[3]);

        do {
            imageCursor.fillPixelHood(pixelHood, parameters.edgeModeIndex);
            float[] pixel = pixelHood.getPixel(0,0);
            float[] newPixel = new float[3];
            if(parameters.edgeModeIndex == ImageCursor.COMPLETE_SKIP) {
                int x = imageCursor.getPosX();
                int y = imageCursor.getPosY();

                if(x==0 || x == inImage.getWidth() -1 || y == 0 || y == inImage.getHeight() -1) {
                    hsvOutMatrix[imageCursor.getPosX()][imageCursor.getPosY()] = pixel;
                    continue;
                }
            }

//            System.out.printf("Pixel z %f %f %f\n", pixel[0], pixel[1], pixel[2]);

            for(int b = 0; b<3; b++) {

                if(parameters.hsvChangeMatrix[b] == true) {

                    double newValue = 0.0d;
                    for (int i = -1; i < 2; i++) {
                        for (int j = -1; j < 2; j++) {
                            newValue += pixelMask.getPixel(j, i)[b] * pixelHood.getPixel(j, i)[b];
                        }
                    }

                    newPixel[b] = (float) (newValue);
                } else {
                    newPixel[b] = pixel[b];
                }
            }

//            System.out.printf("NewPixel z %f %f %f\n", newPixel[0], newPixel[1], newPixel[2]);
            hsvOutMatrix[imageCursor.getPosX()][imageCursor.getPosY()] = newPixel;

        } while (imageCursor.forward());

        return new SimpleHSVBufferedImage(width, height, inImage.getType(), hsvOutMatrix, parameters.normalizationModeIndex, parameters.hsvChangeMatrix);
    }

    protected static class Parameters {



        public static final String[] maskClassStrings = {"Prewitt", "Kirch"};
        public static final String[] maskSubClassStrings = MASK_GEO_DIRECTIONS;


        public static final int[][][][] maskValues = {MASKS_PREWITT, MASKS_KIRCH};

        public static final int COMPLETE_MIN = 0;
        public static final int COMPLETE_MAX = 1;
        public static final int COMPLETE_COPY = 2;
        public static final int COMPLETE_SKIP = 3;

        public static final String[] edgeModeStrings = {"Wartości minimalne", "Wartości maksymalne", "Powtórzenie piksela z obrazu", "Pominięcie brzegu"};

        public static final String[] normalizationModeStrings = SimpleHSVBufferedImage.normalizationModeStrings;

        int maskClassIndex;
        int maskSubClassIndex;

        int[][] serializedMask;
        int edgeModeIndex;
        int normalizationModeIndex;

        int colorMode;
        boolean[] hsvChangeMatrix;

        public Parameters() {
            maskClassIndex = 0;
            maskSubClassIndex = 0;
            serializedMask = maskValues[maskClassIndex][maskSubClassIndex];
            edgeModeIndex = 0;
            normalizationModeIndex = 0;

            colorMode = OP_MODE_RGB;
            hsvChangeMatrix = new boolean[]{false,true,true};
        }

        protected void setMask() {
            serializedMask = maskValues[maskClassIndex][maskSubClassIndex];
        }

    }
}