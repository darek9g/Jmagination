package jmagination.operations;

import jmagination.guitools.LineEditor;

import static jmagination.ConstantsInitializers.BR;

/**
 * Created by darek on 06.01.2017.
 */
public class OperationLUTStretchInRanges extends OperationLUTPointOperation {

    {

        label = "Rozciągaj zakresy wartości";
        header = "Rozciąganie zakresami do zakresu 0 - maksimum";
        description = "Skalowanie wartości z wybranych zakresów do zakresu <0-maks>." + BR + "Ustawienie minimalnej jasności pikselom spoza" + BR + "określonych zakresów jasności.";

        parameters = new Parameters(256);

        thresholdLineEditor = new LineEditor(LineEditor.STRECH_MODE, 0, 255, 0, 255);
        thresholdLineEditor.addActionListener(runOperationTrigger);

    }

    @Override
    public Operation Clone() {
        return new OperationLUTStretchInRanges();
    }
}
