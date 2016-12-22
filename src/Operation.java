import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by darek on 03.12.2016.
 */
public abstract class Operation {

    String label = "Dummy";
    ArrayList<String> categories = new ArrayList<>();
    boolean hsvModeAllowed = false;
    Operation that = this;

    RunOperation runOperation;


    // wspólne dla operacji elementy interfejsu

    GUIStyler.JButtonJM jButtonApply  = new GUIStyler.JButtonJM("Wykonaj");

    JLabel jLabelColorMode = new JLabel("Tryb");
    JRadioButton jRadioButtonColorModeHSV = new JRadioButton("HSV");
    JRadioButton jRadioButtonColorModeRGB = new JRadioButton("RGB");
    ButtonGroup buttonGroupColorMode = new ButtonGroup();

    ActionListener runOperationTrigger = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            jButtonApply.setEnabled(false);
            RunOperation(that);
        }
    };

    ChangeListener runOperationChangeTrigger = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            jButtonApply.setEnabled(false);
            RunOperation(that);
        }
    };

    public Operation() {
        categories.add("Wszystkie");
        jButtonApply.addActionListener(runOperationTrigger);
    }

    public ArrayList<String> getCategories () {
        return categories;
    }


    public void setRunOperation(RunOperation runOperation) {
        this.runOperation = runOperation;
    }

    public String getLabel() {
        return label;
    }

    public String toString() {
        return label;
    }

    public abstract BufferedImage RunOperationFunction(BufferedImage bufferedImage, Histogram histogram);

    public void RunOperation(Operation operation) {
        runOperation.runOperation(operation);
    };

    public abstract void drawConfigurationPanel(JPanel panel);

    public abstract Operation Clone();

    protected void configureColorModeControls() {

        jLabelColorMode.setHorizontalTextPosition(SwingConstants.RIGHT);
        jRadioButtonColorModeRGB.setSelected(true);

        buttonGroupColorMode.add(jRadioButtonColorModeRGB);
        buttonGroupColorMode.add(jRadioButtonColorModeHSV);

        if(hsvModeAllowed == false) {
            jRadioButtonColorModeHSV.setEnabled(false);
        }

    }

}
