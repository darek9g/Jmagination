package jmagination.operations;

import jmagination.util.SimpleHSVBufferedImage;

import java.awt.image.WritableRaster;

import static jmagination.ConstantsInitializers.BR;

/**
 * Created by darek on 06.01.2017.
 */
public class OperationTwoImagesLogicAND extends OperationTwoImagesAbstract {

    {
        label = "Logiczna koniunkcja";
        header = "Logiczna koniunkcja";
        description = "Nadanie wynikowym pikselom wartości" + BR + "AND bitów odpowiadających sobie pikesli z dwóch obrazów.";

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
            newPixel[i] = outImage.getImageMaxValues()[i] & ( leftPixel[i] & rightPixel[i] );
        }

        return newPixel;
    }
}
