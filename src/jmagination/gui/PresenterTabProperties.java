package jmagination.gui;

import jmagination.ConstantsInitializers;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by darek on 29.12.2016.
 */
public class PresenterTabProperties extends PresenterTab {

    BufferedImage img;

    JPanel controlsPanel;
    JPanel propertiesPanel;

    public PresenterTabProperties(BufferedImage img) {
        super();
        this.img = img;
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_COLOR);

        propertiesPanel = new JPanel();
        //parametersPanel.setLayout(new BoxLayout(parametersPanel,BoxLayout.LINE_AXIS));
        propertiesPanel.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_ALT_COLOR);

        drawProperties();

        add(controlsPanel);
        add(propertiesPanel);
    }

    private void drawProperties() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_ALT_COLOR);



        int panelX = 0;
        int panelY = 0;

        {
            JLabel label = new JLabel("Wysokość");
            panel.add(label, new GUIStyler.ParamsGrid(panelX++, panelY));

            JTextArea value = new JTextArea(String.valueOf(img.getHeight()));
            value.setEditable(false);
            panel.add(value, new GUIStyler.ParamsGrid(panelX, panelY++));
        }

        {
            panelX=0;
            JLabel label = new JLabel("Szerokość");
            panel.add(label, new GUIStyler.ParamsGrid(panelX++, panelY));

            String s = String.valueOf(img.getWidth());
            JTextArea value = new JTextArea(s);
            value.setEditable(false);
            panel.add(value, new GUIStyler.ParamsGrid(panelX, panelY++));
        }

        {
            panelX=0;
            JLabel label = new JLabel("Typ");
            panel.add(label, new GUIStyler.ParamsGrid(panelX++, panelY));

            int input = img.getColorModel().getColorSpace().getType();
            String s = "Typ przestrzeni kolorów";

            switch(input) {
                case java.awt.color.ColorSpace.CS_CIEXYZ:
                    s = "CS_CIEXYZ";
                    break;
                case java.awt.color.ColorSpace.CS_GRAY:
                    s = "CS_GRAY";
                    break;
                case java.awt.color.ColorSpace.CS_LINEAR_RGB:
                    s = "CS_LINEAR_RGB";
                    break;
                case java.awt.color.ColorSpace.CS_PYCC:
                    s = "CS_PYCC";
                    break;
                case java.awt.color.ColorSpace.CS_sRGB:
                    s = "CS_sRGB";
                    break;
                case java.awt.color.ColorSpace.TYPE_2CLR:
                    s = "2CLR";
                    break;
                case java.awt.color.ColorSpace.TYPE_3CLR:
                    s = "3CLR";
                    break;
                case java.awt.color.ColorSpace.TYPE_4CLR:
                    s = "4CLR";
                    break;
                case java.awt.color.ColorSpace.TYPE_5CLR:
                    s = "5CLR";
                    break;
                case java.awt.color.ColorSpace.TYPE_6CLR:
                    s = "6CLR";
                    break;
                case java.awt.color.ColorSpace.TYPE_7CLR:
                    s = "7CLR";
                    break;
                case java.awt.color.ColorSpace.TYPE_8CLR:
                    s = "8CLR";
                    break;
                case java.awt.color.ColorSpace.TYPE_9CLR:
                    s = "9CLR";
                    break;
                case java.awt.color.ColorSpace.TYPE_ACLR:
                    s = "ACLR";
                    break;
                case java.awt.color.ColorSpace.TYPE_BCLR:
                    s = "BCLR";
                    break;
                case java.awt.color.ColorSpace.TYPE_CCLR:
                    s = "CCLR";
                    break;
                case java.awt.color.ColorSpace.TYPE_CMY:
                    s = "CMY";
                    break;
                case java.awt.color.ColorSpace.TYPE_CMYK:
                    s = "CMYK";
                    break;
                case java.awt.color.ColorSpace.TYPE_DCLR:
                    s = "DCLR";
                    break;
                case java.awt.color.ColorSpace.TYPE_ECLR:
                    s = "ECLR";
                    break;
                case java.awt.color.ColorSpace.TYPE_FCLR:
                    s = "FCLR";
                    break;
                case java.awt.color.ColorSpace.TYPE_GRAY:
                    s = "GRAY";
                    break;
                case java.awt.color.ColorSpace.TYPE_HLS:
                    s = "HLS";
                    break;
                case java.awt.color.ColorSpace.TYPE_HSV:
                    s = "HSV";
                    break;
                case java.awt.color.ColorSpace.TYPE_Lab:
                    s = "Lab";
                    break;
                case java.awt.color.ColorSpace.TYPE_Luv:
                    s = "Luv";
                    break;
                case java.awt.color.ColorSpace.TYPE_RGB:
                    s = "RGB";
                    break;
                case java.awt.color.ColorSpace.TYPE_XYZ:
                    s = "XYZ";
                    break;
                case java.awt.color.ColorSpace.TYPE_YCbCr:
                    s = "YCbCr";
                    break;
                case java.awt.color.ColorSpace.TYPE_Yxy:
                    s = "Yxy";
                    break;
            }

            JTextArea value = new JTextArea(s);
            value.setEditable(false);
            panel.add(value, new GUIStyler.ParamsGrid(panelX, panelY++));
        }

        {
            panelX=0;
            JLabel label = new JLabel("Liczba komponentów koloru");
            panel.add(label, new GUIStyler.ParamsGrid(panelX++, panelY));

            int input = img.getColorModel().getColorSpace().getNumComponents();
            String s = String.valueOf(input);


            JTextArea value = new JTextArea(s);
            value.setEditable(false);
            panel.add(value, new GUIStyler.ParamsGrid(panelX, panelY++));
        }

        {
            panelX=0;
            JLabel label = new JLabel("Liczba kolorów");
            panel.add(label, new GUIStyler.ParamsGrid(panelX++, panelY));

            int input = img.getColorModel().getColorSpace().getNumComponents();
            String s = String.valueOf(input);

            JTextArea value = new JTextArea(s);
            value.setEditable(false);
            panel.add(value, new GUIStyler.ParamsGrid(panelX, panelY++));
        }


        propertiesPanel.add(panel);

    }
}
