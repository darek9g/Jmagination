package util;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Enumeration;

/**
 * @author dariusz.zaslona, created on 2017-01-25.
 */
public class JTableFilterMask extends JTable {

    int width;
    int maxValue = Integer.MAX_VALUE;
    int minValue = Integer.MIN_VALUE;
    Component parent;
    TableModelListener tableModelListener = new TableModelListener() {

        public void tableChanged(TableModelEvent e) {
            Object object = getValueAt(e.getFirstRow(), e.getColumn());
            // Ustawiając sparsowaną wartość listener wywołuje się kolejny raz, wiec w tym momecie przerywam jego działanie.
            if (object.getClass().equals(Integer.class))
                return;
            Integer selected = pharseValue(object);
            selected = checkValue(selected);
            setValueAt(selected, e.getFirstRow(), e.getColumn());
        }
    };

    public JTableFilterMask(int width) {
        super();
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.width = width;

    }

    public void fillMask (int size, int[][] data) {
        setSize(size, size);
        setModel(new DefaultTableModel(size, size));
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                setValueAt(new Integer(data[i][j]), i, j);
            }
        }
        getModel().addTableModelListener(tableModelListener);
        resizeTable(width);
    }

    public int[][] getMaskMatrix() {
        int[][] matrix = new int[getColumnCount()][getRowCount()];
        for (int i = 0; i < getColumnCount(); i++) {
            for (int j = 0; j < getRowCount(); j++) {
                matrix[i][j] = (Integer) getValueAt(i, j);
            }
        }
        return matrix;
    }

    protected void resizeTable(int maxValue) {
        int newWidth =  maxValue/getColumnCount();
        Enumeration<TableColumn> enumeration = getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
            enumeration.nextElement().setMaxWidth(newWidth);
        }
    }

    protected Integer checkValue(Integer object) {
        if(object > maxValue) {
            JOptionPane.showMessageDialog(this,
                    "Przekroczono wartość maksymalną.",
                    "Błąd danych wejściowych!",
                    JOptionPane.WARNING_MESSAGE);
            return maxValue;
        }
        if(object < minValue) {
            JOptionPane.showMessageDialog(this,
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
            JOptionPane.showMessageDialog(this,
                    "Dane wejściowe nie są liczbą.",
                    "Błąd danych wejściowych!",
                    JOptionPane.ERROR_MESSAGE);
            return 0;
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }
}
