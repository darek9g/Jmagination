import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;


/**
 * Created by darek on 30.11.2016.
 */

public class OperationThreshold extends Operation {

    Parameters parameters;
    JTextField thresholdJTextField;


    public OperationThreshold(ImageServer srcImageServer) {
        super();
        this.label = "Progowanie";
        categories.add("LAB 2");
        categories.add("Punktowe jednoargumentowe");

        parameters = new Parameters();

        thresholdJTextField = new JTextField(String.valueOf(parameters.threshold));
    }

    @Override
    public BufferedImage RunOperationFunction(BufferedImage bufferedImage, Histogram histogram) {

        String thresholdStr = thresholdJTextField.getText();
        parameters.threshold = Integer.parseInt(thresholdStr);
        return thresholdPixelsFunction(bufferedImage);
    }

    @Override
    public void drawConfigurationPanel(JPanel panel) {
        panel.setLayout(new GridBagLayout());
        panel.setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);
        JLabel title = new JLabel("Progowanie");

        int panelX = 0;
        int panelY = 0;

        panel.add(title, new GUIStyler.ParamsGrid(panelX,panelY++));

        JTextArea description = new JTextArea("Zero pixel values if they do not exess threshold");
        description.setEditable(false);
        panel.add(description, new GUIStyler.ParamsGrid(panelX,panelY++));

        thresholdJTextField.addActionListener(runOperationTrigger);
        panel.add(thresholdJTextField, new GUIStyler.ParamsGrid(panelX,panelY++));

        panel.add(jButtonApply, new GUIStyler.ParamsGrid(panelX,panelY++));

    }

    @Override
    public Operation Clone() {
        return new OperationThreshold(null);
    }


    public BufferedImage thresholdPixelsFunction(BufferedImage srcImage) {

        BufferedImage resultImg;


        System.out.println("Threshold " + parameters.threshold);


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

        int shift = 0;
        int mask = 0x000000ff;

        for(int ch=0; ch<channels; ++ch) {


            for(int w=0; w<width; ++w) {
                for(int h=0; h<height; ++h) {
                    int colorStripe = srcImage.getRGB(w, h);
                    int level = ( colorStripe & mask ) >> shift;

                    //selecting new level

                    int newLevel;

                    if(level<parameters.threshold) { newLevel = 0; } else { newLevel = level; }
                    int newColorStripe = colorStripe & (~mask);

                    newColorStripe = newColorStripe | ( newLevel << shift );
                    resultImg.setRGB(w,h,newColorStripe);
                }
            }

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
