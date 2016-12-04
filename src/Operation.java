import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by darek on 03.12.2016.
 */
public abstract class Operation {

    ImageServer srcImageServer;
    String label = "Dummy";
    Jmagination jmagination;
    ArrayList<String> categories = new ArrayList<>();

    public Operation(Jmagination jmagination) {
        this.jmagination = jmagination;
        categories.add("ALL");
    }

    public ArrayList<String> getCategories () {
        return categories;
    }

    public Operation(ImageServer srcImageServer, Jmagination jmagination) {
        this(jmagination);
        this.srcImageServer = srcImageServer;
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
        jmagination.addImage(srcImageServer.createChildImageServer(RunOperation(srcImageServer)));
    }

    public abstract Operation Clone();

}