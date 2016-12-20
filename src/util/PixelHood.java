package util;

import java.util.ArrayList;

/**
 * Created by darek on 20.12.2016.
 */
public class PixelHood <E> {

    private int horizontalBorderSize;
    private int verticalBorderSize;

    private ArrayList<E> data;

    public PixelHood(int horizontalBorderSize, int verticalBorderSize, E initializer) {
        this.horizontalBorderSize = horizontalBorderSize;
        this.verticalBorderSize = verticalBorderSize;

        int dataSize= (1 + 2 * this.horizontalBorderSize) * (1 + 2 * this.verticalBorderSize);

        data = new ArrayList<>(dataSize);
        for(int i=0;i<dataSize;++i) {
            data.add(initializer);
        }
        System.out.printf("Data size: %d", data.size());
    }

    public int getHorizontalBorderSize() {
        return horizontalBorderSize;
    }

    public int getVerticalBorderSize() {
        return verticalBorderSize;
    }

    public void setPixel(int x, int y, E e) {
        data.set(getIndex(x,y), e);
    }

    public E getPixel(int x, int y) {
        return data.get(getIndex(x,y));
    }

    private int getIndex(int x, int y) {
        if(x > horizontalBorderSize || x< -horizontalBorderSize || y>verticalBorderSize || y< -verticalBorderSize) {
            throw new IndexOutOfBoundsException();
        }
        return x + horizontalBorderSize + ( y + verticalBorderSize ) * (1 + 2 * horizontalBorderSize);
    }
}
