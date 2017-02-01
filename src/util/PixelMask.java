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
            for(int i=0; i<values[0].length; i++) {
                if(values[j].length != values[0].length) {
                    throw new IllegalArgumentException("Błąd w konstruktorze maski - nierówna liczba kolumn w wierszach.");
                }
                int[] newPixel = new int[witness.length];

                for (int b = 0; b < witness.length; b++) {
                    newPixel[b] = values[j][i];
                }

                // Hood jest symetryczny a Mask nie koniecznie, stąd konieczność, Hood może zawiarać nadmiarowe nieustawione pixele
                setPixel(getIndexFromTopLeft(i,j), (E) newPixel);
            }
        }
    }
}
