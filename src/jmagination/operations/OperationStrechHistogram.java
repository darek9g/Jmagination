package jmagination.operations;

import jmagination.ConstantsInitializers;
import jmagination.histogram.Histogram;
import util.ImageCursor;
import util.PixelHood;
import util.SimpleHSVBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

import static jmagination.ConstantsInitializers.BR;

/**
 * Created by darek on 30.11.2016.
 */

public class OperationStrechHistogram extends Operation {

    Parameters parameters;

    public OperationStrechHistogram() {
        super();
        this.label = "Rozciągnij histogramu";
        categories.add("Histogramowe");

        parameters = new Parameters();
    }

    @Override
    public SimpleHSVBufferedImage RunOperationFunction(SimpleHSVBufferedImage bufferedImage, Histogram histogram) {
        return equalizeHistogramFunction(bufferedImage, histogram);
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
        JLabel title = new JLabel("Rozciąganie histogramu");
        panel.add(title, c);

        // opis
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 16;
        JTextArea description = new JTextArea("Dystrybuuje wartości kolorów równomiernie na całą" + BR + "przestrzeń wartości");
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
        return new OperationStrechHistogram();
    }

    public static SimpleHSVBufferedImage equalizeHistogramFunction(SimpleHSVBufferedImage inImage, Histogram histogram) {

        int width = inImage.getWidth();
        int height = inImage.getHeight();
        SimpleHSVBufferedImage outImage = new SimpleHSVBufferedImage(width, height, inImage.getType());
        WritableRaster raster = inImage.getRaster();
        WritableRaster outRaster = outImage.getRaster();

        int pixels = width * height;

        int bands = histogram.getData().size();
        int levels = histogram.getData().get(0).length;

        ArrayList<Integer[]> intergatedHistogramData = new ArrayList<>(bands);
        for(int ch=0; ch<bands; ++ch) {
            Integer[] bandSrc =  histogram.getData().get(ch);
            Integer[] bandData = new Integer[levels];
            intergatedHistogramData.add(bandData);

            if(bandData.length>0) bandData[0] = bandSrc[0];
            for(int level=1; level<levels; ++level) {
                bandData[level] = bandData[level-1] + bandSrc[level];
            }

            for(int level=0; level<levels; ++level) {
                bandData[level] = (int) Math.floor( (levels - 1) * ( bandData[level] + 0.0 ) / pixels);
            }
        }

        //applying changes

        PixelHood<int[]> pixelHood = new PixelHood<>(0,0, new int[raster.getNumBands()]);
        ImageCursor imageCursor = new ImageCursor(inImage);

        do {
            imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_MIN);

            int[] pixel = pixelHood.getPixel(0,0);
            int[] newPixel = new int[pixel.length];

            for(int i = 0; i<pixel.length; ++i) {
                newPixel[i] = intergatedHistogramData.get(i)[pixel[i]];
            }

            outRaster.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), newPixel);

        } while (imageCursor.forward());

        outImage.normalize();

        return outImage;
    }

    private class Parameters {

        public Parameters() {}
    }

}
