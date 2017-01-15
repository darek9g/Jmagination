package jmagination.operations;

import jmagination.ConstantsInitializers;
import jmagination.histogram.Histogram;
import util.ImageCursor;
import util.PixelHood;
import util.SimpleHSVBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static jmagination.ConstantsInitializers.BR;
import static util.SimpleHSVBufferedImage.NORMALIZATION_MODE_PROPORTIONAL;

/**
 * Created by darek on 30.11.2016.
 */

public class OperationThinning extends Operation {

    public static final int COMPLETE_MIN = 0;
    public static final int COMPLETE_MAX = 1;
    public static final int COMPLETE_COPY = 2;
    public static final int COMPLETE_SKIP = 3;


    Parameters parameters;
    String[] edgeModes = { "Wartości minimalne", "Wartości maksymalne", "Powtórzenie piksela z obrazu", "Pominięcie brzegu"};

    JComboBox<String> methodSelect = new JComboBox<>(edgeModes);

    public OperationThinning() {
        super();
        this.label = "Ścienianie";
        categories.add("LAB 4");
        categories.add("Analiza otoczenia");

        parameters = new Parameters();

        methodSelect.setSelectedIndex(0);

    }

    @Override
    public SimpleHSVBufferedImage RunOperationFunction(SimpleHSVBufferedImage bufferedImage, Histogram histogram) {

        String method = (String) methodSelect.getSelectedItem();
        parameters.method = method;

        return thinningFunction(bufferedImage, histogram);
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
        JLabel title = new JLabel("Klasyczna metoda ścieniania");
        panel.add(title, c);

        // opis
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 16;
        JTextArea description = new JTextArea("Znajduje szkielet obrazu" + BR + "metodą klasycznej szkieletyzacji");
        description.setEditable(false);
        panel.add(description, c);

        // parametryzacja
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = GridBagConstraints.REMAINDER;
        JLabel jLabelMethodSelect = new JLabel("Sąsiedztwo pikseli brzegowch:");
        panel.add(jLabelMethodSelect, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(methodSelect, c);

        // wiersz sterowania wykonaniem
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 4;
        panel.add(jLabelColorMode, c);

        c.gridx+= c.gridwidth;
        c.gridy = 6;
        c.gridwidth = 4;
        panel.add(jRadioButtonColorModeRGB, c);

        c.gridx+= c.gridwidth;
        c.gridy = 6;
        c.gridwidth = 4;
        panel.add(jRadioButtonColorModeHSV, c);

        c.gridx+= c.gridwidth;
        c.gridy = 6;
        c.gridwidth = 4;
        panel.add(jButtonApply, c);


        configureColorModeControls();
    }

    @Override
    public Operation Clone() {
        return new OperationThinning();
    }

    public SimpleHSVBufferedImage thinningFunction(SimpleHSVBufferedImage inImage, Histogram histogram) {

        SimpleHSVBufferedImage outImage = new SimpleHSVBufferedImage(inImage.getWidth(), inImage.getHeight(), inImage.getType());

        boolean remain = true;

        while(remain == true) {
            remain = false;


            ArrayList<Point> essentialNeighbors = new ArrayList<>();
            essentialNeighbors.add(new Point( 0, 0));
            essentialNeighbors.add(new Point( 2, 0));
            essentialNeighbors.add(new Point( 0, 2));
            essentialNeighbors.add(new Point( 2, 2));

            {
                PixelHood<int[]> pixelHood = new PixelHood<>(0, 0, new int[inImage.getRaster().getNumBands()]);
                ImageCursor imageCursor = new ImageCursor(inImage);

                do {
                    imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_COPY);


                    int[] pixel = pixelHood.getPixel(0,0);
                    int[] newPixel = new int[pixel.length];

                    for(int b = 0; b<pixel.length; ++b) {
                        if
                    }

                    outImage.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), newPixel);

                } while (imageCursor.forward());

            }

            for(Point n: essentialNeighbors) {

                PixelHood<int[]> pixelHood = new PixelHood<>(1, 1, new int[inImage.getRaster().getNumBands()]);
                ImageCursor imageCursor = new ImageCursor(inImage);

                do {
                    imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_COPY);


                    int[] pixel = pixelHood.getPixel(0,0);
                    int[] newPixel = new int[pixel.length];

                    for(int b = 0; b<pixel.length; ++b) {
                        if
                    }

                    outImage.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), newPixel);

                } while (imageCursor.forward());


            }
        }



        outImage.normalize(NORMALIZATION_MODE_PROPORTIONAL);

        return outImage;
    }


    private class Parameters {
        String method = "Wartości minimalne";

        public Parameters() {}
    }

}