package jmagination.operations;

import jmagination.ConstantsInitializers;
import jmagination.histogram.Histogram;
import jmagination.util.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.CellEditorListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;

import static jmagination.ConstantsInitializers.BR;
import static jmagination.operations.OperationConstants.*;
import static jmagination.operations.OperationDuplicate.duplicateImageFunction;

/**
 * Created by darek on 30.11.2016.
 */

public class OperationMaskCombination extends Operation {

    Parameters parameters;


    JComboBox<String> smoothMaskSelect;
    JComboBox<String> sharpMaskSelect;

    JComboBox<String> edgeNeighborModeSelect;
    JComboBox<String> normalizationSelect;

    JTableFilterMask jTableSmoothMask;
    JTableFilterMask jTableSharpMask;
    JTableFilterMask jTableFinalMask;


    private int[][] smoothMask;
    private int[][] sharpMask;

    {
        label = "Liniowe złożenie dwóch masek";
        header = "Liniowe złożenie dwóch masek w jedną";
        description = "Wartości pikseli są modyfikowane według maski 5x5" + BR + "wyliczonej z podanych masek 3x3";

        hsvModeAllowed = true;
        hsvSpecificModeAllowed = true;

        parameters = new Parameters();

        parameters.edgeModeIndex = 0;
        parameters.normalizationModeIndex = 0;

        jTableSmoothMask = new JTableFilterMask(190);
        jTableSmoothMask.allowNonZeroSum = false;
        jTableSharpMask = new JTableFilterMask(190);
        jTableSmoothMask.allowNonZeroSum = false;
        jTableFinalMask = new JTableFilterMask(380);
        jTableSmoothMask.allowNonZeroSum = false;

    }

