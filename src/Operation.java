import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by darek on 03.12.2016.
 */
public abstract class Operation {

    String label = "Dummy";
    ArrayList<String> categories = new ArrayList<>();

    public Operation() {
        categories.add("Wszystkie");
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

    public abstract BufferedImage RunOperation(BufferedImage bufferedImage);

    public abstract void drawConfigurationPanel(JPanel panel);

    public abstract Operation Clone();

}