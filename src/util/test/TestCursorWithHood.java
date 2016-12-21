package util.test;

import util.ImageCursor;
import util.PixelHood;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Created by darek on 20.12.2016.
 */
public class TestCursorWithHood {

    public TestCursorWithHood() {

        int bufferedImageWidth = 1;
        int bufferedImageHeight = 2;

        BufferedImage bufferedImage = new BufferedImage(bufferedImageWidth, bufferedImageHeight, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = bufferedImage.getRaster();

        for(int y = 0; y<bufferedImageHeight; ++y) {
            for(int x = 0; x<bufferedImageWidth; ++x) {
                int[] pixel = new int[3];
                for(int p=0;p<pixel.length; ++p) {
                    pixel[p] = (x * y) % 256;
                }
                raster.setPixel(x,y,pixel);
            }
        }

        PixelHood<int[]> pixelHood = new PixelHood<>(2,1, new int[raster.getNumBands()]);
        ImageCursor imageCursor = new ImageCursor(bufferedImage);

         do {
                imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_MAX);

                System.out.printf("\nPixel hood for (%d,%d)\n", imageCursor.getPosX(), imageCursor.getPosY());
                printPixelHood(pixelHood);

         } while(imageCursor.forward());
    }

    private void printPixelHood(PixelHood<int[]> pixelHood) {

        System.out.println("-------------------------------------------------------------------");
        for(int i=-pixelHood.getVerticalBorderSize(); i<=pixelHood.getVerticalBorderSize(); ++i) {
            for (int j = -pixelHood.getHorizontalBorderSize(); j <= pixelHood.getHorizontalBorderSize(); ++j) {
                System.out.printf("| ");
                int[] pixel = pixelHood.getPixel(j,i);
                for(int p=0;p<pixel.length;++p) {
                    System.out.printf(" %3d ", pixel[p]);
                }
                System.out.printf("| ");

            }
            System.out.printf(" |\n");
            System.out.println("-------------------------------------------------------------------");
        }

    }

    public static void main(String[] args) {
        TestCursorWithHood test = new TestCursorWithHood();
    }
}
