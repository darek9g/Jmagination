import java.awt.image.BufferedImage;

/**
 * Created by darek on 16.12.2016.
 */
public interface RunOperation {
    public void Discard();
    public void Save(Operation operation);
    public GUIStyler.ImagePanel3 getImageContainer();
    public GUIStyler.ImagePanel3 getHistogramContainer();
}
