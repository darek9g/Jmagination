import slider.RangeSlider;

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
    JLabel thresholdRangeLowJLabel;
    JLabel thresholdRangeHighJLabel;
    RangeSlider thresholdJRangeSlider;


    public OperationThreshold(ImageServer srcImageServer) {
        super();
        this.label = "Progowanie";
        categories.add("LAB 2");
        categories.add("Punktowe jednoargumentowe");

        parameters = new Parameters();

        thresholdRangeLowJLabel = new JLabel(String.valueOf(parameters.thresholdRangeLow));
        thresholdRangeHighJLabel = new JLabel(String.valueOf(parameters.thresholdRangeHigh));
        thresholdJRangeSlider = new RangeSlider();
        thresholdJRangeSlider.setMinimum(0);
        thresholdJRangeSlider.setMaximum(255);
        thresholdJRangeSlider.setValue(0);
        thresholdJRangeSlider.setUpperValue(255);
    }

    @Override
    public BufferedImage RunOperationFunction(BufferedImage bufferedImage, Histogram histogram) {

        /*String thresholdStr = thresholdJTextField.getText();
        Integer.parseInt(thresholdStr);*/
        parameters.thresholdRangeLow = thresholdJRangeSlider.getValue();
        parameters.thresholdRangeHigh = thresholdJRangeSlider.getUpperValue();
        thresholdRangeLowJLabel.setText(String.valueOf(parameters.thresholdRangeLow));
        thresholdRangeHighJLabel.setText(String.valueOf(parameters.thresholdRangeHigh));
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

        panel.add(thresholdRangeLowJLabel, new GUIStyler.ParamsGrid(panelX,panelY));
        panel.add(thresholdRangeHighJLabel, new GUIStyler.ParamsGrid(panelX + 1, panelY++));

        thresholdJRangeSlider.addChangeListener(runOperationChangeTrigger);
        panel.add(thresholdJRangeSlider, new GUIStyler.ParamsGrid(panelX, panelY++));

        panel.add(jButtonApply, new GUIStyler.ParamsGrid(panelX,panelY++));

    }

    @Override
    public Operation Clone() {
        return new OperationThreshold(null);
    }


    public BufferedImage thresholdPixelsFunction(BufferedImage srcImage) {

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

                    if(level<parameters.thresholdRangeLow) {
                        newLevel = 0;
                    } else {
                        if (level>parameters.thresholdRangeHigh) { newLevel = 0;} else {
                            newLevel = level;
                        }
                    }
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
        int thresholdRangeLow = 0;
        int thresholdRangeHigh = 255;

        public Parameters() {}
    }
}
