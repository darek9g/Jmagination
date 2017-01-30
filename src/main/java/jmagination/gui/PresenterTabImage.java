package jmagination.gui;

import jmagination.ConstantsInitializers;
import jmagination.util.SimpleHSVBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Created by darek on 29.12.2016.
 */
public class PresenterTabImage extends PresenterTab {

    ImagePanel imagePanel;
    JPanel controlsPanel;

    static final String CMD_CE = "Wyśrodkuj";
    static final String CMD_TL = "Górny-lewy róg";
    static final String CMD_TR = "Górny-prawy róg";
    static final String CMD_BL = "Dolny-lewy róg";
    static final String CMD_BR = "Dolny-prawy róg";
    static final String CMD_AT = "Względem góry";
    static final String CMD_AB = "Wzgledem dołu";
    static final String CMD_AL = "Względem lewej krawędzi";
    static final String CMD_AR = "Względem prawej krawędzi";

    static final String[] commands = { CMD_TL, CMD_TR, CMD_CE, CMD_BL, CMD_BR, CMD_AT, CMD_AB, CMD_AL, CMD_AR };


    public PresenterTabImage(BufferedImage img) {
        super();
        setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));

        imagePanel = new ImagePanel(img);
        imagePanel.setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);

        controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel,BoxLayout.Y_AXIS));
        controlsPanel.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_COLOR);
        drawControls();

        add(controlsPanel);
        add(imagePanel);
    }

    public void setImage(BufferedImage bufferedImage) {
        imagePanel.setImage(bufferedImage);
    }

    public BufferedImage getImage() {
        return imagePanel.getImage();
    }

    public PresenterTabImage(BufferedImage img, Dimension dimension) {
        this(img);
        imagePanel.setPreferredSize(dimension);
        imagePanel.setMinimumSize(dimension);
    }

    private void drawControls() {

        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUIStyler.JButtonJM jb = (GUIStyler.JButtonJM)e.getSource();
                imagePanel.setOriginCmd(jb.getText());
            }
        };


        for (String s: commands) {
            GUIStyler.JButtonJM jbCmd = new GUIStyler.JButtonJM(s);
            jbCmd.setMinimumSize(ConstantsInitializers.GUI_BUTTON_SIZE_LONG);
            jbCmd.setMaximumSize(ConstantsInitializers.GUI_BUTTON_SIZE_LONG);
            jbCmd.setPreferredSize(ConstantsInitializers.GUI_BUTTON_SIZE_LONG);
            jbCmd.setOpaque(true);
            controlsPanel.add(jbCmd);

            jbCmd.addActionListener(al);
        }
    }

}
