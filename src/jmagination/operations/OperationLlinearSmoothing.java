package jmagination.operations;

import jmagination.ConstantsInitializers;
import jmagination.histogram.Histogram;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
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
    JComboBox<JTableFilterMask.EdgeMode> edgeNeighborModeSelect = new JComboBox<>(JTableFilterMask.EdgeMode.values());

    {
        label = "Wygładzenie";
        description = "Nadaje pikselom nowe wartości," + BR + "bliższe wartościom z najbliższego otoczenia";
    }

    public OperationLlinearSmoothing() {
        super();
        this.label = "Wygładzanie";
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
            float[] pixel = null;
            if (methodSelect.getSelectedIndex() == 1) {
                imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_COPY);
                switch (edgeNeighborModeSelect.getItemAt(edgeNeighborModeSelect.getSelectedIndex())) {
                    case EXISTS_NEIGHBORHOOD:
                        imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_MINUS);
                        pixel = medianSmoothHSV(pixelHood, hsvChangeMatrix);
                        break;
                    case NO_CHANGE:
                        if(imageCursor.itIsEdge(hoodSize)) {
                            pixel = bufferedImage.getHsv()[imageCursor.getPosX()][ imageCursor.getPosY()];
                            break;
                        } // jeśli piksel ma pełne sądziedzstwo, to robimy to samo co przy REPLICATE
                    case REPLICATE:
                        imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_COPY);
                        pixel = medianSmoothHSV(pixelHood, hsvChangeMatrix);
                        break;
                }
            } else {
                imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_COPY);
                pixel = smoothHSVPixel(
                        pixelHood,
                        jTableMask.getMaskMatrixEdgeMode(
                                imageCursor.getPosX(),
                                imageCursor.getPosY(),
                                width,
                                height,
                                edgeNeighborModeSelect.getItemAt(edgeNeighborModeSelect.getSelectedIndex())),
                        hsvChangeMatrix);
            }
            hsvOutMatrix[imageCursor.getPosX()][imageCursor.getPosY()] = pixel;

        } while (imageCursor.forward());

        return new SimpleHSVBufferedImage(width, height, bufferedImage.getType(), hsvOutMatrix, SimpleHSVBufferedImage.NORMALIZATION_MODE_CUTTING, new boolean[]{true, true, true});
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
            int[] pixel = null;
            if (methodSelect.getSelectedIndex() == 1) {
                switch (edgeNeighborModeSelect.getItemAt(edgeNeighborModeSelect.getSelectedIndex())) {
                    case EXISTS_NEIGHBORHOOD:
                        imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_MINUS);
                        pixel = medianSmooth(outRaster.getNumBands(), pixelHood);
                        break;
                    case NO_CHANGE:
                        if(imageCursor.itIsEdge(hoodSize)) {
                            pixel = raster.getPixel(imageCursor.getPosX(), imageCursor.getPosY(), pixel);
                            break;
                        } // jeśli piksel ma pełne sądziedzstwo, to robimy to samo co przy REPLICATE
                    case REPLICATE:
                        imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_COPY);
                        pixel = medianSmooth(outRaster.getNumBands(), pixelHood);
                        break;
                }

            } else {
                imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_COPY);
                pixel = smoothRGBPixel(
                        outRaster.getNumBands(),
                        pixelHood,
                        jTableMask.getMaskMatrixEdgeMode(
                                imageCursor.getPosX(),
                                imageCursor.getPosY(),
                                width,
                                height,
                                edgeNeighborModeSelect.getItemAt(edgeNeighborModeSelect.getSelectedIndex())));
            }
            outRaster.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), pixel);

        } while (imageCursor.forward());

        outImage.normalize(SimpleHSVBufferedImage.NORMALIZATION_MODE_CUTTING);

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
            int[] values = getBandValuesWithOutZeroValue(pixelHood, band);
            Arrays.sort(values);
            newPixel[band] = values[(values.length-1)/2];
        }
        return newPixel;
    }

    private int[] getBandValuesWithOutZeroValue(PixelHood<int[]> pixelHood, int band) {
        java.util.List<Integer> values = new ArrayList<>();
        for (int i = 0; i < pixelHood.getDataSize(); i++) {
            if(pixelHood.getPixel(i)[band] != -1) {
                values.add(pixelHood.getPixel(i)[band]);
            }
        }
        return toIntArray(values);
    }

    int[] toIntArray(java.util.List<Integer> list)  {
        int[] ret = new int[list.size()];
        int i = 0;
        for (Integer e : list)
            ret[i++] = e.intValue();
        return ret;
    }



    float[] toFloatArray(java.util.List<Float> list)  {
        float[] ret = new float[list.size()];
        int i = 0;
        for (Float e : list)
            ret[i++] = e.floatValue();
        return ret;
    }

    private float[] medianSmoothHSV(PixelHood<float[]> pixelHood, boolean[] hsvChooseMatrix) {
        float[] newPixel = new float[3];
        for (int band = 0; band < 3; band++) {
            if(hsvChooseMatrix[band]) {
                java.util.List<Float> valueList = new ArrayList<>();
                for (int i = 0; i < pixelHood.getDataSize(); i++) {
                    if (! new Float(-1F).equals(pixelHood.getPixel(i)[band])) {
                        valueList.add(pixelHood.getPixel(i)[band]);
                    }
                }
                float[] values = toFloatArray(valueList);
                Arrays.sort(values);
                newPixel[band] = values[(valueList.size() - 1) / 2];
            } else {
                newPixel[band] = pixelHood.getPixel(0,0)[band];
            }
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

        drawConfigurationPanelRow(panel, c, new JLabel(label));
        drawConfigurationPanelRow(panel, c,
                new JTextArea(description));
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
        drawConfigurationPanelRow(panel, c, new JLabel("Metoda operacji na pikselach brzegowych:"));
        drawConfigurationPanelRow(panel, c, edgeNeighborModeSelect);
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
