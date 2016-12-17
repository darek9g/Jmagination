import java.awt.image.BufferedImage;

/**
 * Created by darek on 16.12.2016.
 */
public interface RunOperation {
    public void RunBatch(Operation operation);
    public void RunInteractive(Operation operation);
    public void Save(Operation operation);
}
