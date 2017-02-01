package jmagination.operations;

import util.ImageCursor;
import util.PixelHood;
import util.SimpleHSVBufferedImage;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

import static jmagination.ConstantsInitializers.BR;

/**
 * Created by darek on 06.01.2017.
 */
public class OperationTwoImagesArithmAdd extends OperationTwoImagesAbstract {

    {
        label = "Dodawanie arytmetyczne";
        header = "Dodawanie arytmetyczne";
        description = "Nadanie wynikowym pikselom wartości" + BR + "sumy odpowiadających sobie pikesli z dwóch obrazów.";

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
            newPixel[i] = (int) Math.round( ( leftPixel[i] + rightPixel[i] ) / 2.0 );
        }

        return newPixel;
    }
}
