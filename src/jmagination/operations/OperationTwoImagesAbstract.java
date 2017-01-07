package jmagination.operations;

import jmagination.ConstantsInitializers;
import jmagination.ImageServer;
import jmagination.gui.ImagesComboBox;
import jmagination.histogram.Histogram;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by darek on 06.01.2017.
 */
public abstract class OperationTwoImagesAbstract extends Operation{

    Parameters parameters;

    ImagesComboBox imagesComboBox;

    ButtonGroup buttonGroupOperationMode;
    JRadioButton jRadioButtonCutToLeft;
    JRadioButton jRadioButtonCutToRight;
    JRadioButton jRadioButtonCutToMinimum;
    JRadioButton jRadioButtonCutToMaximum;





    {
        label = "Dummy - dwuobrazowa";
        header = "Dummy - oparacja arytmetyczna lub logiczna";
        description = "Dummy - wyznaczanie wartości według odpowiadających\n" +
                "sobie pikesli dwóch obrazów.";

        parameters = new Parameters();

    }

    public OperationTwoImagesAbstract() {
        super();

        categories.add("LAB 2");

        buttonGroupOperationMode = new ButtonGroup();

        jRadioButtonCutToLeft = new JRadioButton("Do wielkości głównego obrazu (lewy operand)");
        jRadioButtonCutToRight = new JRadioButton("Do wielkości wybranego obrazu (prawy operand)");
        jRadioButtonCutToMinimum = new JRadioButton("Do minimalnej szerokości i wysokości");
        jRadioButtonCutToMaximum = new JRadioButton("Do maksymalnej szerokości i wysokości");

        buttonGroupOperationMode.add(jRadioButtonCutToLeft);
        buttonGroupOperationMode.add(jRadioButtonCutToRight);
        buttonGroupOperationMode.add(jRadioButtonCutToMinimum);
        buttonGroupOperationMode.add(jRadioButtonCutToMaximum);

        jRadioButtonCutToLeft.setSelected(true);
    }

    @Override
    public BufferedImage RunOperationFunction(BufferedImage bufferedImage, Histogram histogram) {
        return twoImagesFunction(bufferedImage);
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

        // etykieta
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(new JLabel("Wybierz prawy operand"), c);

        // wybór obrazu
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = GridBagConstraints.REMAINDER;
        imagesComboBox = new ImagesComboBox(runOperation.supplyAvailableImages(), ConstantsInitializers.GUI_LARGE_IMAGEICON_SIZE.width, ConstantsInitializers.GUI_LARGE_IMAGEICON_SIZE.height);
        panel.add(imagesComboBox, c);

        // wybór sposobu wyznaczenia rozmiaru wynikowego obrazu
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(jRadioButtonCutToLeft, c);

        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(jRadioButtonCutToRight, c);

        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(jRadioButtonCutToMinimum, c);

        c.gridx = 0;
        c.gridy = 9;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(jRadioButtonCutToMaximum, c);


        // wiersz sterowania wykonaniem
        c.gridx = 0;
        c.gridy = 12;
        c.gridwidth = 4;
        panel.add(jLabelColorMode, c);

        c.gridx+= c.gridwidth;
        c.gridy = 12;
        c.gridwidth = 4;
        panel.add(jRadioButtonColorModeRGB, c);

        c.gridx+= c.gridwidth;
        c.gridy = 12;
        c.gridwidth = 4;
        panel.add(jRadioButtonColorModeHSV, c);

        c.gridx+= c.gridwidth;
        c.gridy = 12;
        c.gridwidth = 4;
        panel.add(jButtonApply, c);


        configureColorModeControls();
    }

    public abstract BufferedImage twoImagesFunction(BufferedImage srcImage);

    protected class Parameters {

        ArrayList<BufferedImage> operands;

        public Parameters() {}
    }

}