    public OperationMaskCombination() {
        super();

        categories.add("Sąsiedztwa");
        categories.add("Gradientowe");
        categories.add("Filtry górnoprzepustowe");
        categories.add("Filtry dolnoprzepustowe");



        smoothMaskSelect = new JComboBox<>(parameters.smooothMaskStrings);
        smoothMaskSelect.setSelectedIndex(MaskGenerator.MaskType.AVERAGING.type-1);
        smoothMask = MaskGenerator.getMask(parameters.maskSize, MaskGenerator.MaskType.AVERAGING);


        sharpMaskSelect = new JComboBox<>(parameters.sharpMaskStrings);
        sharpMaskSelect.setSelectedIndex(0);
        sharpMask = parameters.sharpMaskValues[sharpMaskSelect.getSelectedIndex()];

        parameters.maskTrueCoeffDivider = calculateCompCoeffDivider(smoothMask,sharpMask);
        parameters.serializedMask = calculateCompMask(smoothMask,sharpMask);

        edgeNeighborModeSelect = new JComboBox<>(parameters.edgeModeStrings);
        edgeNeighborModeSelect.setSelectedIndex(0);

        normalizationSelect = new JComboBox<>(parameters.normalizationModeStrings);
        normalizationSelect.setSelectedIndex(0);

        if(parameters.hsvChangeMatrix[0] == true) {
            jCheckBoxHue.setSelected(true);
        } else {
            jCheckBoxHue.setSelected(false);
        }

        if(parameters.hsvChangeMatrix[1] == true) {
            jCheckBoxSaturation.setSelected(true);
        } else {
            jCheckBoxSaturation.setSelected(false);
        }

        if(parameters.hsvChangeMatrix[2] == true) {
            jCheckBoxValue.setSelected(true);
        } else {
            jCheckBoxValue.setSelected(false);
        }

        jTableSmoothMask.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateFinalMask();
            }
        });

        smoothMaskSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox comboBox = (JComboBox) e.getSource();

                MaskGenerator.MaskType maskType = null;
                for(MaskGenerator.MaskType m: MaskGenerator.MaskType.values()) {
                    if(m.type - 1 == comboBox.getSelectedIndex()) {
                        maskType = m;
                    }
                }
                if(maskType==null) {
                    throw new IllegalStateException("Nieobsłużona operacja wygładzania.");
                }
                smoothMask = MaskGenerator.getMask(parameters.maskSize, maskType);
                jTableSmoothMask.fillMask(smoothMask.length, smoothMask);
                jTableSmoothMask.repaint();
                parameters.maskTrueCoeffDivider = calculateCompCoeffDivider(smoothMask,sharpMask);

                updateFinalMask();
            }
        });

        sharpMaskSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox comboBox = (JComboBox) e.getSource();
                sharpMask = parameters.sharpMaskValues[comboBox.getSelectedIndex()];
                jTableSharpMask.fillMask(sharpMask.length, sharpMask);
                jTableSharpMask.repaint();
                parameters.maskTrueCoeffDivider = calculateCompCoeffDivider(smoothMask,sharpMask);
                parameters.serializedMask = calculateCompMask(smoothMask,sharpMask);

                updateFinalMask();
            }
        });

        jTableSharpMask.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateFinalMask();
            }
        });

        jTableSmoothMask.fillMask(smoothMask.length, smoothMask);
        jTableSmoothMask.repaint();
        jTableSharpMask.fillMask(sharpMask.length, sharpMask);
        jTableSharpMask.repaint();
        updateFinalMask();
    }

    private void updateFinalMask() {
        int[][] finalMask = calculateCompMask(jTableSmoothMask.getMaskMatrix(),jTableSharpMask.getMaskMatrix());
        jTableFinalMask.fillMask(finalMask.length, finalMask);
        jTableFinalMask.repaint();
    }




    public static int calculateCompCoeffDivider(int[][] left, int[][] right) {

        if(left.length % 2 == 0) {
            throw new IllegalArgumentException("Warunek aby pierwszy argument był tablicą o nieparzystej liczbie wierszy nie spełniony.");
        }
        if(left[0].length % 2 == 0) {
            throw new IllegalArgumentException("Warunek aby pierwszy argument był tablicą o nieparzystej liczbie kolumn nie spełniony.");
        }
        if(right.length % 2 == 0) {
            throw new IllegalArgumentException("Warunek aby drugi argument był tablicą o nieparzystej liczbie wierszy nie spełniony.");
        }
        if(right[0].length % 2 == 0) {
            throw new IllegalArgumentException("Warunek aby drugi argument był tablicą o nieparzystej liczbie kolumn nie spełniony.");
        }

        return left.length * left[0].length * right.length * right[0].length % 2;
    }

    public static int[][] calculateCompMask(int[][] left, int[][] right) {
/*
        System.out.println("");
        System.out.println("");
        for(int i=0;i<left.length;i++) {
            System.out.printf("|");
            for(int j=0;j<left[i].length;j++) {
                System.out.printf(" %d |", left[i][j]);
            }
            System.out.println();
        }
        System.out.println("Koniec lewej");

        for(int i=0;i<right.length;i++) {
            System.out.printf("|");
            for(int j=0;j<right[i].length;j++) {
                System.out.printf(" %d |", right[i][j]);
            }
            System.out.println();
        }
        System.out.println("Koniec prawej");
*/


        if(left.length % 2 == 0) {
            throw new IllegalArgumentException("Warunek aby pierwszy argument był tablicą o nieparzystej liczbie wierszy nie spełniony.");
        }
        if(left[0].length % 2 == 0) {
            throw new IllegalArgumentException("Warunek aby pierwszy argument był tablicą o nieparzystej liczbie kolumn nie spełniony.");
        }
        if(right.length % 2 == 0) {
            throw new IllegalArgumentException("Warunek aby drugi argument był tablicą o nieparzystej liczbie wierszy nie spełniony.");
        }
        if(right[0].length % 2 == 0) {
            throw new IllegalArgumentException("Warunek aby drugi argument był tablicą o nieparzystej liczbie kolumn nie spełniony.");
        }

        PixelMask<int[]> leftPixelMask = new PixelMask<>(left, new int[1]);
        PixelMask<int[]> rightPixelMask = new PixelMask<>(right, new int[1]);

        // przygotowanie obszaru roboczego, wypełnienie 0
        SimpleHSVBufferedImage workImg= new SimpleHSVBufferedImage( 2 * ( leftPixelMask.getHorizontalBorderSize() + rightPixelMask.getHorizontalBorderSize() ) + 1,
                2 * ( leftPixelMask.getVerticalBorderSize() + rightPixelMask.getVerticalBorderSize() ) + 1, BufferedImage.TYPE_BYTE_GRAY);
        int bands = workImg.getRaster().getNumBands();
        ImageCursor imageCursor = new ImageCursor(workImg);


        do {
            int[] newPixel = new int[bands];
            for(int i=0;i<bands;i++) {
                newPixel[i] = 0;
            }
            workImg.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), newPixel);

        } while (imageCursor.forward());
        workImg.normalize(SimpleHSVBufferedImage.NORMALIZATION_MODE_CUTTING);

        //wstawenie lewej maski w środek obrazu
        {
            imageCursor.reset();
            PixelHood<int[]> pixelHood = new PixelHood<>(leftPixelMask.getHorizontalBorderSize(), leftPixelMask.getVerticalBorderSize(), new int[bands]);
            try {
                imageCursor.goTo(workImg.getWidth()/2, workImg.getHeight()/2);
            } catch (Exception e) {
                throw new ArrayIndexOutOfBoundsException("Błąd wyliczenia współrzednych srodkowego pixela obrazu.");
            }
            for (int j = -pixelHood.getVerticalBorderSize(); j <= pixelHood.getVerticalBorderSize(); j++) {
                for (int i = -pixelHood.getHorizontalBorderSize(); i <= pixelHood.getHorizontalBorderSize(); i++) {
                    int[] newPixel = new int[bands];
                    for (int b = 0; b < bands; b++) {
                        newPixel[b] = leftPixelMask.getPixel(i, j)[b];
                    }
                    workImg.setPixel(imageCursor.getPosX()+i, imageCursor.getPosY()+j, newPixel);
                }
            }


            workImg.normalize(SimpleHSVBufferedImage.NORMALIZATION_MODE_CUTTING);
        }

        // zaaplikowanie maski prawej (dane zostają w buforze 1)
        {
            imageCursor.reset();
            PixelHood<int[]> pixelHood = new PixelHood<>(rightPixelMask.getHorizontalBorderSize(), rightPixelMask.getVerticalBorderSize(), new int[bands]);
            do {
                imageCursor.fillPixelHood(pixelHood, 0, ImageCursor.COMPLETE_MIN);

                int[] newPixel = new int[bands];
                for (int b = 0; b < bands; b++) {
                    double newValue = 0;
                    for (int j = -pixelHood.getVerticalBorderSize(); j <= pixelHood.getVerticalBorderSize(); j++) {
                        for (int i = -pixelHood.getHorizontalBorderSize(); i <= pixelHood.getHorizontalBorderSize(); i++) {
                            newValue += pixelHood.getPixel(i, j)[b] * rightPixelMask.getPixel(i, j)[b];
                        }
                    }

//                    newPixel[b] = pixelHood.getPixel(0, 0)[b];
                    newPixel[b] = (int) Math.round(newValue);
                }
                workImg.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), newPixel);

            } while (imageCursor.forward());
        }

        // zebranie wartości maski wynikowej (z bufora 1 obrazu)
        int[][] retArr = new int[2 * ( leftPixelMask.getVerticalBorderSize() + rightPixelMask.getVerticalBorderSize() ) + 1]
                [ 2 * ( leftPixelMask.getHorizontalBorderSize() + rightPixelMask.getHorizontalBorderSize() ) + 1];

        {
            PixelHood<int[]> pixelHood = new PixelHood<>(retArr[0].length/2, retArr.length/2, new int[bands]);
            try {
                imageCursor.goTo(pixelHood.getHorizontalBorderSize(), pixelHood.getVerticalBorderSize());
            } catch (Exception e) {
                throw new ArrayIndexOutOfBoundsException("Błąd wyliczenia współrzednych srodkowego pixela obrazu.");
            }
            imageCursor.fillPixelHood(pixelHood, 1, ImageCursor.COMPLETE_MIN);

            for (int j = -pixelHood.getVerticalBorderSize(); j <= pixelHood.getVerticalBorderSize(); j++) {
                for (int i = -pixelHood.getHorizontalBorderSize(); i <= pixelHood.getHorizontalBorderSize(); i++) {
                    retArr[j+pixelHood.getVerticalBorderSize()][i+pixelHood.getHorizontalBorderSize()]=pixelHood.getPixel(i, -j)[0];
                }
            }
        }

        return retArr;
    }

    @Override
    public SimpleHSVBufferedImage RunOperationFunction(SimpleHSVBufferedImage bufferedImage, Histogram histogram) {

        parameters.serializedMask = jTableFinalMask.getMaskMatrix();
        parameters.edgeModeIndex = edgeNeighborModeSelect.getSelectedIndex();
        parameters.normalizationModeIndex = normalizationSelect.getSelectedIndex();

        if (jRadioButtonColorModeRGB.isSelected()) {
            parameters.colorMode = OP_MODE_RGB;
        } else {
            parameters.colorMode = OP_MODE_HSV;

            if(jCheckBoxHue.isSelected() == true) {
                parameters.hsvChangeMatrix[0] = true;
            } else {
                parameters.hsvChangeMatrix[0] = false;
            }

            if(jCheckBoxSaturation.isSelected() == true) {
                parameters.hsvChangeMatrix[1] = true;
            } else {
                parameters.hsvChangeMatrix[1] = false;
            }

            if(jCheckBoxValue.isSelected() == true) {
                parameters.hsvChangeMatrix[2] = true;
            } else {
                parameters.hsvChangeMatrix[2] = false;
            }
        }

        return maskFunctionInterface(bufferedImage, histogram);
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
        JTextArea jTextAreaDescription = new JTextArea(description);
        jTextAreaDescription.setEditable(false);
        panel.add(jTextAreaDescription, c);

        // parametryzacja

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 8;
        JLabel jLabelSmoothMaskSelect = new JLabel("Maska: etap I (wygładzanie):");
        panel.add(jLabelSmoothMaskSelect, c);

        c.gridx += c.gridwidth;
        c.gridy = 2;
        c.gridwidth = 8;
        JLabel jLabelSharpMaskSelect = new JLabel("etap II (wyostrzanie) według:");
        panel.add(jLabelSharpMaskSelect, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 8;
        panel.add(smoothMaskSelect, c);

        c.gridx += c.gridwidth;
        c.gridy = 3;
        c.gridwidth = 8;
        panel.add(sharpMaskSelect, c);

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 8;
        panel.add(jTableSmoothMask, c);

        c.gridx += c.gridwidth;
        c.gridy = 4;
        c.gridwidth = 8;
        panel.add(jTableSharpMask, c);

        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 6;
        JLabel jLabelCompMask = new JLabel("Maska wynikowa:");
        panel.add(jLabelCompMask, c);

        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(jTableFinalMask, c);

        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = GridBagConstraints.REMAINDER;
        JLabel jLabelEdgeNeighborModeSelect = new JLabel("Sąsiedztwo pikseli brzegowch:");
        panel.add(jLabelEdgeNeighborModeSelect, c);

        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(edgeNeighborModeSelect, c);

        c.gridx = 0;
        c.gridy = 9;
        c.gridwidth = GridBagConstraints.REMAINDER;
        JLabel jLabelNormalizationSelect = new JLabel("Metoda normalizacji:");
        panel.add(jLabelNormalizationSelect, c);

        c.gridx = 0;
        c.gridy = 10;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(normalizationSelect, c);

        // wiersz sterowania wykonaniem
        c.gridx = 0;
        c.gridy = 11;
        c.gridwidth = 4;
        panel.add(jLabelColorMode, c);

        c.gridx+= c.gridwidth;
        c.gridy = 11;
        c.gridwidth = 4;
        panel.add(jRadioButtonColorModeRGB, c);

        c.gridx+= c.gridwidth;
        c.gridy = 11;
        c.gridwidth = 4;
        panel.add(jRadioButtonColorModeHSV, c);

        c.gridx+= c.gridwidth;
        c.gridy = 11;
        c.gridwidth = 4;
        panel.add(jButtonApply, c);

        // ruchomy wiersz sterowania wykonaniem HSV
        c.gridx = 0;
        c.gridy = 12;
        c.gridwidth = 4;
        panel.add(jLabelHSVComponentsSelet, c);

        c.gridx += c.gridwidth;
        c.gridy = 12;
        c.gridwidth = 4;
        panel.add(jCheckBoxHue, c);

        c.gridx += c.gridwidth;
        c.gridy = 12;
        c.gridwidth = 4;
        panel.add(jCheckBoxSaturation, c);

        c.gridx += c.gridwidth;
        c.gridy = 12;
        c.gridwidth = 4;
        panel.add(jCheckBoxValue, c);


        configureColorModeControls();
    }

    @Override
    public Operation Clone() {
        return new OperationMaskCombination();
    }

    public SimpleHSVBufferedImage maskFunctionInterface(SimpleHSVBufferedImage inImage, Histogram histogram) {
        return maskFunction(inImage, parameters.serializedMask);
    }

    public SimpleHSVBufferedImage maskFunction(SimpleHSVBufferedImage inImage, int[][] serializedMask) {
        switch(parameters.colorMode) {
            case OP_MODE_RGB:
                return maskFunctionRGB(inImage);
            case OP_MODE_HSV:
                return maskFunctionHSV(inImage);
            default:
                throw new IllegalStateException("Nieobsłużona tryb koloru.");
        }
    }

    public SimpleHSVBufferedImage maskFunctionRGB(SimpleHSVBufferedImage inImage) {

        SimpleHSVBufferedImage outImage = duplicateImageFunction(inImage);

        int bands = outImage.getRaster().getNumBands();

        PixelMask<int[]> pixelMask = new PixelMask<>(parameters.serializedMask, new int[bands]);

        ImageCursor imageCursor = new ImageCursor(outImage);

        PixelHood<int[]> pixelHood = new PixelHood<>(pixelMask.getHorizontalBorderSize(), pixelMask.getVerticalBorderSize(), new int[bands]);

        do {
            if(parameters.edgeModeIndex == ImageCursor.COMPLETE_SKIP) {
                int x = imageCursor.getPosX();
                int y = imageCursor.getPosY();

                if(x==0 || x == outImage.getWidth() -1 || y == 0 || y == outImage.getHeight() - 1) {
                    copyRGBPixel(outImage, 0, 1, imageCursor.getPosX(), imageCursor.getPosY());
                    continue;
                }
            }
            imageCursor.fillPixelHood(pixelHood, 0, parameters.edgeModeIndex);

            int[] pixel = pixelHood.getPixel(0,0);


            for(int b = 0; b< bands; ++b) {
                    double newValue = 0;

                    for(int i=-pixelHood.getVerticalBorderSize(); i<=pixelHood.getVerticalBorderSize(); i++) {
                        for(int j=-pixelHood.getHorizontalBorderSize(); j<=pixelHood.getHorizontalBorderSize(); j++) {
                            newValue += pixelMask.getPixel(j,i)[b] * pixelHood.getPixel(j,i)[b];
                        }
                    }

                    pixel[b] = (int) Math.round(newValue / parameters.maskTrueCoeffDivider);
            }

            outImage.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), pixel);

        } while (imageCursor.forward());


        outImage.normalize(parameters.normalizationModeIndex);

        return outImage;
    }

    public SimpleHSVBufferedImage maskFunctionHSV(SimpleHSVBufferedImage inImage) {

        int width = inImage.getWidth();
        int height = inImage.getHeight();

        float hsvOutMatrix[][][] = new float[width][height][3];
        HSVImageCursor imageCursor = new HSVImageCursor(inImage.getHsv(), width, height);

        PixelMask<int[]> pixelMask = new PixelMask<>(parameters.serializedMask, new int[3]);

        PixelHood<float[]> pixelHood = new PixelHood<>(pixelMask.getHorizontalBorderSize(), pixelMask.getVerticalBorderSize(), new float[3]);

        do {
            imageCursor.fillPixelHood(pixelHood, parameters.edgeModeIndex);
            float[] pixel = pixelHood.getPixel(0,0);
            float[] newPixel = new float[3];
            if(parameters.edgeModeIndex == ImageCursor.COMPLETE_SKIP) {
                int x = imageCursor.getPosX();
                int y = imageCursor.getPosY();

                if(x==0 || x == inImage.getWidth() -1 || y == 0 || y == inImage.getHeight() - 1) {
                    hsvOutMatrix[imageCursor.getPosX()][imageCursor.getPosY()] = pixel;
                    continue;
                }
            }

            for(int b = 0; b<3; b++) {

                if(parameters.hsvChangeMatrix[b] == true) {

                    double newValue = 0.0d;
                    for(int i=-pixelHood.getVerticalBorderSize(); i<=pixelHood.getVerticalBorderSize(); i++) {
                        for(int j=-pixelHood.getHorizontalBorderSize(); j<=pixelHood.getHorizontalBorderSize(); j++) {
                            newValue += pixelMask.getPixel(j, i)[b] * pixelHood.getPixel(j, i)[b];
                        }
                    }

                    newPixel[b] = (float) (newValue / parameters.maskTrueCoeffDivider);
                } else {
                    newPixel[b] = pixel[b];
                }
            }

            hsvOutMatrix[imageCursor.getPosX()][imageCursor.getPosY()] = newPixel;

        } while (imageCursor.forward());

        return new SimpleHSVBufferedImage(width, height, inImage.getType(), hsvOutMatrix, parameters.normalizationModeIndex, parameters.hsvChangeMatrix);
    }

    protected static class Parameters {

        private static final int maskSize = 3;

        public static final String[] smooothMaskStrings = MASKS_NAMES_SMOOTHING;
        public static final String[] sharpMaskStrings = MASKS_NAMES_SHARPENING_LINEAR;
        public static final int[][][] sharpMaskValues = MASKS_SHARPENING_LINEAR;

        public static final String[] edgeModeStrings = ImageCursor.edgeModeStrings;

        public static final String[] normalizationModeStrings = SimpleHSVBufferedImage.normalizationModeStrings;

        int[][] serializedMask;
        int maskTrueCoeffDivider;

        int edgeModeIndex;
        int normalizationModeIndex;

        int colorMode;
        boolean[] hsvChangeMatrix;

        public Parameters() {
            serializedMask = null;
            maskTrueCoeffDivider = 1;
            edgeModeIndex = 0;
            normalizationModeIndex = 0;

            colorMode = OP_MODE_RGB;
            hsvChangeMatrix = new boolean[]{false,true,true};
        }

    }
}