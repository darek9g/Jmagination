package jmagination.gui;

import jmagination.ConstantsInitializers;
import jmagination.RunOperation;
import jmagination.operations.Operation;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by darek on 27.10.16.
 */
public class GUIStyler {

    public static class JButtonJM extends JButton {

        public JButtonJM() {
            super();
            setDefaults();
        }

        public JButtonJM(String s) {
            super(s);
            setDefaults();
        }

        private void setDefaults() {

            setFont(ConstantsInitializers.GUI_SMALL_FONT);

//            setPreferredSize(jmagination.ConstantsInitializers.GUI_BUTTON_SIZE_SHORT);
            setMinimumSize(ConstantsInitializers.GUI_BUTTON_SIZE_SHORT);

        }

    }

    public static class JSeparatorJM extends JSeparator {

        private final Dimension dimension = new Dimension(200,10);

        public JSeparatorJM() {
            super();
            setDefaults();
        }

        private void setDefaults() {
//            setPreferredSize(dimension);
            setMinimumSize(dimension);
        }
    }

    public static class ParamsGrid extends GridBagConstraints {

        public ParamsGrid() {
            super();
            setDefaults();
        }

        public ParamsGrid(int gridx, int gridy) {
            this();

            this.gridx = gridx;
            this.gridy = gridy;
        }

        public ParamsGrid(int gridx, int gridy, int anchor) {
            this(gridx, gridy);

            this.anchor = anchor;
        }

        public ParamsGrid(int gridx, int gridy, int gridwidth, int gridheight) {
            this();

            this.gridx = gridx;
            this.gridy = gridy;

            this.gridwidth = gridwidth;
            this.gridheight = gridheight;
        }

        public ParamsGrid(int gridx, int gridy, int gridwidth, int gridheight, int anchor) {
            this(gridx, gridy, gridwidth, gridheight);

            this.anchor = anchor;
        }

        public ParamsGrid(int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty, int anchor, int fill, Insets insets, int ipadx, int ipady) {
            super(gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets, ipadx, ipady);
        }

        private void setDefaults() {

            this.gridx = 0;
            this.gridy = 0;
            this.gridwidth = 1;
            this.gridheight = 1;
            this.weightx = 1.0d;
            this.weighty = 1.0d;
            this.anchor = BASELINE;
            this.fill = 0;
            this.insets = new Insets(2, 2, 2, 2);
            this.ipadx = 1;
            this.ipady = 1;
        }
    }

    private GUIStyler() {
        throw new AssertionError();
    }

}
