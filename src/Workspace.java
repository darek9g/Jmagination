import javax.swing.*;

/**
 * Created by darek on 19.11.2016.
 */
public class Workspace {

    JFrame window;
    ImageServer srcImageServer;

    public Workspace() {
        window = new JFrame("Workspace");
        window.setPreferredSize(ConstantsInitializers.GUI_IMAGEWINDOW_SIZE);
        window.setMinimumSize(ConstantsInitializers.GUI_IMAGEWINDOW_SIZE);
        window.setDefaultCloseOperation((JFrame.EXIT_ON_CLOSE));

        JPanel panel = new JPanel();
        window.setContentPane(panel);

        panel.setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);


        window.setVisible(true);
    }

}
