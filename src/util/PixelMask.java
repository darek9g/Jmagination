package util;

import java.util.ArrayList;

/**
 * Created by darek on 20.12.2016.
 */
public class PixelMask<E> extends PixelHood<E>{

    public PixelMask(int[] values, E initializer) {
        super((int)(Math.sqrt(values.length)/2), (int)(Math.sqrt(values.length)/2), initializer);
        fillMask((int[])initializer, values);
    }

    private void fillMask(int[] witness, int[] values) {
        for(int i=0; i<values.length; i++) {
            int[] newPixel = new int[witness.length];

            for(int b=0; b<witness.length; b++) {
                newPixel[b] = values[i];
            }

            setPixel(i, (E) newPixel);
        }
    }
}
