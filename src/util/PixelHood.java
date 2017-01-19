package util;

import java.util.ArrayList;

/**
 * Created by darek on 20.12.2016.
 */
public class PixelHood <E> {

    protected int horizontalBorderSize;
    protected int verticalBorderSize;
    protected int dataSize;

    protected ArrayList<E> data;

    public PixelHood(int horizontalBorderSize, int verticalBorderSize, E initializer) {
        this.horizontalBorderSize = horizontalBorderSize;
        this.verticalBorderSize = verticalBorderSize;

        dataSize= (1 + 2 * this.horizontalBorderSize) * (1 + 2 * this.verticalBorderSize);

        data = new ArrayList<>(dataSize);
        for(int i=0;i<dataSize;++i) {
            data.add(initializer);
        }
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

    public void setPixel(int index, E e) {
        if(index>dataSize) {
            throw new IndexOutOfBoundsException();
        }
        data.set(index, e);
    }

    public E getPixel(int x, int y) {
        return data.get(getIndex(x,y));
    }

    private int getIndex(int x, int y) {
        if((x > horizontalBorderSize) || (x < -horizontalBorderSize) || (y > verticalBorderSize) || (y < -verticalBorderSize)) {
            throw new IndexOutOfBoundsException();
        }
        return x + horizontalBorderSize + ( y + verticalBorderSize ) * (1 + 2 * horizontalBorderSize);
    }

    public int getDataSize(){
        return dataSize;
    }

    public E getPixel(int index) {
        return data.get(index);
    }
}
