package jmagination.util;

import java.util.ArrayList;

/**
 * Created by darek on 20.12.2016.
 */
public class PixelHoodFourWays<E> {

    protected int topBorderSize, bottomBorderSize, leftBorderSize, rightBorderSize;
    protected int dataSize;

    protected ArrayList<E> data;

    public PixelHoodFourWays(int topBorderSize, int bottomBorderSize, int leftBorderSize, int rightBorderSize, E initializer) {
        this.topBorderSize = topBorderSize;
        this.bottomBorderSize = bottomBorderSize;
        this.leftBorderSize = leftBorderSize;
        this.rightBorderSize = rightBorderSize;

        dataSize= (1 + this.topBorderSize + this.bottomBorderSize) * (1 + this.leftBorderSize + this.rightBorderSize);

        data = new ArrayList<>(dataSize);
        for(int i=0;i<dataSize;++i) {
            data.add(initializer);
        }
    }

    public int getTopBorderSize() {
        return topBorderSize;
    }

    public int getBottomBorderSize() {
        return bottomBorderSize;
    }

    public int getLeftBorderSize() {
        return leftBorderSize;
    }

    public int getRightBorderSize() {
        return rightBorderSize;
    }

    public void setPixel(int x, int y, E e) {
        data.set(getIndex(x,y), e);
    }

    protected void setPixel(int index, E e) {
        if(index>dataSize) {
            throw new IndexOutOfBoundsException();
        }
        data.set(index, e);
    }

    public E getPixel(int x, int y) {
        return data.get(getIndex(x,y));
    }

    private int getIndex(int x, int y) {
        if((x > bottomBorderSize) || (x < -topBorderSize) || (y > rightBorderSize) || (y < -leftBorderSize)) {
            throw new IndexOutOfBoundsException();
        }
        return x + topBorderSize + ( y + leftBorderSize ) * (1 + topBorderSize + bottomBorderSize);
    }

    // drugi układ współrzędnych 0,0 w lewym górnym, w dół oraz w prawo współrzędne rosną
    public E getPixelFromTopLeft(int x, int y) {
        return data.get(getIndexFromTopLeft(x,y));
    }

    protected int getIndexFromTopLeft(int x, int y) {
        if( (x<0) || (x >  topBorderSize + bottomBorderSize) || (y<0) || (y > leftBorderSize + rightBorderSize)) {
            throw new IndexOutOfBoundsException();
        }
        if(y==0) {
            return x;
        } else {
            return x + y * ( topBorderSize + bottomBorderSize + 1 );
        }
    }

    public int getDataSize(){
        return dataSize;
    }

    public E getPixel(int index) {
        return data.get(index);
    }
}
