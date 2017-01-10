package jmagination.operations;

import jmagination.ConstantsInitializers;
import jmagination.histogram.Histogram;
import util.SimpleHSVBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

/**
 * Created by darek on 30.11.2016.
 */
public class OperationDuplicate extends Operation {

    Parameters parameters;

    public OperationDuplicate() {
        super();
        this.label = "Zduplikuj obraz";
        categories.add("LAB 1");
        categories.add("Ogólne");

        parameters = new Parameters();
    }

    @Override
    public SimpleHSVBufferedImage RunOperationFunction(SimpleHSVBufferedImage bufferedImage, Histogram histogram) {
        return duplicateImageFunction(bufferedImage);
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
        JLabel title = new JLabel("Duplikowanie obrazu");
        panel.add(title, c);

        // opis
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 16;
        JTextArea description = new JTextArea("Kopiuje obraz do nowego bufora");
        description.setEditable(false);
        panel.add(description, c);

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
        return new OperationDuplicate();
    }

    public static SimpleHSVBufferedImage duplicateImageFunction(SimpleHSVBufferedImage srcImage) {
        ColorModel cm = srcImage.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = srcImage.copyData(null);
        return new SimpleHSVBufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private class Parameters {

        public Parameters() {}
    }

}
