package util;

import java.util.ArrayList;

/**
 * Created by darek on 20.12.2016.
 */
public class PixelMask<E> extends PixelHood<E>{

    public PixelMask(int[][] values, E initializer) {
        super(values[0].length/2, values.length/2, initializer);
        fillMask((int[])initializer, values);
    }

    private void fillMask(int[] witness, int[][] values) {
        for(int j=0; j<values.length; j++) {
            for(int i=0; i<values.length; i++) {
                int[] newPixel = new int[witness.length];

                for (int b = 0; b < witness.length; b++) {
                    newPixel[b] = values[j][i];
                }

                if(j==0) {
                    setPixel(i, (E) newPixel);
                } else {
                    setPixel(j * values.length - 1 + i, (E) newPixel);
                }
            }
        }
    }
}
