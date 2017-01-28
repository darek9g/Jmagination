package jmagination.operations;

import jmagination.histogram.Histogram;
import util.SimpleHSVBufferedImage;

import static jmagination.ConstantsInitializers.BR;

/**
 * Created by darek on 30.11.2016.
 */

public class OperationMorphClosing extends OperationMorphOpenning {

    {
        label = "Zamknięcie";
        header = "Zamknięcie";
        description = "Aplikuje sekwencję operacji otwarcia i zamknięcia";

        parameters = new Parameters();
        parameters.promotingBrighter = true;
    }

    @Override
    public Operation Clone() {
        return new OperationMorphClosing();
    }

}