package jmagination.operations;

import jmagination.guitools.LineEditor;

import static jmagination.ConstantsInitializers.BR;

/**
 * Created by darek on 06.01.2017.
 */
public class OperationLUTScale extends OperationLUTPointOperation {

    {

        label = "Liniowa operacja punktowa";
        header = "Skalowanie wartości piksel";
        description = "Skalowanie wartości jasności" + BR + "według prostej lini skalowania.";

        parameters = new Parameters(256);

        thresholdLineEditor = new LineEditor(LineEditor.SCALE_MODE, 0, 255, 0, 255);
        thresholdLineEditor.addActionListener(runOperationTrigger);

    }


    @Override
    public Operation Clone() {
        return new OperationLUTScale();
    }
}
