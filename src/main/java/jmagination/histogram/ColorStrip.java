package jmagination.histogram;
import java.awt.*;

class ColorStrip implements Comparable<ColorStrip>{
    int size;
    Color color;

    public ColorStrip(int size, Color color) {
        this.size = size;
        this.color = color;
    }


    public int compareTo(ColorStrip o) {
        if(size==o.size) {
            return 0;
        }
        else {
            if(size>o.size) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
