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
import java.lang.reflect.Array;
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
    JTableFilterMask jTableMask;
    JLabel jLabelMaskaFiltru = new JLabel("Maska filtru:");
    String[] edgeModeStrings = {"Wartości brzegowe bez zmian", "Powielenie wartości brzegowych", "Operacja na istniejącym sąsiedzstwie"};
    JComboBox<String> edgeNeighborModeSelect = new JComboBox<>(edgeModeStrings);

    public OperationLlinearSmoothing() {
        super();
        this.label = "Wygładzanie";
        categories.add("LAB 3");
        categories.add("Sąsiedztwa");
        categories.add("Filtry dolnoprzepustowe");
        ItemListener itemListener = new ItemListener() {
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
        methodSelect.addItemListener(itemListener);
        neighborhoodSizeSelect.addItemListener(itemListener);
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

    public void drawConfigurationPanelRow(JPanel jPanel, GridBagConstraints gridBagConstraints, Component... comps) {
        gridBagConstraints.gridx =0;
        int gridwidth = 16/comps.length;
        gridBagConstraints.gridy++;
        for (Component component : comps) {
            gridBagConstraints.gridwidth = gridwidth;
            jPanel.add(component, gridBagConstraints);
            gridBagConstraints.gridx+=gridwidth;
        }
    }

    @Override
    public void drawConfigurationPanel(JPanel panel) {
        panel.setLayout(new GridBagLayout());
        panel.setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0f;
        c.weighty = 1.0f;

        drawConfigurationPanelRow(panel, c, new JLabel("Wygładzenie"));
        drawConfigurationPanelRow(panel, c,
                new JTextArea("Nadaje pikselom nowe wartości," + BR + "bliższe wartościom z najbliższego otoczenia"));
        drawConfigurationPanelRow(panel, c,
                new JLabel("Metoda:"),
                methodSelect,
                new JLabel("Sąsiedztwo:"),
                neighborhoodSizeSelect);
        drawConfigurationPanelRow(panel, c,
                new JLabel("Tryb:"),
                jRadioButtonColorModeHSV,
                jRadioButtonColorModeRGB,
                jButtonApply);
        drawConfigurationPanelRow(panel, c,
                jLabelHSVComponentsSelet,
                jCheckBoxHue,
                jCheckBoxSaturation,
                jCheckBoxValue);
//        drawConfigurationPanelRow(panel, c, new JLabel("Metoda operacji na pikselach brzegowych:"));
//        drawConfigurationPanelRow(panel, c, edgeNeighborModeSelect);
        drawConfigurationPanelRow(panel, c, new JLabel("Edytowalna maska wygładzania:"));
        drawConfigurationPanelRow(panel, c, jTableMask);

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
