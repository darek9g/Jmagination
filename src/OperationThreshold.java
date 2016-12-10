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

public class OperationThreshold extends Operation {

    JPanel configurationPanel;
    JTextField thresholdJTextField = new JTextField("128");

    public OperationThreshold(ImageServer srcImageServer) {
        super(srcImageServer);
        this.label = "Threshold pixels";
        categories.add("LAB 2");
        categories.add("SINGLE POINT");
    }

    @Override
    public BufferedImage RunOperation(ImageServer srcImageServer) {

        BufferedImage srcImage = srcImageServer.getImg();
        System.out.printf("Test input %d",Integer.getInteger(thresholdJTextField.getText()));
//        return thresholdPixelsFunction(srcImage, Integer.getInteger(thresholdJTextField.getText()));
        return thresholdPixelsFunction(srcImage, 20);
    }

    @Override
    public void drawConfigurationPanel(JPanel panel) {
        panel.setLayout(new GridBagLayout());
        panel.setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);
        JLabel title = new JLabel("Threshold pixels");

        int panelX = 0;
        int panelY = 0;

        panel.add(title, new GUIStyler.ParamsGrid(panelX,panelY++));

        JTextArea description = new JTextArea("Zero pixel values if they do not exess threshold");
        description.setEditable(false);
        panel.add(description, new GUIStyler.ParamsGrid(panelX,panelY++));

        panel.add(thresholdJTextField, new GUIStyler.ParamsGrid(panelX,panelY++));

        JButton apply  = new JButton("Apply");
        panel.add(apply, new GUIStyler.ParamsGrid(panelX,panelY++));
        apply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Run();
            }
        });

        System.out.printf("Test input %d",Integer.getInteger(thresholdJTextField.getText()));
    }

    @Override
    public Operation Clone() {
        return new OperationThreshold(null);
    }


    public static BufferedImage thresholdPixelsFunction(BufferedImage srcImage, int threshold) {

        BufferedImage resultImg;


        System.out.println("Threshold " + threshold);


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


//        Random random = new Random();

        for(int ch=0; ch<channels; ++ch) {

            int shift = 0;
            int mask = 0x000000ff;

            for(int w=0; w<width; ++w) {
                for(int h=0; h<height; ++h) {
                    int colorStripe = srcImage.getRGB(w, h);
                    int level = ( colorStripe & mask ) >> shift;

                    //selecting new level

                    int newLevel;

                    if(level<threshold) { newLevel = 0; } else { newLevel = level; }
                    int newColorStripe = colorStripe & (~mask);

//                    if(h==0) { System.out.println("Oldstripe " + String.format("%x",colorStripe)); }
//                    if(h==0) { System.out.println("Newstripe " + String.format("%x",newColorStripe)); }


                    newColorStripe = newColorStripe | ( newLevel << shift );
//                    if(h==0) { System.out.println("Newstripe " + String.format("%x",newColorStripe)); }
                    resultImg.setRGB(w,h,newColorStripe);
                }
            }

            shift+=8;
            mask*=0x100;
        }

        return resultImg;
    }

}
