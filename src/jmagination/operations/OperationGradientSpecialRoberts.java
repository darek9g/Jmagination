package jmagination.operations;

import jmagination.ConstantsInitializers;
import jmagination.histogram.Histogram;
import util.*;

import javax.swing.*;
import java.awt.*;

import static jmagination.ConstantsInitializers.BR;
import static jmagination.operations.OperationConstants.*;
import static jmagination.operations.OperationDuplicate.duplicateImageFunction;

/**
 * Created by darek on 30.11.2016.
 */

public class OperationGradientSpecialRoberts extends OperationGradientSpecialAbstract {

    {
        label = "Detekcja krawędzi Robertsa";
        header = "Detekcja krawędzi Robertsa";
        description = "Foo" + BR + "bar.";

        hsvModeAllowed = true;
        hsvSpecificModeAllowed = true;

        parameters = new Parameters();

        parameters.edgeModeIndex = 0;
        parameters.normalizationModeIndex = 0;

        parameters.serializedMask1 = MASK_ROBERTS_GRADIENT_X_SAMPLE;
        parameters.serializedMask2 = MASK_ROBERTS_GRADIENT_Y_SAMPLE;

        jTableFilterMaskLeft.fillMask(parameters.serializedMask1.length,parameters.serializedMask1);
        jTableFilterMaskLeft.repaint();
        jTableFilterMaskRight.fillMask(parameters.serializedMask2.length,parameters.serializedMask2);
        jTableFilterMaskRight.repaint();
    }

    @Override
    public Operation Clone() {
        return new OperationGradientSpecialRoberts();
    }

    @Override
    public SimpleHSVBufferedImage maskFunctionRGB(SimpleHSVBufferedImage inImage) {

        SimpleHSVBufferedImage outImage = duplicateImageFunction(inImage);

        int bands = outImage.getRaster().getNumBands();

        PixelMask<int[]> pixelMask1 = new PixelMask<>(parameters.serializedMask1, new int[bands]);
        PixelMask<int[]> pixelMask2 = new PixelMask<>(parameters.serializedMask2, new int[bands]);

            ImageCursor imageCursor = new ImageCursor(outImage);

            PixelHood<int[]> pixelHood = new PixelHood<>(1, 1, new int[bands]);

            do {
                if(parameters.edgeModeIndex == ImageCursor.COMPLETE_SKIP) {
                    int x = imageCursor.getPosX();
                    int y = imageCursor.getPosY();

                    if(x==0 || x == outImage.getWidth() -1 || y == 0 || y == outImage.getHeight()) {
                        copyRGBPixel(outImage, 0, 1, imageCursor.getPosX(), imageCursor.getPosY());
                        continue;
                    }
                }
                imageCursor.fillPixelHood(pixelHood, 0, parameters.edgeModeIndex);

                int[] pixel = pixelHood.getPixel(0,0);


                for(int b = 0; b< bands; ++b) {
                        double newValueMask1 = 0;
                        double newValueMask2 = 0;

                        // maska
                        for(int i=0; i<2; i++) {
                            for(int j=0; j<2; j++) {
                                newValueMask1 += pixelMask1.getPixelFromTopLeft(j,i)[b] * pixelHood.getPixel(j,i)[b];
                                newValueMask2 += pixelMask2.getPixelFromTopLeft(j,i)[b] * pixelHood.getPixel(j,i)[b];
                            }
                        }

                        pixel[b] = (int) Math.round( Math.abs(newValueMask1) + Math.abs(newValueMask2));
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
        PixelHood<float[]> pixelHood = new PixelHood<>(1, 1, new float[3]);
        HSVImageCursor imageCursor = new HSVImageCursor(inImage.getHsv(), width, height);

        PixelMask<int[]> pixelMask1 = new PixelMask<>(parameters.serializedMask1, new int[3]);
        PixelMask<int[]> pixelMask2 = new PixelMask<>(parameters.serializedMask2, new int[3]);

        do {
            imageCursor.fillPixelHood(pixelHood, parameters.edgeModeIndex);
            float[] pixel = pixelHood.getPixel(0,0);
            float[] newPixel = new float[3];
            if(parameters.edgeModeIndex == ImageCursor.COMPLETE_SKIP) {
                int x = imageCursor.getPosX();
                int y = imageCursor.getPosY();

                if(x==0 || x == inImage.getWidth() -1 || y == 0 || y == inImage.getHeight()) {
                    hsvOutMatrix[imageCursor.getPosX()][imageCursor.getPosY()] = pixel;
                    continue;
                }
            }

            for(int b = 0; b<3; b++) {

                if(parameters.hsvChangeMatrix[b] == true) {

                    double newValueMask1 = 0.0d;
                    double newValueMask2 = 0.0d;

                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 2; j++) {
                            newValueMask1 += pixelMask1.getPixelFromTopLeft(j, i)[b] * pixelHood.getPixel(j, i)[b];
                            newValueMask2 += pixelMask2.getPixelFromTopLeft(j, i)[b] * pixelHood.getPixel(j, i)[b];
                        }
                    }

                    newPixel[b] = (float)  (Math.abs(newValueMask1) + Math.abs(newValueMask2));
                } else {
                    newPixel[b] = pixel[b];
                }
            }

            hsvOutMatrix[imageCursor.getPosX()][imageCursor.getPosY()] = newPixel;

        } while (imageCursor.forward());

        return new SimpleHSVBufferedImage(width, height, inImage.getType(), hsvOutMatrix, parameters.normalizationModeIndex, parameters.hsvChangeMatrix);
    }
}