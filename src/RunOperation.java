import java.awt.image.BufferedImage;

/**
 * Created by darek on 16.12.2016.
 */
public interface RunOperation {
    public void setupOperation(Operation operation);
    public void postOperation();
    public void discardOperation();
    public void saveOperationsOutput(Operation operation);
    public GUIStyler.ImagePanel3 getImageContainer();
    public GUIStyler.ImagePanel3 getHistogramContainer();
    public boolean getChanged();
    public boolean getReplaced();
}
