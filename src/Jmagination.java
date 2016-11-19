import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by darek on 25.10.16.
 */
public class Jmagination extends JComponent {

    // test branching

    JFrame window;
    ArrayList<ImageServer> images;
    GUIStyler.JButtonS jButtonForNewImage;


    public Jmagination() {
        images = new ArrayList<>();
        window = new JFrame("Jmagination");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
        window.setContentPane(panel);

        jButtonForNewImage = new GUIStyler.JButtonS("New buffer");

        Jmagination selfHandle = this;

        jButtonForNewImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser chooser = new JFileChooser();
                chooser.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_COLOR);
                chooser.setCurrentDirectory(new File("C:\\Users\\" + System.getProperty("user.name") + "\\Pictures"));
                int chooserResult = chooser.showOpenDialog(Jmagination.this);
                if(chooserResult == JFileChooser.APPROVE_OPTION) {

                    BufferedImage loaded = ImageServer.LoadImageFromFile(chooser.getSelectedFile().getAbsolutePath());

                    if(loaded!=null) {
                        addImage(new ImageServer(loaded, chooser.getSelectedFile().getAbsolutePath(), selfHandle));
                    }
                }
            }
        });

        window.getContentPane().add(jButtonForNewImage);

        window.setResizable(false);
        window.setAlwaysOnTop(false);

        window.pack();

        window.setLocation(MouseInfo.getPointerInfo().getLocation());


        window.setVisible(true);

    }

    public void addImage(ImageServer imageServer) {
        images.add(imageServer);
        window.getContentPane().add(imageServer.getCallUpButton());
        System.out.println("Button text: " + imageServer.getCallUpButton().getText());
        window.pack();
        repaint();
    }

    public void removeImage(ImageServer imageServer) {
        images.remove(imageServer);
        window.getContentPane().remove(imageServer.getCallUpButton());
        repaint();
    }


    public static void main(String[] args) {
        Jmagination j_obj = new Jmagination();
    }

}
