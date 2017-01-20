package util;

import java.awt.*;
import java.awt.image.*;
import java.util.Hashtable;

/**
 * Created by Rideau on 2016-12-18.
 */
public class SimpleHSVBufferedImage extends BufferedImage {

    public final static int NORMALIZATION_MODE_VOID = 0;
    public final static int NORMALIZATION_MODE_PROPORTIONAL = 1;
    public final static int NORMALIZATION_MODE_THREE_VALUED = 2;
    public final static int NORMALIZATION_MODE_CUTTING = 3;
    public final static int NORMALIZATION_MODE_BINARY = 4;

    public final static String[] normalizationModeStrings = { "Bez normalizacji", "Proporcjonalna", "Trzy-wartościowa", "Obcinająca", "Binarna"};


    // dla obrazów o zakresie jasności RGB innym niż 0-255
    int[] imageMinValues;
    int[] imageMaxValues;

    private float[][][] hsv;
    private DataBufferInt opDataBuffer;
    boolean opDataBufferDirty;

    // dla procedur normalizacji
    int[] opDataBufferMinValues;
    int[] opDataBufferMaxValues;

    int defaultNormalizationMode = NORMALIZATION_MODE_VOID;

    {
        opDataBufferDirty = false;
        opDataBufferMinValues = new int[this.getRaster().getNumBands()];
        opDataBufferMaxValues = new int[this.getRaster().getNumBands()];
        imageMinValues = new int[this.getRaster().getNumBands()];
        imageMaxValues = new int[this.getRaster().getNumBands()];

        for(int i=0;i<imageMinValues.length;i++) {
            imageMinValues[i] = 0;
            imageMaxValues[i] = 255;
        }
    }


    public SimpleHSVBufferedImage(BufferedImage srcImage) {
        super(srcImage.getColorModel(),srcImage.copyData(null), srcImage.getColorModel().isAlphaPremultiplied(), null);
        fillHsv();
    }

    public SimpleHSVBufferedImage(int width, int height, int imageType) {
        super(width, height, imageType);
    }

    public SimpleHSVBufferedImage(int width, int height, int imageType, IndexColorModel cm) {
        super(width, height, imageType, cm);
    }
    public SimpleHSVBufferedImage(int width, int height, int imageType, float[][][] hsvMatrix) {
        super(width, height, imageType);
        this.hsv = hsvMatrix;
        fillRGB();
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
                hsv[x][y][1] = temp[1];
                hsv[x][y][2] = temp[2];
            }
        }
    }

    public void fillRGB() {
        for(int x = 0; x < getWidth(); x++){
            for (int y = 0; y < getHeight(); y++) {
                Color color = Color.getHSBColor(hsv[x][y][0], hsv[x][y][1], hsv[x][y][2]);
                setRGB(x, y, color.getRGB());
            }
        }
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

    public void setPixel(int x, int y, int[] iArray) {

        maintainOpDataBuffer();

        opDataBufferDirty = true;

        for(int i=0;i<iArray.length;i++) {
            opDataBuffer.setElem(i,y * getWidth() + x, iArray[i]);

            if(iArray[i] > opDataBufferMaxValues[i]) {
                opDataBufferMaxValues[i] = iArray[i];
            }
            if(iArray[i] < opDataBufferMinValues[i]) {
                opDataBufferMinValues[i] = iArray[i];
            }
        }
    }

    public int[] getPixel(int x, int y) {
        int[] iArray = new int[opDataBuffer.getNumBanks()];
        for(int i=0;i<iArray.length;i++) {
            iArray[i] = opDataBuffer.getElem(i, y * getWidth() + x);
        }
        return iArray;
    }

    public int[] getImageMinValues() {
        return  imageMinValues;
    }

    public int[] getImageMaxValues() {
        return  imageMaxValues;
    }

    public void normalize(int mode) {

        maintainOpDataBuffer();

        if(opDataBufferDirty == false) return;

        WritableRaster raster = this.getRaster();

        for(int i=0;i<this.getHeight();i++) {
            for(int j=0;j<this.getWidth();j++) {

                int[] pixel = new int[opDataBuffer.getNumBanks()];
                for(int b=0;b<opDataBuffer.getNumBanks();b++) {
                    pixel[b] = opDataBuffer.getElem(b, i * getWidth() + j);
                }

                int newPixel[];

                switch(mode) {
                    case NORMALIZATION_MODE_PROPORTIONAL:
                        newPixel = normalizePixelProportional(pixel);
                        break;
                    case NORMALIZATION_MODE_THREE_VALUED:
                        newPixel = normalizePixel3Valued(pixel);
                        break;
                    case NORMALIZATION_MODE_BINARY:
                        newPixel = normalizePixelBinary(pixel);
                        break;
                    case  NORMALIZATION_MODE_VOID:
                    case  NORMALIZATION_MODE_CUTTING:
                        newPixel = pixel;
                        break;
                    default:
                        throw new java.lang.IllegalArgumentException("Nieprawidłowa wartość dla trybu normalizacji.");
                }

                raster.setPixel(j, i, newPixel);
            }
        }

        opDataBufferDirty = false;
    }

    public void normalize() {
        normalize(defaultNormalizationMode);
    }

    private int[] normalizePixelVoid(int[] pixel) {
        int[] newPixel = new int[pixel.length];

        for(int i=0; i<pixel.length; i++) {
            newPixel[i] =  pixel[i];
        }

        return newPixel;
    }

    private int[] normalizePixelProportional(int[] pixel) {
        int[] newPixel = new int[pixel.length];

        for(int i=0; i<pixel.length; i++) {
            newPixel[i] =  (int) Math.round(
                    ( ( pixel[i] - opDataBufferMinValues[i] + 0.0 ) / ( opDataBufferMaxValues[i] - opDataBufferMinValues[i] )
                    ) * (
                            imageMaxValues[i] - imageMinValues[i] - 1
                    )
                            + imageMinValues[i]
                    );
        }

        return newPixel;
    }

    private int[] normalizePixel3Valued(int[] pixel) {
        int[] newPixel = new int[pixel.length];

        for(int i=0; i<pixel.length; i++) {
            if(pixel[i] < imageMinValues[i]) {
                newPixel[i] = imageMinValues[i];
            } else {
                if(pixel[i] > imageMinValues[i]) {
                    newPixel[i] = imageMaxValues[i];
                } else {
                    newPixel[i] =  (int) Math.round( ( imageMaxValues[i] - imageMinValues[i] ) / 2.0 + imageMinValues[i]);
                }

            }
        }

        return newPixel;
    }

    private int[] normalizePixelBinary(int[] pixel) {
        int[] newPixel = new int[pixel.length];

        for(int i=0; i<pixel.length; i++) {
            newPixel[i] =  pixel[i] > 0 ? imageMaxValues[i] : imageMinValues[i];
        }

        return newPixel;
    }

    private void maintainOpDataBuffer() {
        if(opDataBuffer == null) {
            opDataBuffer = new DataBufferInt(this.getWidth() * this.getHeight(), this.getRaster().getNumBands());
        }

        if(opDataBufferDirty == false) {

            for(int i=0; i<this.getRaster().getNumBands(); i++) {
                opDataBufferMinValues[i] = Integer.MAX_VALUE;
                opDataBufferMaxValues[i] = Integer.MIN_VALUE;
            }

        }
    }

}
