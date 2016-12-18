import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

/**
 * Created by darek on 30.11.2016.
 */

public class OperationNegation extends Operation {

    Parameters parameters;

    public OperationNegation(ImageServer srcImageServer) {
        super();
        this.label = "Negacja";
        categories.add("LAB 2");
        categories.add("Punktowe jednoargumentowe");

        parameters = new Parameters();
    }

    @Override
    public BufferedImage RunOperationFunction(BufferedImage bufferedImage) {
        return negate(bufferedImage);
    }

    @Override
    public void drawConfigurationPanel(JPanel panel) {
        panel.setLayout(new GridBagLayout());
        panel.setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);
        JLabel title = new JLabel("Negacja");

        int panelX = 0;
        int panelY = 0;

        panel.add(title, new GUIStyler.ParamsGrid(panelX,panelY++));

        JTextArea description = new JTextArea("Opis - UZUPEŁNIĆ");
        description.setEditable(false);
        panel.add(description, new GUIStyler.ParamsGrid(panelX,panelY++));

        JButton apply  = new JButton("Wykonaj");
        panel.add(apply, new GUIStyler.ParamsGrid(panelX,panelY++));
        apply.addActionListener(runOperationTrigger);
    }

    @Override
    public Operation Clone() {
        return new OperationNegation(null);
    }

    public static BufferedImage negate(BufferedImage inImage){
        int width = inImage.getWidth();
        int height = inImage.getHeight();
        BufferedImage outImage = new BufferedImage(width, height, inImage.getType());
        WritableRaster raster = inImage.getRaster();
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                outImage.setRGB(x, y, negatePixel(raster.getPixel(x,y, new int[raster.getNumBands()])).getRGB());
            }
        }
        return outImage;
    }

    public static Color negatePixel(int... pixel){
        switch (pixel.length) {
            case 1: //GREY
                return new Color(255-pixel[0], 255-pixel[0], 255-pixel[0]);
            case 3: //RGB
                return new Color(255-pixel[0], 255-pixel[1], 255-pixel[2]);
            case 4: //RGBA
                return new Color(255-pixel[0], 255-pixel[1], 255-pixel[2], 255-pixel[3]);
            default:
                throw new java.lang.IllegalArgumentException("Niedozwolona liczba argumentów (1 dla GREY, 3 dla RGB, 4 dla RGBA).");
        }
    }

    @Deprecated
    public static BufferedImage negatePixelsFunction(BufferedImage srcImage) {

        BufferedImage resultImg;

        int channels;
        {
            ColorModel cm = srcImage.getColorModel();
            channels = cm.getNumComponents();
            boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
            WritableRaster raster = srcImage.copyData(null);
            resultImg = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
        }

        int width = srcImage.getWidth();
        int height = srcImage.getHeight();

        System.out.println("Channels " + channels);

//        Random random = new Random();

        int shift = 0;
        int mask = 0x000000ff;

        for(int ch=0; ch<channels; ++ch) {


            for(int w=0; w<width; ++w) {
                for(int h=0; h<height; ++h) {
                    int colorStripe = srcImage.getRGB(w, h);
                    int level = ( colorStripe & mask ) >> shift;

                    //selecting new level

                    int newLevel = 255 - level;
                    int newColorStripe = colorStripe & (~mask);

                    newColorStripe = newColorStripe | ( newLevel << shift );
                    resultImg.setRGB(w,h,newColorStripe);
                }
            }

            System.out.println("Shift " + shift);
            shift+=8;
            mask*=0x100;
        }

        return resultImg;
    }

    private class Parameters {
        int threshold = 128;

        public Parameters() {}
    }

}
