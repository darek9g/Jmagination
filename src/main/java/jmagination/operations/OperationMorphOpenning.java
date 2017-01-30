package jmagination.operations;

import jmagination.histogram.Histogram;
import jmagination.util.SimpleHSVBufferedImage;

import static jmagination.ConstantsInitializers.BR;

/**
 * Created by darek on 30.11.2016.
 */

public class OperationMorphOpenning extends OperationErosion {

    {
        label = "Otwarcie";
        header = "Otwarcie";
        description = "Aplikuje sekwencję operacji erozji i otwarcia";

        parameters = new Parameters();
        parameters.promotingBrighter = false;
    }

    @Override
    public Operation Clone() {
        return new OperationMorphOpenning();
    }

    @Override
    public SimpleHSVBufferedImage morphFunctionInterface(SimpleHSVBufferedImage inImage, Histogram histogram) {
        SimpleHSVBufferedImage afterErosion = morphFunction(inImage, parameters.promotingBrighter);
        return morphFunction(afterErosion, !parameters.promotingBrighter);
    }

}
