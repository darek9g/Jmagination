package jmagination.operations;

import jmagination.RunOperation;
import jmagination.gui.GUIStyler;
import jmagination.histogram.Histogram;
import util.SimpleHSVBufferedImage;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static jmagination.ConstantsInitializers.BR;

/**
 * Created by darek on 03.12.2016.
 */
public abstract class Operation {

    String label = "Dummy";
    String header = "Dummy";
    String description = "Foo" + BR + "Bar";
    ArrayList<String> categories = new ArrayList<>();
    boolean hsvModeAllowed = false;
    boolean hsvSpecificModeAllowed = false;
    Operation that = this;

    RunOperation runOperation;


    // wspólne dla operacji elementy interfejsu

    public GUIStyler.JButtonJM jButtonApply  = new GUIStyler.JButtonJM("Wykonaj");

    JLabel jLabelColorMode = new JLabel("Tryb");
    JRadioButton jRadioButtonColorModeHSV = new JRadioButton("HSV");
    JRadioButton jRadioButtonColorModeRGB = new JRadioButton("RGB");
    JLabel jLabelHSVComponentsSelet = new JLabel("Składowe:");
    JCheckBox jCheckBoxHue = new JCheckBox("Odcień(H)");
    JCheckBox jCheckBoxSaturation = new JCheckBox("Nasycenie(S)");
    JCheckBox jCheckBoxValue = new JCheckBox("Jasność(V)");
    ButtonGroup buttonGroupColorMode = new ButtonGroup();

    ActionListener runOperationTrigger = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            jButtonApply.setEnabled(false);
            RunOperation(that);
        }
    };

    ChangeListener hsvSpecificModeAllowedTrigger = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            JRadioButton hsvRadio = (JRadioButton)e.getSource();
            if (hsvRadio.isSelected()){
                jCheckBoxHue.setVisible(true);
                jCheckBoxSaturation.setVisible(true);
                jCheckBoxValue.setVisible(true);
                jLabelHSVComponentsSelet.setVisible(true);
            } else {
                jCheckBoxHue.setVisible(false);
                jCheckBoxSaturation.setVisible(false);
                jCheckBoxValue.setVisible(false);
                jLabelHSVComponentsSelet.setVisible(false);
            }
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
        jCheckBoxHue.setVisible(false);
        jCheckBoxSaturation.setVisible(false);
        jCheckBoxValue.setVisible(false);
        jLabelHSVComponentsSelet.setVisible(false);
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

    public abstract SimpleHSVBufferedImage RunOperationFunction(SimpleHSVBufferedImage bufferedImage, Histogram histogram);

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
        if(hsvSpecificModeAllowed == true) {
            jRadioButtonColorModeHSV.addChangeListener(hsvSpecificModeAllowedTrigger);
        }

    }

}
