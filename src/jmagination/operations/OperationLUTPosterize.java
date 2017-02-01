package jmagination.operations;

import jmagination.guitools.LineEditor;

import static jmagination.ConstantsInitializers.BR;

/**
 * Created by darek on 06.01.2017.
 */
public class OperationLUTPosterize extends OperationLUTPointOperation {

    {

        label = "Redukuj liczbę poziomów jasności";
        header = "Redukcja liczby poziomów jasności";
        description = "Skalowanie wartości jasności w dół" + BR + "według prostej lini skalowania.";

        parameters = new Parameters(256);

        thresholdLineEditor = new LineEditor(LineEditor.POSTER_MODE, 0, 255, 0, 255);
        thresholdLineEditor.addActionListener(runOperationTrigger);

    }

    @Override
    public Operation Clone() {
        return new OperationLUTPosterize();
    }
}
