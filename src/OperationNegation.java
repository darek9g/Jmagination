import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

/**
 * Created by darek on 30.11.2016.
 */

public class OperationNegation extends Operation {

    Parameters parameters;

    public OperationNegation(ImageServer srcImageServer) {
        super(srcImageServer);
        this.label = "Negacja";
        categories.add("LAB 2");
        categories.add("Punktowe jednoargumentowe");

        parameters = new Parameters();
    }

    @Override
    public BufferedImage RunOperation(ImageServer srcImageServer) {

        BufferedImage srcImage = srcImageServer.getImg();
        Histogram histogram = srcImageServer.getHistogram();
        return negatePixelsFunction(srcImage, histogram);


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
        apply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Run();
            }
        });
    }

    @Override
    public Operation Clone() {
        return new OperationNegation(null);
    }


    public static BufferedImage negatePixelsFunction(BufferedImage srcImage, Histogram histogram) {

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
