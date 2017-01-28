package jmagination.gui;

import javax.swing.*;

import java.awt.*;

import static jmagination.ConstantsInitializers.BR;

/**
 * Created by darek on 27.01.2017.
 */
public class AboutPanel extends JPanel {

    JTextArea jTextArea;

    String description = "Jmagination" + BR +
            "Aplikacja przetwarzania obrazów." + BR +
            "Projekt studencki kursu:" + BR +
            "\"Algorytmy przetwarzania obrazów\"" + BR +
            "Nazwa projektowa:" + BR +
            "GutZaslonaFiltColor16_17" + BR + BR +
            "Autor: Dariusz Gut " + BR +
            "Autor: Dariusz Zasłona" + BR +
            "Prowadzący: dr inż. Marek Doros" + BR +
            "Wyższa Szkoła Informatyki " + BR + "Stosowanej i Zarządzania" + BR + "Warszawa 2016/2017";

    public AboutPanel() {
        super();

        jTextArea = new JTextArea(description);
        Font font = jTextArea.getFont();
        jTextArea.setFont(new Font(font.getName(), font.getStyle(), 12));
        jTextArea.setEditable(false);
        add(jTextArea);


    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        jTextArea.setPreferredSize(preferredSize);
    }
    @Override
    public void setMinimumSize(Dimension minimumSize) {
        super.setMinimumSize(minimumSize);
        jTextArea.setPreferredSize(minimumSize);
    }

}



