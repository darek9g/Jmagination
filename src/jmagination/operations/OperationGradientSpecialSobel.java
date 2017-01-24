package jmagination.operations;

import util.*;

import javax.swing.*;

import static jmagination.ConstantsInitializers.BR;
import static jmagination.operations.OperationConstants.*;
import static jmagination.operations.OperationDuplicate.duplicateImageFunction;

/**
 * Created by darek on 30.11.2016.
 */

public class OperationGradientSpecialSobel extends OperationGradientSpecialAbstract {

    {
        label = "Detekcja krawędzi Sobela";
        header = "Detekcja krawędzi Sobela";
        description = "Foo" + BR + "bar.";

        hsvModeAllowed = true;
        hsvSpecificModeAllowed = true;

        parameters = new Parameters();

        parameters.edgeModeIndex = 0;
        parameters.normalizationModeIndex = 0;

        parameters.serializedMask1 = MASK_SOBEL_GRADIENT_X_SAMPLE;
        parameters.serializedMask2 = MASK_SOBEL_GRADIENT_Y_SAMPLE;
    }

    @Override
    public Operation Clone() {
        return new OperationGradientSpecialSobel();
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
                imageCursor.fillPixelHood(pixelHood, 0, parameters.edgeModeIndex);

                int[] pixel = pixelHood.getPixel(0,0);


                for(int b = 0; b< bands; ++b) {
                        double newValueMask1 = 0;
                        double newValueMask2 = 0;

                        // maska
                        for(int i =-1; i<2; i++) {
                            for(int j=-1; j<2; j++) {
                                newValueMask1 += pixelMask1.getPixel(j,i)[b] * pixelHood.getPixel(j,i)[b];
                                newValueMask2 += pixelMask2.getPixel(j,i)[b] * pixelHood.getPixel(j,i)[b];
                            }
                        }

                        pixel[b] = (int) Math.round( Math.sqrt(Math.pow(newValueMask1,2) + Math.pow(newValueMask2,2)));
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
            imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_COPY);
            float[] pixel = pixelHood.getPixel(0,0);
            float[] newPixel = new float[3];

//            System.out.printf("Pixel z %f %f %f\n", pixel[0], pixel[1], pixel[2]);

            for(int b = 0; b<3; b++) {

                if(parameters.hsvChangeMatrix[b] == true) {

                    double newValueMask1 = 0.0d;
                    double newValueMask2 = 0.0d;

                    for (int i = -1; i < 2; i++) {
                        for (int j = -1; j < 2; j++) {
                            newValueMask1 += pixelMask1.getPixel(j, i)[b] * pixelHood.getPixel(j, i)[b];
                            newValueMask2 += pixelMask2.getPixel(j, i)[b] * pixelHood.getPixel(j, i)[b];
                        }
                    }

                    newPixel[b] = (float)  Math.sqrt(Math.pow(newValueMask1,2) + Math.pow(newValueMask2,2));
                } else {
                    newPixel[b] = pixel[b];
                }
            }

//            System.out.printf("NewPixel z %f %f %f\n", newPixel[0], newPixel[1], newPixel[2]);
            hsvOutMatrix[imageCursor.getPosX()][imageCursor.getPosY()] = newPixel;

        } while (imageCursor.forward());

        return new SimpleHSVBufferedImage(width, height, inImage.getType(), hsvOutMatrix, parameters.normalizationModeIndex, parameters.hsvChangeMatrix);
    }
}