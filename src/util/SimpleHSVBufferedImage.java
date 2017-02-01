package util;

import java.awt.*;
import java.awt.image.*;
import java.util.Hashtable;

/**
 * Created by Rideau on 2016-12-18.
 */
public class SimpleHSVBufferedImage extends BufferedImage {

    public final static int NORMALIZATION_MODE_CUTTING = 0;
    public final static int NORMALIZATION_MODE_PROPORTIONAL = 1;
    public final static int NORMALIZATION_MODE_THREE_VALUED = 2;
    public final static int NORMALIZATION_MODE_BINARY = 3;

    public final static String[] normalizationModeStrings = { "Obcinająca", "Proporcjonalna", "Trzy-wartościowa", "Binarna"};


    // określenie zakresów jasności RGB
    int[] imageMinValues;
    int[] imageMaxValues;

    // określenie zakresów jasności HSV
    float[] hsvImageMinValues;
    float[] hsvImageMaxValues;

    private float[][][] hsv;
    private DataBufferInt opDataBuffer;
    boolean opDataBufferDirty;

    // dla procedur normalizacji
    int[] opDataBufferMinValues;
    int[] opDataBufferMaxValues;

    int defaultNormalizationMode = NORMALIZATION_MODE_CUTTING;

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

        hsvImageMinValues = new float[]{0.0f, 0.0f, 0.0f };
        hsvImageMaxValues = new float[]{1.0f, 1.0f, 1.0f };
    }


    public SimpleHSVBufferedImage(BufferedImage srcImage) {
        super(srcImage.getColorModel(),srcImage.copyData(null), srcImage.getColorModel().isAlphaPremultiplied(), null);
        fillHsv();
    }

    public SimpleHSVBufferedImage(int width, int height, int imageType) {
        super(width, height, imageType);
        this.hsv = new float[this.getWidth()][this.getHeight()][3];
    }

    public SimpleHSVBufferedImage(int width, int height, int imageType, IndexColorModel cm) {
        super(width, height, imageType, cm);
        this.hsv = new float[this.getWidth()][this.getHeight()][3];
    }
    public SimpleHSVBufferedImage(int width, int height, int imageType, float[][][] hsvMatrix) {
        super(width, height, imageType);
        this.hsv = hsvMatrix;
        fillRGB();
    }

    public SimpleHSVBufferedImage(int width, int height, int imageType, float[][][] hsvMatrix, int normalizationMode, boolean[] hsvChangeMatrix) {
        super(width, height, imageType);
        this.hsv = normalizeHSV(hsvMatrix, normalizationMode, hsvChangeMatrix);
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

        if(opDataBufferDirty == false) { fillHsv(); return; }

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
                        newPixel = normalizePixelProportional(pixel, opDataBufferMinValues, opDataBufferMaxValues);
                        break;
                    case NORMALIZATION_MODE_THREE_VALUED:
                        newPixel = normalizePixel3Valued(pixel);
                        break;
                    case NORMALIZATION_MODE_BINARY:
                        newPixel = normalizePixelBinary(pixel);
                        break;
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
        fillHsv();
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

    private int[] normalizePixelProportional(int[] pixel, int[] bufferMinValues, int[] bufferMaxValues) {
        int[] newPixel = new int[pixel.length];

        for(int i=0; i<pixel.length; i++) {
            newPixel[i] =  (int) Math.round(
                    ( ( pixel[i] - bufferMinValues[i] + 0.0 ) / ( bufferMaxValues[i] - bufferMinValues[i] )
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
            newPixel[i] =  pixel[i] > imageMinValues[i] ? imageMaxValues[i] : imageMinValues[i];
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

    public float[][][] normalizeHSV(float[][][] raw, int mode, boolean[] changeMatrix) {

        float[][][] cooked = new float[raw.length][raw[0].length][raw[0][0].length];

        float[] minValues = new float[raw[0][0].length];
        float[] maxValues = new float[raw[0][0].length];

        for (int b = 0; b < raw[0][0].length; b++) {
            maxValues[b] = Float.MIN_VALUE;
            minValues[b] = Float.MAX_VALUE;
        }

        switch(mode) {
            case NORMALIZATION_MODE_PROPORTIONAL:
                for (int i = 0; i < raw.length; i++) {
                    for (int j = 0; j < raw[0].length; j++) {
                        for (int b = 0; b < raw[0][0].length; b++) {
                            if (raw[i][j][b] < minValues[b]) {
                                minValues[b] = raw[i][j][b];
                            }
                            if (raw[i][j][b] > maxValues[b]) {
                                maxValues[b] = raw[i][j][b];
                            }
                        }
                    }
                }
                break;
            default:
        }

        for(int i=0;i<raw.length;i++) {
            for(int j=0;j<raw[0].length;j++) {

                float[] pixel = new float[raw[0][0].length];
                for(int b=0;b<raw[0][0].length;b++) {
                    pixel[b] = raw[i][j][b];

                }

                float newPixel[];

                switch(mode) {
                    case NORMALIZATION_MODE_PROPORTIONAL:
                        newPixel = normalizePixelProportional(pixel, changeMatrix, minValues, maxValues);
                        break;
                    case NORMALIZATION_MODE_THREE_VALUED:
                        newPixel = normalizePixel3Valued(pixel, changeMatrix);
                        break;
                    case NORMALIZATION_MODE_BINARY:
                        newPixel = normalizePixelBinary(pixel, changeMatrix);
                        break;
                    case  NORMALIZATION_MODE_CUTTING:
                        newPixel = pixel;
                        break;
                    default:
                        throw new java.lang.IllegalArgumentException("Nieprawidłowa wartość dla trybu normalizacji.");
                }

                cooked[i][j] = newPixel;
            }
        }
        return cooked;
    }

    public void normalizeHSV(float[][][] raw, boolean[] changeMatrix) {
        normalizeHSV(raw, defaultNormalizationMode, changeMatrix);
    }

    private float[] normalizePixelVoid(float[] pixel, boolean[] changeMatrix) {
        float[] newPixel = new float[pixel.length];

        for(int i=0; i<pixel.length; i++) {
            newPixel[i] =  pixel[i];
        }

        return newPixel;
    }

    private float[] normalizePixelProportional(float[] pixel, boolean[] changeMatrix, float[] bufferMinValues, float[] bufferMaxValues) {
        float[] newPixel = new float[pixel.length];

        for(int i=0; i<pixel.length; i++) {
            if (changeMatrix[i]) {
                newPixel[i] = (float) (
                        ((pixel[i] - bufferMinValues[i]) / (bufferMaxValues[i] - bufferMinValues[i]))
                                * ( hsvImageMaxValues[i] - hsvImageMinValues[i])
                                + hsvImageMinValues[i]);
            } else {
                newPixel[i] = pixel[i];
            }
        }

        return newPixel;
    }

    private float[] normalizePixel3Valued(float[] pixel, boolean[] changeMatrix) {
        float[] newPixel = new float[pixel.length];

        for(int i=0; i<pixel.length; i++) {
            if(changeMatrix[i]) {
                if (pixel[i] < hsvImageMinValues[i]) {
                    newPixel[i] = hsvImageMinValues[i];
                } else {
                    if (pixel[i] > hsvImageMinValues[i]) {
                        newPixel[i] = hsvImageMaxValues[i];
                    } else {
                        newPixel[i] = (float) ((hsvImageMaxValues[i] - hsvImageMinValues[i]) / 2.0 + hsvImageMinValues[i]);
                    }

                }
            } else {
                newPixel[i] = pixel[i];
            }
        }

        return newPixel;
    }

    private float[] normalizePixelBinary(float[] pixel, boolean[] changeMatrix) {
        float[] newPixel = new float[pixel.length];

        for(int i=0; i<pixel.length; i++) {
            if(changeMatrix[i]) {
                newPixel[i] = pixel[i] > hsvImageMinValues[i] ? hsvImageMaxValues[i] : hsvImageMinValues[i];
            } else {
                newPixel[i] = pixel[i];
            }
        }

        return newPixel;
    }

}
