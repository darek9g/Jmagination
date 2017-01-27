package util;

import java.awt.image.WritableRaster;

/**
 * Created by darek on 20.12.2016.
 */
public class ImageCursor extends AbstractImageCursor<int[]>{


    private SimpleHSVBufferedImage img;
    WritableRaster raster;

    public ImageCursor(SimpleHSVBufferedImage bufferedImage) {
        this.img = bufferedImage;
        raster = this.img.getRaster();

        if(this.img == null) { return; }

        posX = 0;
        posY = 0;

        width = img.getWidth();
        height = img.getHeight();

    }

    protected int[] getPixelValue(int x, int y, int bufferId) {
        switch (bufferId) {
            case 1:
                return img.getPixel(x, y);
        }
        return raster.getPixel(x, y, new int[raster.getNumBands()]);
    }

    protected int[] getPixelMinPossibleValue() {
        int[] ret = new int[raster.getNumBands()];
        for(int i=0;i<raster.getNumBands();++i) {
            ret[i] = img.getImageMinValues()[i];
        }
        return ret;
    }

    protected int[] getPixelMaxPossibleValue() {
        int[] ret = new int[raster.getNumBands()];
        for(int i=0;i<raster.getNumBands();++i) {
            ret[i] = img.getImageMaxValues()[i];
        }
        return ret;
    }

    @Override
    protected int[] getSkipValue() {
        int[] ret = new int[raster.getNumBands()];
        for(int i=0;i<raster.getNumBands();++i) {
            ret[i] = -1;
        }
        return ret;
    }

}
