package jmagination.gui;

import jmagination.ConstantsInitializers;

import javax.swing.*;
import java.awt.*;

/**
 * Created by darek on 29.12.2016.
 */
public class PresenterTab extends JPanel {

    public PresenterTab() {
        super();
        setBackground(ConstantsInitializers.GUI_CONTROLS_BG_COLOR);
    }

    public PresenterTab(Dimension dimension) {
        this();
//            setMinimumSize(dimension);
//            setPreferredSize(dimension);
    }
}