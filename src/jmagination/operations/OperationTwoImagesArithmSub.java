package jmagination.operations;

import util.SimpleHSVBufferedImage;

import java.awt.image.WritableRaster;

import static jmagination.ConstantsInitializers.BR;

/**
 * Created by darek on 06.01.2017.
 */
public class OperationTwoImagesArithmSub extends OperationTwoImagesAbstract {

    {
        label = "Odejmowanie arytmetyczne";
        header = "Odejmowanie arytmetyczne";
        description = "Nadanie wynikowym pikselom wartości" + BR + "bezwzględne różnicy odpowiadających sobie pikesli z dwóch obrazów.";

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
            newPixel[i] = Math.abs( leftPixel[i] - rightPixel[i] ) ;
        }

        return newPixel;
    }
}
