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
public class OperationDuplicate extends Operations.Operation {

    JPanel configurationPanel;

    public OperationDuplicate(ImageServer srcImageServer, Jmagination jmagination) {
        super(srcImageServer, jmagination);
        this.label = "Duplicate";
        configurationPanel = buildConfigurationPanel();

    }

    @Override
    public BufferedImage RunOperation(ImageServer srcImageServer) {

        BufferedImage srcImage = srcImageServer.getImg();

        return duplicateImageFunction(srcImage);
    }

    @Override
    public JPanel getConfiguratorPanel() {
        return configurationPanel;
    }

    private JPanel buildConfigurationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_COLOR);
        JLabel title = new JLabel("Duplicate");

        int panelX = 0;
        int panelY = 0;

        panel.add(title, new GUIStyler.ParamsGrid(panelX,panelY++));

        JTextArea description = new JTextArea("Copies the image into new buffer");
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

    public static BufferedImage duplicateImageFunction(BufferedImage srcImage) {
        ColorModel cm = srcImage.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = srcImage.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

}
