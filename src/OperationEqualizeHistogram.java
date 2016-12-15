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

public class OperationEqualizeHistogram extends Operation {

    Parameters parameters;

    public OperationEqualizeHistogram(ImageServer srcImageServer) {
        super();
        this.label = "Rozciąganie histogramu";
        categories.add("LAB 1");
        categories.add("Wielopunktowe");

        parameters = new Parameters();
    }

    @Override
    public BufferedImage RunOperation(BufferedImage bufferedImage) {

        Histogram histogram = new Histogram(bufferedImage);
        return equalizeHistogramFunction(bufferedImage, histogram);


    }

    @Override
    public void drawConfigurationPanel(JPanel panel) {
        panel.setLayout(new GridBagLayout());
        panel.setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);
        JLabel title = new JLabel("Rozciąganie histogramu");

        int panelX = 0;
        int panelY = 0;

        panel.add(title, new GUIStyler.ParamsGrid(panelX,panelY++));

        JTextArea description = new JTextArea("Opis - UZUPEŁNIĆ");
        description.setEditable(false);
        panel.add(description, new GUIStyler.ParamsGrid(panelX,panelY++));

/*        JButton apply  = new JButton("Wykonaj");
        panel.add(apply, new GUIStyler.ParamsGrid(panelX,panelY++));
        apply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Run();
            }
        });*/
    }

    @Override
    public Operation Clone() {
        return new OperationEqualizeHistogram(null);
    }

    public static BufferedImage equalizeHistogramFunction(BufferedImage srcImage, Histogram histogram) {

        BufferedImage resultImg;

        {
            ColorModel cm = srcImage.getColorModel();
            boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
            WritableRaster raster = srcImage.copyData(null);
            resultImg = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
        }

        int width = srcImage.getWidth();
        int height = srcImage.getHeight();

        int pixels = width * height;

        int channels = histogram.getData().size();
        int levels = histogram.getData().get(0).length;

        ArrayList<Integer[]> intergatedHistogramData = new ArrayList<>(channels);
        for(int ch=0; ch<channels; ++ch) {
            Integer[] channelSrc =  histogram.getData().get(ch);
            Integer[] channelData = new Integer[levels];
            intergatedHistogramData.add(channelData);

            if(channelData.length>0) channelData[0] = channelSrc[0];
            for(int level=1; level<levels; ++level) {
                channelData[level] = channelData[level-1] + channelSrc[level];
            }

            for(int level=0; level<levels; ++level) {
                channelData[level] = (int) Math.floor( (levels - 1) * ( channelData[level] + 0.0 ) / pixels);
//                System.out.printf("Level %d value = %d\n", level, (int) channelData[level]);
            }


        }

        //applying changes

        for(int w=0; w<width; ++w) {
            for(int h=0; h<height; ++h) {
                int colorStripe = srcImage.getRGB(w, h);
                int newColorStripe = colorStripe & 0xff000000;

                int shift = 0;
                int mask = 0x000000ff;
                for(int ch=0; ch<channels; ++ch) {
                    int level = ( colorStripe & mask ) >> shift;

                    //setting new level
                    int newLevel = intergatedHistogramData.get(ch)[level];

//                  System.out.printf("Old level=>New Level %d=>%d\n",level, newLevel);

                    newColorStripe = newColorStripe | ( newLevel << shift );
                    shift+=8;
                    mask*=0x100;
                }
                resultImg.setRGB(w,h,newColorStripe);
            }
        }

        return resultImg;
    }

    private class Parameters {
        int threshold = 128;

        public Parameters() {}
    }

}
