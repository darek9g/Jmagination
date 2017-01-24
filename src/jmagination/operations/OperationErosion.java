package jmagination.operations;

import jmagination.ConstantsInitializers;
import jmagination.histogram.Histogram;
import util.ImageCursor;
import util.PixelHood;
import util.SimpleHSVBufferedImage;

import javax.swing.*;
import java.awt.*;

import static jmagination.ConstantsInitializers.BR;
import static jmagination.operations.OperationDuplicate.duplicateImageFunction;

/**
 * Created by darek on 30.11.2016.
 */

public class OperationErosion extends Operation {

    Parameters parameters;


    JComboBox<String> methodSelect;

    ButtonGroup buttonGroupObjectChoice;
    JRadioButton jRadioButtonPixel4Connected;
    JRadioButton jRadioButtonPixel8Connected;

    {
        label = "Erozja";
        header = "Erozja";
        description = "Foo" + BR + "bar.";

        parameters = new Parameters();
        parameters.promotingBrighter = false;

    }

    public OperationErosion() {
        super();

        categories.add("LAB 4");
        categories.add("Morfologiczne");

        methodSelect = new JComboBox<>(parameters.edgeModes);

        buttonGroupObjectChoice = new ButtonGroup();
        jRadioButtonPixel4Connected = new JRadioButton("4 spójne");
        buttonGroupObjectChoice.add(jRadioButtonPixel4Connected);
        jRadioButtonPixel8Connected = new JRadioButton("8 spójne");
        buttonGroupObjectChoice.add(jRadioButtonPixel8Connected);

        jRadioButtonPixel4Connected.setSelected(true);

        methodSelect.setSelectedIndex(0);

    }

    @Override
    public SimpleHSVBufferedImage RunOperationFunction(SimpleHSVBufferedImage bufferedImage, Histogram histogram) {

        parameters.edgeNeighborMode = methodSelect.getSelectedIndex();

        if(jRadioButtonPixel4Connected.isSelected() == true) {
            parameters.setPixelConnections(true);
        } else {
            parameters.setPixelConnections(false);
        }

        return morphFunctionInterface(bufferedImage, histogram);
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
        c.gridy = 3;
        c.gridwidth = GridBagConstraints.REMAINDER;
        JLabel jLabelObjectSelect = new JLabel("Model sąsiedztwa:");
        panel.add(jLabelObjectSelect, c);

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(jRadioButtonPixel4Connected, c);

        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(jRadioButtonPixel8Connected, c);

        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = GridBagConstraints.REMAINDER;
        JLabel jLabelMethodSelect = new JLabel("Sąsiedztwo pikseli brzegowch:");
        panel.add(jLabelMethodSelect, c);

        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(methodSelect, c);

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
        return new OperationErosion();
    }

    public SimpleHSVBufferedImage morphFunctionInterface(SimpleHSVBufferedImage inImage, Histogram histogram) {
        return morphFunction(inImage, parameters.promotingBrighter);
    }

    public SimpleHSVBufferedImage morphFunction(SimpleHSVBufferedImage inImage, boolean promotingBrighter) {

        SimpleHSVBufferedImage outImage = duplicateImageFunction(inImage);

        for(int b = 0; b<outImage.getRaster().getNumBands(); ++b) {

            ImageCursor imageCursor = new ImageCursor(outImage);
            PixelHood<int[]> pixelHood = new PixelHood<>(1, 1, new int[outImage.getRaster().getNumBands()]);

            do {
                imageCursor.fillPixelHood(pixelHood, 0, parameters.edgeNeighborMode);

                int[] pixel = pixelHood.getPixel(0,0);

                int newValue = pixel[b];

                for(int i=-1; i<2; i++) {
                    for(int j=0; j<2; j++) {

                        if(parameters.pixelNeighborHoodDefinition4 == true && i!=j && i*j!=0) { continue; }
                        if ( promotingBrighter == true ) {
                            if (pixelHood.getPixel(j,i)[b] > newValue) { newValue = pixelHood.getPixel(j,i)[b]; }
                        } else {
                            if (pixelHood.getPixel(j,i)[b] < newValue) { newValue = pixelHood.getPixel(j,i)[b]; }
                        }
                    }
                }

                pixel[b] = newValue;

                outImage.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), pixel);

            } while (imageCursor.forward());
        }


        outImage.normalize();

        return outImage;
    }

    protected class Parameters {

        public static final int COMPLETE_MIN = 0;
        public static final int COMPLETE_MAX = 1;
        public static final int COMPLETE_COPY = 2;
        public static final int COMPLETE_SKIP = 3;

        public String[] edgeModes = {"Wartości minimalne", "Wartości maksymalne", "Powtórzenie piksela z obrazu", "Pominięcie brzegu"};
        int edgeNeighborMode = 0;

        public boolean pixelNeighborHoodDefinition4 = true;

        public boolean promotingBrighter = true;

        public Parameters() {}

        public void setPixelConnections(boolean is4Connected) {
            if(is4Connected==true) {
                pixelNeighborHoodDefinition4 = true;
            } else {
                pixelNeighborHoodDefinition4 = false;
            }
        }
    }
}