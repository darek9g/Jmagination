import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Created by darek on 30.11.2016.
 */

public class OperationConvertToGray extends Operations.Operation {

    JPanel configurationPanel;

    public OperationConvertToGray(ImageServer srcImageServer, Jmagination jmagination) {
        super(srcImageServer, jmagination);
        this.label = "Convert to gray";
        configurationPanel = buildConfigurationPanel();

    }

    @Override
    public BufferedImage RunOperation(ImageServer srcImageServer) {
        BufferedImage srcImage = srcImageServer.getImg();
        return convertToGrayFunction(srcImage);
    }

    @Override
    public JPanel getConfiguratorPanel() {
        return configurationPanel;
    }

    private JPanel buildConfigurationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_COLOR);

        int panelX = 0;
        int panelY = 0;

        JLabel title = new JLabel("Convert to gray");
        panel.add(title, new GUIStyler.ParamsGrid(panelX,panelY++));

        JTextArea description = new JTextArea("Combines each pixel color values into grey level");
        description.setEditable(false);
        panel.add(description, new GUIStyler.ParamsGrid(panelX,panelY++));

        JButton apply  = new JButton("Apply");
        panel.add(apply, new GUIStyler.ParamsGrid(panelX,panelY++));
        apply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Run();
            }
        });

        return panel;
    }

    public static BufferedImage convertToGrayFunction(BufferedImage colorImg) {

        BufferedImage grayImg = new BufferedImage(colorImg.getWidth(), colorImg.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        int channels = colorImg.getColorModel().getNumColorComponents();

        for(int x=0; x<colorImg.getWidth(); ++x) {
            for(int y=0;y<colorImg.getHeight(); ++y) {

                int colorStripe = colorImg.getRGB(x,y);
                int grayStripe = 0xff000000 & colorStripe;
                int grayLevel = 0;

                {
                    int shift = 0;
                    int mask = 0x000000ff;
                    for (int ch = 0; ch < channels; ++ch) {
                        int level = (colorStripe & mask) >> shift;
                        //setting new level
                        grayLevel += level;

                        shift += 8;
                        mask *= 0x100;
                    }
                    grayLevel = (int) Math.round(grayLevel / (channels + 0.0));
                }

                {
                    int shift = 0;
                    for (int ch = 0; ch < channels; ++ch) {
                        int level = grayLevel << shift;
                        //setting new level
                        grayStripe = grayStripe | level;

                        shift += 8;
                    }
                }

                grayImg.setRGB(x,y,grayStripe);

            }
        }

        return grayImg;
    }
}