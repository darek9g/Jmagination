package jmagination.operations;

import jmagination.ConstantsInitializers;
import jmagination.ImageServer;
import jmagination.histogram.Histogram;
import util.ImageCursor;
import util.MaskGenerator;
import util.PixelHood;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import static jmagination.ConstantsInitializers.BR;

/**
 * Created by Rideau on 2017-01-06.
 */
public class OperationLlinearSmoothing extends Operation{

    String[] runModes = { "Uśrednienie", "Mediana", "Filtr krzyżyzowy", "Filtr piramidalny"};
    JComboBox<String> methodSelect = new JComboBox<>(runModes);
    String[] neighborhoodSizesStrings = { "3x3", "5x5", "7x7", "9x9", "11x11", "13x13"};
    JComboBox<String> neighborhoodSizeSelect = new JComboBox<>(neighborhoodSizesStrings);

    public OperationLlinearSmoothing() {
        super();
        this.label = "Wygładzanie";
        categories.add("LAB 3");
        categories.add("Wielopunktowe");

    }

    @Override
    public BufferedImage RunOperationFunction(BufferedImage bufferedImage, Histogram histogram) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        BufferedImage outImage = new BufferedImage(width, height, bufferedImage.getType());
        WritableRaster raster = bufferedImage.getRaster();
        WritableRaster outRaster = outImage.getRaster();
        int hoodSize = neighborhoodSizeSelect.getSelectedIndex()+1;
        PixelHood<int[]> pixelHood = new PixelHood<>(hoodSize,hoodSize, new int[raster.getNumBands()]);
        ImageCursor imageCursor = new ImageCursor(bufferedImage);

        do {
            imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_COPY);
            int[] pixel;
            switch (methodSelect.getSelectedIndex()) {
                case 0:
                    pixel = smooth(outRaster.getNumBands(), pixelHood,
                            MaskGenerator.getMask(hoodSize*2+1, MaskGenerator.MaskType.AVERAGING));
                    break;
                case 1:
                    pixel = medianSmooth(outRaster.getNumBands(), pixelHood);
                    break;
                case 2:
                    pixel = smooth(outRaster.getNumBands(), pixelHood,
                            MaskGenerator.getMask(hoodSize*2+1, MaskGenerator.MaskType.CROSS));
                    break;
                case 3:
                    pixel = smooth(outRaster.getNumBands(), pixelHood,
                            MaskGenerator.getMask(hoodSize*2+1, MaskGenerator.MaskType.PIRAMIDE));
                    break;
                default:
                    throw new IllegalStateException("Nieobsłużona operacja wygładzania.");
            }
            outRaster.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), pixel);

        } while (imageCursor.forward());

        return outImage;
    }

    private int[] smooth(int numBands, PixelHood<int[]> pixelHood, int[][] mask) {
        int dzielnik = 0;
        for (int[] items : mask)
            for (int item : items)
                dzielnik += item;

        int hor = pixelHood.getHorizontalBorderSize();
        int vert = pixelHood.getVerticalBorderSize();
        int[] newPixel = new int[numBands];
        for (int band = 0; band < numBands; band++) {
            float tmp = 0;
            for (int i = -hor; i <= hor; i++)
                for (int j = -vert; j <= vert; j++) {
                    tmp += ((float)pixelHood.getPixel(i,j)[band]) * mask[i+hor][j+vert] / dzielnik;
                }
            newPixel[band] = (int)tmp;
        }
        return newPixel;
    }

    @Deprecated
    private int[] smooth(int numBands, PixelHood<int[]> pixelHood) {
        int hor = pixelHood.getHorizontalBorderSize();
        int vert = pixelHood.getVerticalBorderSize();
        int[] newPixel = new int[numBands];
        for (int band = 0; band < numBands; band++) {
            float tmp = 0;
            for (int i = -hor; i <= hor; i++)
                for (int j = -vert; j <= vert; j++) {
                    tmp += ((float)pixelHood.getPixel(i,j)[band])/pixelHood.getDataSize();
                }
            newPixel[band] = (int)tmp;
        }
        return newPixel;
    }

    private int[] medianSmooth(int numBands, PixelHood<int[]> pixelHood) {
        int[] newPixel = new int[numBands];
        for (int band = 0; band < numBands; band++) {
            int[] values = new int[pixelHood.getDataSize()];
            for (int i = 0; i < pixelHood.getDataSize(); i++) {
                values[i] = pixelHood.getPixel(i)[band];
            }
            Arrays.sort(values);
            newPixel[band] = values[(pixelHood.getDataSize()-1)/2];
        }
        return newPixel;
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
        JLabel title = new JLabel("Wygładzenie");
        panel.add(title, c);

        // opis
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 16;
        JTextArea description = new JTextArea("Nadaje pikselom nowe wartości," + BR +"bliższe wartościom z najbliższego otoczenia");
        description.setEditable(false);
        panel.add(description, c);

        // parametryzacja
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 4;
        JLabel jLabelMethodSelect = new JLabel("Metoda:");
        panel.add(jLabelMethodSelect, c);

        c.gridx = 4;
        c.gridy = 2;
        c.gridwidth = 4;
        panel.add(methodSelect, c);

        c.gridx = 8;
        c.gridy = 2;
        c.gridwidth = 4;
        JLabel jLabelNeighborHoodSelect = new JLabel("Sąsiedztwo:");
        panel.add(jLabelNeighborHoodSelect, c);

        c.gridx = 12;
        c.gridy = 2;
        c.gridwidth = 4;
        panel.add(neighborhoodSizeSelect, c);

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
        return null;
    }
}
