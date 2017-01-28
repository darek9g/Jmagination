package jmagination.operations;

import jmagination.histogram.Histogram;
import util.SimpleHSVBufferedImage;

import static jmagination.ConstantsInitializers.BR;

/**
 * Created by darek on 30.11.2016.
 */

public class OperationDilatation extends OperationErosion {

    {
        label = "Dylatacja";
        header = "Dylatacja";
        description = "Nadaje pikselowa wartość maksymalną z otoczenia.";

        parameters = new Parameters();
        parameters.promotingBrighter = true;

    }

    @Override
    public Operation Clone() {
        return new OperationDilatation();
    }

}