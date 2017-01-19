package jmagination.operations;

import jmagination.ConstantsInitializers;
import jmagination.histogram.Histogram;
import util.ImageCursor;
import util.PixelHood;
import util.PixelMask;
import util.SimpleHSVBufferedImage;

import javax.swing.*;
import java.awt.*;

import static jmagination.ConstantsInitializers.BR;
import static jmagination.operations.OperationDuplicate.duplicateImageFunction;

/**
 * Created by darek on 30.11.2016.
 */

public class OperationSharpening extends Operation {

    Parameters parameters;


    JComboBox<String> maskSelect;
    JComboBox<String> edgeNeighborModeSelect;
    JComboBox<String> normalizationSelect;



    {
        label = "Wyostrzanie";
        header = "Wyostrzanie";
        description = "Foo" + BR + "bar.";

        parameters = new Parameters();

        parameters.serializedMask = parameters.maskValues[0];
        parameters.edgeModeIndex = 0;
        parameters.normalizationModeIndex = 0;
    }

    public OperationSharpening() {
        super();

        categories.add("LAB 4");
        categories.add("Sąsiedztwa");

        maskSelect = new JComboBox<>(parameters.maskStrings);
        maskSelect.setSelectedIndex(0);

        edgeNeighborModeSelect = new JComboBox<>(parameters.edgeModeStrings);
        edgeNeighborModeSelect.setSelectedIndex(0);

        normalizationSelect = new JComboBox<>(parameters.normalizationModeStrings);
        normalizationSelect.setSelectedIndex(0);
    }

    @Override
    public SimpleHSVBufferedImage RunOperationFunction(SimpleHSVBufferedImage bufferedImage, Histogram histogram) {

        parameters.serializedMask = parameters.maskValues[maskSelect.getSelectedIndex()];
        parameters.edgeModeIndex = edgeNeighborModeSelect.getSelectedIndex();
        parameters.normalizationModeIndex = normalizationSelect.getSelectedIndex();


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
        c.gridwidth = GridBagConstraints.REMAINDER;
        JLabel jLabelMaskSelect = new JLabel("Maska operacji:");
        panel.add(jLabelMaskSelect, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(maskSelect, c);

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
        c.gridy = 9;
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


        configureColorModeControls();
    }

    @Override
    public Operation Clone() {
        return new OperationSharpening();
    }

    public SimpleHSVBufferedImage maskFunctionInterface(SimpleHSVBufferedImage inImage, Histogram histogram) {
        return maskFunction(inImage, parameters.serializedMask);
    }

    public SimpleHSVBufferedImage maskFunction(SimpleHSVBufferedImage inImage, int[] serializedMask) {

        SimpleHSVBufferedImage outImage = duplicateImageFunction(inImage);

        int bands = outImage.getRaster().getNumBands();

        PixelMask<int[]> pixelMask = new PixelMask<>(serializedMask, new int[bands]);

            ImageCursor imageCursor = new ImageCursor(outImage);

            PixelHood<int[]> pixelHood = new PixelHood<>(1, 1, new int[bands]);

            do {
                imageCursor.fillPixelHood(pixelHood, 0, parameters.edgeModeIndex);

                int[] pixel = pixelHood.getPixel(0,0);

                double newValue = 0;

                for(int b = 0; b< bands; ++b) {
                        for(int i=-1; i<2; i++) {
                            for(int j=-1; j<2; j++) {
                                newValue += pixelMask.getPixel(j,i)[b] * pixelHood.getPixel(j,i)[b];
                            }
                        }

                        pixel[b] = (int) Math.round(newValue / pixelHood.getDataSize());
                }

                outImage.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), pixel);

            } while (imageCursor.forward());


        outImage.normalize(parameters.normalizationModeIndex);

        return outImage;
    }

    protected static class Parameters {

        public static final int[] gradientXSerializedValues = {-1, -2, -1, 0, 0, 0, 1, 2, 1};
        public static final int[] gradientYSerializedValues = {-1, 0, 1, -2, 0, 2, -1, 0, 1};
        public static final int[] laplaceSerializedValues = {0, 1, 0, 1, -4, 1, 0, 1, 0};

        public static final String[] maskStrings = {"Gradient poziomy", "Gradient pionowy", "Laplasjan"};
        public static final int[][] maskValues = {gradientXSerializedValues, gradientYSerializedValues, laplaceSerializedValues};

        public static final String[] edgeModeStrings = {"Wartości minimalne", "Wartości maksymalne", "Powtórzenie piksela z obrazu", "Pominięcie brzegu"};

        public static final String[] normalizationModeStrings = SimpleHSVBufferedImage.normalizationModeStrings;

        int[] serializedMask;
        int edgeModeIndex;
        int normalizationModeIndex;

        public Parameters() {
            serializedMask = maskValues[0];
            edgeModeIndex = 0;
            normalizationModeIndex = 0;
        }

    }
}