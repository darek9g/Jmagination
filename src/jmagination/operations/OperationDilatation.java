package jmagination.operations;

import static jmagination.ConstantsInitializers.BR;

/**
 * Created by darek on 30.11.2016.
 */

public class OperationDilatation extends OperationErosion {

    {
        label = "Dylatacja";
        header = "Dylatacja";
        description = "Foo" + BR + "bar.";

        parameters = new Parameters();
        parameters.promotingBrighter = true;

    }

    @Override
    public Operation Clone() {
        return new OperationDilatation();
    }

}