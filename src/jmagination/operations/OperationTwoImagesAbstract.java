package jmagination.operations;

import jmagination.ConstantsInitializers;
import jmagination.ImageServer;
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
    {
        label = "Dummy - dwuobrazowa";
        header = "Dummy - oparacja arytmetyczna lub logiczna";
        description = "Dummy - wyznaczanie wartości według na odpowiadających sobie pikesli z dwóch obrazów.";

        parameters = new Parameters();

    }

    public OperationTwoImagesAbstract() {
        super();

        categories.add("LAB 2");

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

        // wybór obrazu
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 4;
        String imagesList = "";
        for(ImageServer s: runOperation.supplyAvailableImages()) {
            imagesList = imagesList.concat(s.toString() + "\n");
        }
        System.out.println(imagesList);
        JTextArea jTextAreaPicturesList = new JTextArea(imagesList);
        jTextAreaPicturesList.setEditable(false);
        panel.add(jTextAreaPicturesList, c);


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

    public abstract BufferedImage twoImagesFunction(BufferedImage srcImage);

    protected class Parameters {

        ArrayList<BufferedImage> operands;

        public Parameters() {}
    }

}
