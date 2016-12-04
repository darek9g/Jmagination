import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by darek on 03.12.2016.
 */
public abstract class Operation {

    ImageServer srcImageServer;
    String label = "Dummy";
    ArrayList<String> categories = new ArrayList<>();

    public Operation(ImageServer srcImageServer) {
        categories.add("ALL");
        this.srcImageServer = srcImageServer;
    }

    public ArrayList<String> getCategories () {
        return categories;
    }


    public String getLabel() {
        return label;
    }

    public String toString() {
        return label;
    }

    public abstract BufferedImage RunOperation(ImageServer srcImageServer);

    public abstract void drawConfigurationPanel(JPanel panel);

    public void Run() {
        srcImageServer.createChildImageServer(RunOperation(srcImageServer));
    }

    public abstract Operation Clone();

}