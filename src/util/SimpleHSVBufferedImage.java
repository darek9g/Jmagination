package util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

/**
 * Created by Rideau on 2016-12-18.
 */
public class SimpleHSVBufferedImage extends BufferedImage {

    private float[][][] hsv;

    public SimpleHSVBufferedImage(BufferedImage srcImage) {
        super(srcImage.getColorModel(),srcImage.copyData(null), srcImage.getColorModel().isAlphaPremultiplied(), null);
        fillHsv();
    }

    public SimpleHSVBufferedImage(int width, int height, int imageType) {
        super(width, height, imageType);
        fillHsv();
    }

    public SimpleHSVBufferedImage(int width, int height, int imageType, IndexColorModel cm) {
        super(width, height, imageType, cm);
        fillHsv();
    }

    public SimpleHSVBufferedImage(ColorModel cm, WritableRaster raster, boolean isRasterPremultiplied, Hashtable<?, ?> properties) {
        super(cm, raster, isRasterPremultiplied, properties);
        fillHsv();
    }

    public void fillHsv() {
        this.hsv = new float[this.getWidth()][this.getHeight()][3];
        float[] temp;
        for(int x = 0; x < this.getWidth(); x++){
            for (int y = 0; y < this.getHeight(); y++) {
                Color color = new Color(this.getRGB(x,y));
                temp = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), new float[3]);
                hsv[x][y][0] = temp[0];
                hsv[x][y][0] = temp[1];
                hsv[x][y][0] = temp[2];
            }
        }
    }

    public SimpleHSVBufferedImage getRgbFromHsv() {
        return  getRgbFromHsv(this.getWidth(), this.getHeight(), this.getHsv());
    }


    public static SimpleHSVBufferedImage getRgbFromHsv(int width, int height, float[][][] hsvTable) {
        SimpleHSVBufferedImage simpleHSVBufferedImage = new SimpleHSVBufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for(int x = 0; x < width; x++){
            for (int y = 0; y < height; y++) {
                Color color = Color.getHSBColor(hsvTable[x][y][0], hsvTable[x][y][1], hsvTable[x][y][2]);
                simpleHSVBufferedImage.setRGB(x, y, color.getRGB());
            }
        }
        return  simpleHSVBufferedImage;
    }

    public float[][][] getHsv() {
        return hsv;
    }

    public static Color colorFromPixel(int... pixel){
        switch (pixel.length) {
            case 1: //GREY
                return new Color(pixel[0], pixel[0], pixel[0]);
            case 3: //RGB
                return new Color(pixel[0], pixel[1], pixel[2]);
            case 4: //RGBA
                return new Color(pixel[0], pixel[1], pixel[2], pixel[3]);
            default:
                throw new java.lang.IllegalArgumentException("Niedozwolona liczba argumentów (1 dla GREY, 3 dla RGB, 4 dla RGBA).");
        }
    }
}
