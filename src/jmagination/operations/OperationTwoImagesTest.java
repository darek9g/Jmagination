package jmagination.operations;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by darek on 06.01.2017.
 */
public class OperationTwoImagesTest extends OperationTwoImagesAbstract {

    {
        label = "Dummy - dwuobrazowa";
        header = "Dummy - oparacja arytmetyczna lub logiczna";
        description = "Dummy - wyznaczanie wartości według na odpowiadających sobie pikesli z dwóch obrazów.";

        parameters = new Parameters();

    }

    @Override
    public Operation Clone() {
        return null;
    }

    @Override
    public BufferedImage twoImagesFunction(BufferedImage srcImage) {
        return null;
    }

}
