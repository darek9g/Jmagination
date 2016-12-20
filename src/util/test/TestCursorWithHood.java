package util.test;

import util.ImageCursor;
import util.PixelHood;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by darek on 20.12.2016.
 */
public class TestCursorWithHood {

    public TestCursorWithHood() {

        int bufferedImageWidth = 1;
        int bufferedImageHeight = 1;

        BufferedImage bufferedImage = new BufferedImage(bufferedImageWidth, bufferedImageHeight, BufferedImage.TYPE_INT_RGB);

        for(int y = 0; y<bufferedImageHeight; ++y) {
            for(int x = 0; x<bufferedImageWidth; ++x) {
                bufferedImage.setRGB(x,y,(x*y)%(256*256));
            }
        }

        PixelHood<Integer> pixelHood = new PixelHood<>(1,1, 0);
        ImageCursor imageCursor = new ImageCursor(bufferedImage);

         do {
                imageCursor.fillPixelHood(pixelHood, ImageCursor.COMPLETE_COPY);

                System.out.printf("\nPixel hood for (%d,%d)\n", imageCursor.getPosX(), imageCursor.getPosY());
                printPixelHood(pixelHood);

         } while(imageCursor.forward());
    }

    private void printPixelHood(PixelHood<Integer> pixelHood) {

        System.out.println("----------------------------------");
        for(int i=-pixelHood.getVerticalBorderSize(); i<=pixelHood.getVerticalBorderSize(); ++i) {
            for (int j = -pixelHood.getHorizontalBorderSize(); j <= pixelHood.getHorizontalBorderSize(); ++j) {
                System.out.printf("|" + pixelHood.getPixel(i,j));
            }
            System.out.printf("|\n");
            System.out.println("----------------------------------");
        }

    }

    public static void main(String[] args) {
        TestCursorWithHood test = new TestCursorWithHood();
    }
}
