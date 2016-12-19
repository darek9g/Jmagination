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
public class OperationDuplicate extends Operation {

    Parameters parameters;

    public OperationDuplicate(ImageServer srcImageServer) {
        super();
        this.label = "Zduplikuj";
        categories.add("LAB 1");
        categories.add("Ogólne");

        parameters = new Parameters();
    }

    @Override
    public BufferedImage RunOperationFunction(BufferedImage bufferedImage, Histogram histogram) {
        return bufferedImage;
    }

    @Override
    public void drawConfigurationPanel(JPanel panel) {
        panel.setLayout(new GridBagLayout());
        panel.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_COLOR);
        JLabel title = new JLabel("Zduplikuj");

        int panelX = 0;
        int panelY = 0;

        panel.add(title, new GUIStyler.ParamsGrid(panelX,panelY++));

        JTextArea description = new JTextArea("Kopiuje obraz do nowego bufora");
        description.setEditable(false);
        panel.add(description, new GUIStyler.ParamsGrid(panelX,panelY++));

        panel.add(jButtonApply, new GUIStyler.ParamsGrid(panelX,panelY++));
    }

    @Override
    public Operation Clone() {
        return new OperationDuplicate(null);
    }

    public static BufferedImage duplicateImageFunction(BufferedImage srcImage) {
        ColorModel cm = srcImage.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = srcImage.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private class Parameters {
        int threshold = 128;

        public Parameters() {}
    }

}
