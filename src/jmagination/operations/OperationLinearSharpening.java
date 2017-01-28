package jmagination.operations;

import util.*;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import static jmagination.ConstantsInitializers.BR;
import static jmagination.operations.OperationConstants.*;
import static jmagination.operations.OperationDuplicate.duplicateImageFunction;

/**
 * Created by darek on 28.01.2017.
 */
public class OperationLinearSharpening extends OperationSharpening {

    {
        label = "Wyostrzanie liniowe";
        header = "Wyostrzanie liniowe";
        description = "Wartość piksela jest zmieniana według maski" + BR + "pochodzącej od operatora Laplace'a" + BR + "wzmacniającej wartość piksela względem sąsiedztwa";

        parameters = new Parameters();

        parameters.maskStrings = MASKS_NAMES_SHARPENING_LINEAR;
        parameters.maskValues = MASKS_SHARPENING_LINEAR;

        parameters.serializedMask = parameters.maskValues[0];
        parameters.edgeModeIndex = 0;
        parameters.normalizationModeIndex = 0;
    }

    public OperationLinearSharpening() {
        super();


        itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateMask();
                }
            }
        };


        maskSelect = new JComboBox<>(parameters.maskStrings);
        maskSelect.setSelectedIndex(0);
        maskSelect.addItemListener(itemListener);

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

        jTableMask = new JTableFilterMask(380);
        jTableMask.allowNonZeroSum = false;
//        jTableMask.setEnabled(false);

        updateMask();

    }

    @Override
    public SimpleHSVBufferedImage maskFunctionRGB(SimpleHSVBufferedImage inImage) {

        SimpleHSVBufferedImage outImage = duplicateImageFunction(inImage);

        int bands = outImage.getRaster().getNumBands();


        ImageCursor imageCursor = new ImageCursor(outImage);

        PixelMask<int[]> pixelMask = new PixelMask<>(parameters.serializedMask, new int[bands]);
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
                float newValue = 0;
                int divider = 0;

                for (int i = -pixelHood.getVerticalBorderSize(); i <= pixelHood.getVerticalBorderSize(); i++) {
                    for (int j = -pixelHood.getHorizontalBorderSize(); j <= pixelHood.getHorizontalBorderSize(); j++) {
                        newValue += pixelMask.getPixel(j,i)[b] * pixelHood.getPixel(j,i)[b];
                        divider += pixelMask.getPixel(j,i)[b];
                    }
                }

                System.out.println("Divider: " + divider);
                pixel[b] = Math.round(newValue/divider);
            }

            outImage.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), pixel);

        } while (imageCursor.forward());


        outImage.normalize(parameters.normalizationModeIndex);

        return outImage;
    }

    @Override
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
                    int divider = 0;
                    for (int i = -pixelHood.getVerticalBorderSize(); i <= pixelHood.getVerticalBorderSize(); i++) {
                        for (int j = -pixelHood.getHorizontalBorderSize(); j <= pixelHood.getHorizontalBorderSize(); j++) {
                            newValue += pixelMask.getPixel(j, i)[b] * pixelHood.getPixel(j, i)[b];
                            divider += pixelMask.getPixel(j,i)[b];
                        }
                    }

                    newPixel[b] = (float) (newValue/divider);
                } else {
                    newPixel[b] = pixel[b];
                }
            }

            hsvOutMatrix[imageCursor.getPosX()][imageCursor.getPosY()] = newPixel;

        } while (imageCursor.forward());

        return new SimpleHSVBufferedImage(width, height, inImage.getType(), hsvOutMatrix, parameters.normalizationModeIndex, parameters.hsvChangeMatrix);
    }

    @Override
    public Operation Clone() {
        return new OperationLinearSharpening();
    }

}
