package jmagination.operations;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.util.Enumeration;

/**
 * Created by Rideau on 2017-01-23.
 */
public abstract class OperationWithMask extends Operation {

    JTable jTableMask = null;
    JLabel jLabelMaskaFiltru = new JLabel("Maska filtru:");
    int maxValue = Integer.MAX_VALUE;
    int minValue = Integer.MIN_VALUE;

    protected void fillMask (int size, int[][] data) {
        if (jTableMask == null) {
            jTableMask = new JTable();
        }
        jTableMask.setSize(size, size);
        jTableMask.setModel(new DefaultTableModel(size, size));
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                jTableMask.setValueAt(new Integer(data[i][j]), i, j);
            }
        }
        jTableMask.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableMask.getModel().addTableModelListener(tableModelListener);
        resizeTable(380);
    }

    protected void resizeTable(int maxValue) {
        int newWidth =  maxValue/jTableMask.getColumnCount();
        Enumeration<TableColumn> enumeration = jTableMask.getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
            enumeration.nextElement().setMaxWidth(newWidth);
        }
    }

    protected int[][] getMaskMatrix() {
        int[][] matrix = new int[jTableMask.getColumnCount()][jTableMask.getRowCount()];
        for (int i = 0; i < jTableMask.getColumnCount(); i++) {
            for (int j = 0; j < jTableMask.getRowCount(); j++) {
                matrix[i][j] = (Integer) jTableMask.getValueAt(i, j);
            }
        }
        return matrix;
    }

    protected Integer checkValue(Integer object) {
        if(object > maxValue) {
            JOptionPane.showMessageDialog(runOperation.getMainWindow(),
                    "Przekroczono wartość maksymalną.",
                    "Błąd danych wejściowych!",
                    JOptionPane.WARNING_MESSAGE);
            return maxValue;
        }
        if(object < minValue) {
            JOptionPane.showMessageDialog(runOperation.getMainWindow(),
                    "Przekroczono wartość minimalną.",
                    "Błąd danych wejściowych!",
                    JOptionPane.WARNING_MESSAGE);
            return minValue;
        }
        return object;
    }

    protected Integer pharseValue(Object object) {
        try {
            return Integer.parseInt(String.valueOf(object));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(runOperation.getMainWindow(),
                    "Dane wejściowe nie są liczbą.",
                    "Błąd danych wejściowych!",
                    JOptionPane.ERROR_MESSAGE);
            return 0;
        }
    }

    TableModelListener tableModelListener = new TableModelListener() {

        public void tableChanged(TableModelEvent e) {
            Object object = jTableMask.getValueAt(e.getFirstRow(), e.getColumn());
            // Ustawiając sparsowaną wartość listener wywołuje się kolejny raz, wiec w tym momecie przerywam jego działanie.
            if (object.getClass().equals(Integer.class))
                return;
            Integer selected = pharseValue(object);
            selected = checkValue(selected);
            jTableMask.setValueAt(selected, e.getFirstRow(), e.getColumn());
        }
    };
}
