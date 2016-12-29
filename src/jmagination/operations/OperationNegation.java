package jmagination.operations;

import jmagination.ConstantsInitializers;
import jmagination.ImageServer;
import jmagination.histogram.Histogram;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Created by darek on 30.11.2016.
 */

public class OperationNegation extends jmagination.operations.Operation {

    Parameters parameters;

    public OperationNegation(ImageServer srcImageServer) {
        super();
        this.label = "Neguj pixele";
        categories.add("LAB 2");
        categories.add("Punktowe jednoargumentowe");

        parameters = new Parameters();
    }

    @Override
    public BufferedImage RunOperationFunction(BufferedImage bufferedImage, Histogram histogram) {
        return negate(bufferedImage);
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
        JLabel title = new JLabel("Negacja poziomów koloru");
        panel.add(title, c);

        // opis
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 16;
        JTextArea description = new JTextArea("Nadaje pikselom nowe wartości dopełniające wartości bieżące\ndo maksimum");
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
        return new OperationNegation(null);
    }

    public static BufferedImage negate(BufferedImage inImage){
        int width = inImage.getWidth();
        int height = inImage.getHeight();
        BufferedImage outImage = new BufferedImage(width, height, inImage.getType());
        WritableRaster raster = inImage.getRaster();
        WritableRaster outRaster = outImage.getRaster();
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                int[] pixel = negatePixel2(raster.getPixel(x,y, new int[raster.getNumBands()]));
                outRaster.setPixel(x, y, pixel);
            }
        }
        return outImage;
    }

    public static int[] negatePixel2(int... pixel){
        int[] newPixel = new int[pixel.length];
        for (int i = 0; i < pixel.length; i++) {
            newPixel[i] = 255-pixel[i];
        }
        return newPixel;
    }

    private class Parameters {
        int threshold = 128;

        public Parameters() {}
    }

}
