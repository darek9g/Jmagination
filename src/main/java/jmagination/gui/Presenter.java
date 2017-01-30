package jmagination.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by darek on 29.12.2016.
 */
public class Presenter extends JPanel {

    private JTabbedPane jTabbedPane;

    public Presenter() {
        super(new GridLayout(1,1));

        jTabbedPane = new JTabbedPane();
        add(jTabbedPane);

    }

    public void addTab(String s, JComponent jComponent) {
        jTabbedPane.addTab(s, jComponent);
    }

    public void remTab(JComponent jComponent) {

    }

}