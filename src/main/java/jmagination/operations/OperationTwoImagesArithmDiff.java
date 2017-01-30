package jmagination.operations;

import jmagination.util.SimpleHSVBufferedImage;

import java.awt.image.WritableRaster;

import static jmagination.ConstantsInitializers.BR;

/**
 * Created by darek on 06.01.2017.
 */
public class OperationTwoImagesArithmDiff extends OperationTwoImagesAbstract {

    {
        label = "Znajdowanie różnic";
        header = "Znajdowanie różnic";
        description = "Nadanie wynikowym pikselom wartości maksymalnej bądź minimalnej" + BR + "w zależności od wówności pikseli z dwóch obrazów";

        parameters = new Parameters();

    }

    @Override
    public Operation Clone() {
        return null;
    }


    @Override
    public int[] twoImagesPixelFunction(SimpleHSVBufferedImage outImage, int[] leftPixel, int[] rightPixel, int fillValue) {
        WritableRaster outRaster = outImage.getRaster();
        int[] newPixel = new int[outRaster.getNumBands()];
        for(int i=0; i<newPixel.length; ++i) {
            newPixel[i] = fillValue;
        }

        for(int i=0; i<(leftPixel.length < rightPixel.length ? leftPixel.length : rightPixel.length); ++i) {

            if(leftPixel[i] == rightPixel[i]) {
                newPixel[i] = outImage.getImageMaxValues()[i];
            } else {
                newPixel[i] = outImage.getImageMinValues()[i];
            }
        }

        return newPixel;
    }
}
