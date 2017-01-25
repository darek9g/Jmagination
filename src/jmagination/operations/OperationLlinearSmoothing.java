package jmagination.operations;

import jmagination.ConstantsInitializers;
import jmagination.histogram.Histogram;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.WritableRaster;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;

import static jmagination.ConstantsInitializers.BR;

/**
 * Created by Rideau on 2017-01-06.
 */
public class OperationLlinearSmoothing extends Operation {

    String[] runModes = { "Uśrednienie", "Mediana", "Filtr krzyżyzowy", "Filtr piramidalny", "4-spójna"};
    JComboBox<String> methodSelect = new JComboBox<>(runModes);
    String[] neighborhoodSizesStrings = { "3x3", "5x5", "7x7", "9x9", "11x11", "13x13"};
    JComboBox<String> neighborhoodSizeSelect = new JComboBox<>(neighborhoodSizesStrings);
    ItemListener itemListener =  null;
    JTableFilterMask jTableMask;
    JLabel jLabelMaskaFiltru = new JLabel("Maska filtru:");

    public OperationLlinearSmoothing() {
        super();
        this.label = "Wygładzanie";
        categories.add("LAB 3");
        categories.add("Sąsiedztwa");
        categories.add("Filtry dolnoprzepustowe");
        itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (methodSelect.getSelectedIndex() == 1) {
                        jTableMask.setVisible(false);
                        jLabelMaskaFiltru.setVisible(false);
                    } else {
                        jTableMask.setVisible(true);
                        jLabelMaskaFiltru.setVisible(true);
                        updateMask();
                    }
                }
            }
        };
        jTableMask = new JTableFilterMask(380);
        jTableMask.setMaxValue(255);
        jTableMask.setMinValue(0);
        updateMask();
    }

    @Override
    public SimpleHSVBufferedImage RunOperationFunction(SimpleHSVBufferedImage bufferedImage, Histogram histogram) {
        if (jRadioButtonColorModeRGB.isSelected()) {
            return runSmoothFunctionRGB(bufferedImage);
        } else {
            bufferedImage.fillHsv();
            return runSmoothFunctionHSV(bufferedImage, jCheckBoxHue.isSelected(), jCheckBoxSaturation.isSelected(), jCheckBoxValue.isSelected());
        }
    }

    private SimpleHSVBufferedImage runSmoothFunctionHSV(SimpleHSVBufferedImage bufferedImage, boolean h, boolean s, boolean v) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        boolean hsvChangeMatrix[] = new boolean[]{h,s,v};
        float hsvOutMatrix[][][] = new float[width][height][3];
        int hoodSize = neighborhoodSizeSelect.getSelectedIndex()+1;
        PixelHood<float[]> pixelHood = new PixelHood<>(hoodSize, hoodSize, new float[3]);
        HSVImageCursor imageCursor = new HSVImageCursor(bufferedImage.getHsv(), width, height);

        do {
            imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_COPY);
            float[] pixel;
            if (methodSelect.getSelectedIndex() == 1) {
                pixel = medianSmoothHSV(pixelHood);
            } else {
                pixel = smoothHSVPixel(pixelHood, jTableMask.getMaskMatrix(), hsvChangeMatrix);
            }
            hsvOutMatrix[imageCursor.getPosX()][imageCursor.getPosY()] = pixel;

        } while (imageCursor.forward());

        return new SimpleHSVBufferedImage(width, height, bufferedImage.getType(), hsvOutMatrix);
    }

    private SimpleHSVBufferedImage runSmoothFunctionRGB(SimpleHSVBufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        SimpleHSVBufferedImage outImage = new SimpleHSVBufferedImage(width, height, bufferedImage.getType());
        WritableRaster raster = bufferedImage.getRaster();
        WritableRaster outRaster = outImage.getRaster();
        int hoodSize = neighborhoodSizeSelect.getSelectedIndex()+1;
        PixelHood<int[]> pixelHood = new PixelHood<>(hoodSize,hoodSize, new int[raster.getNumBands()]);
        ImageCursor imageCursor = new ImageCursor(bufferedImage);

        do {
            imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_COPY);
            int[] pixel;
            if (methodSelect.getSelectedIndex() == 1) {
                pixel = medianSmooth(outRaster.getNumBands(), pixelHood);
            } else {
                pixel = smoothRGBPixel(outRaster.getNumBands(), pixelHood, jTableMask.getMaskMatrix());
            }
            outRaster.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), pixel);

        } while (imageCursor.forward());

        return outImage;
    }

    private float[] smoothHSVPixel(PixelHood<float[]> pixelHood, int[][] mask, boolean[] hsvChange) {
        int dzielnik = 0;
        for (int[] items : mask)
            for (int item : items)
                dzielnik += item;

        int hor = pixelHood.getHorizontalBorderSize();
        int vert = pixelHood.getVerticalBorderSize();
        float[] newPixel = new float[3];
        for (int band = 0; band < 3; band++) {
            if (hsvChange[band]) {
                float tmp = 0;
                for (int i = -hor; i <= hor; i++)
                    for (int j = -vert; j <= vert; j++) {
                        tmp += (pixelHood.getPixel(i, j)[band]) * mask[i + hor][j + vert] / dzielnik;
                    }
                newPixel[band] = tmp;
            } else {
                newPixel[band] = pixelHood.getPixel(0,0)[band];
            }
        }
        return newPixel;
    }

    private int[] smoothRGBPixel(int numBands, PixelHood<int[]> pixelHood, int[][] mask) {
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
            newPixel[band] = Math.round(tmp);
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

    private float[] medianSmoothHSV(PixelHood<float[]> pixelHood) {
        float[] newPixel = new float[3];
        for (int band = 0; band < 3; band++) {
            float[] values = new float[pixelHood.getDataSize()];
            for (int i = 0; i < pixelHood.getDataSize(); i++) {
                values[i] = pixelHood.getPixel(i)[band];
            }
            Arrays.sort(values);
            newPixel[band] = values[(pixelHood.getDataSize()-1)/2];
        }
        return newPixel;
    }

    private void updateMask() {
        int hoodSize = neighborhoodSizeSelect.getSelectedIndex()+1;
        int[][] maskMatrix = null;
        switch (methodSelect.getSelectedIndex()) {
            case 0:
                maskMatrix = MaskGenerator.getMask(hoodSize*2+1, MaskGenerator.MaskType.AVERAGING);
                break;
            case 1:
                // Mediana - nie potrzebuje maski
                break;
            case 2:
                maskMatrix = MaskGenerator.getMask(hoodSize*2+1, MaskGenerator.MaskType.CROSS);
                break;
            case 3:
                maskMatrix = MaskGenerator.getMask(hoodSize*2+1, MaskGenerator.MaskType.PIRAMIDE);
                break;
            case 4:
                maskMatrix = MaskGenerator.getMask(hoodSize*2+1, MaskGenerator.MaskType.COHERENT4);
                break;
            default:
                throw new IllegalStateException("Nieobsłużona operacja wygładzania.");
        }

        jTableMask.fillMask(hoodSize*2+1, maskMatrix);
        jTableMask.repaint();
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
        methodSelect.addItemListener(itemListener);

        c.gridx = 8;
        c.gridy = 2;
        c.gridwidth = 4;
        JLabel jLabelNeighborHoodSelect = new JLabel("Sąsiedztwo:");
        panel.add(jLabelNeighborHoodSelect, c);

        c.gridx = 12;
        c.gridy = 2;
        c.gridwidth = 4;
        panel.add(neighborhoodSizeSelect, c);
        neighborhoodSizeSelect.addItemListener(itemListener);

        // wiersz sterowania wykonaniem
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 4;
        panel.add(jLabelHSVComponentsSelet, c);

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

        // wiersz wyboru HSV
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 4;
        panel.add(jCheckBoxHue, c);

        c.gridx += c.gridwidth;
        c.gridy = 4;
        c.gridwidth = 4;
        panel.add(jCheckBoxHue, c);

        c.gridx += c.gridwidth;
        c.gridy = 4;
        c.gridwidth = 4;
        panel.add(jCheckBoxSaturation, c);

        c.gridx += c.gridwidth;
        c.gridy = 4;
        c.gridwidth = 4;
        panel.add(jCheckBoxValue, c);

        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 8;
        panel.add(jLabelMaskaFiltru, c);

        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 16;
        panel.add(jTableMask, c);

        this.jCheckBoxSaturation.setSelected(true);
        this.jCheckBoxValue.setSelected(true);
        this.hsvSpecificModeAllowed = true;
        this.hsvModeAllowed = true;
        configureColorModeControls();
    }

    @Override
    public Operation Clone() {
        return null;
    }
}
