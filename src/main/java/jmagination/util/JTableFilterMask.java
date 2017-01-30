package jmagination.util;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * @author dariusz.zaslona, created on 2017-01-25.
 */
public class JTableFilterMask extends JTable {

    int width;
    int maxValue = Integer.MAX_VALUE;
    int minValue = Integer.MIN_VALUE;
    public boolean allowNonZeroSum = false;

    public enum EdgeMode {
        NO_CHANGE("Wartości brzegowe bez zmian", 0),
        REPLICATE("Powielenie wartości brzegowych", 1),
        EXISTS_NEIGHBORHOOD("Operacja na istniejącym sąsiedzstwie", 2);

        String name;
        int index;

        EdgeMode(String name, int index) {
            this.name = name;
            this.index = index;
        }

        @Override
        public String toString(){
            return name;
        }
    }

    ArrayList<ActionListener> actionListeners = new ArrayList<>();
    private int actionEventId = 0;

    Component parent;
    TableModelListener tableModelListener = new TableModelListener() {

        public void tableChanged(TableModelEvent e) {
            Object object = getValueAt(e.getFirstRow(), e.getColumn());
            // Ustawiając sparsowaną wartość listener wywołuje się kolejny raz, wiec w tym momecie przerywam jego działanie.
            if (object.getClass().equals(Integer.class))
                return;
            Integer selected = pharseValue(object);
            selected = checkSumValue(checkValue(selected), e.getFirstRow(), e.getColumn());
            setValueAt(selected, e.getFirstRow(), e.getColumn());
            fireActionEvent();
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

    public int[][] getMaskMatrixEdgeMode(int posX, int posY, int width, int height, EdgeMode edgeMode ) {
        if(EdgeMode.REPLICATE.equals(edgeMode) || !itIsEdge(posX, posY, width, height)){
            return getMaskMatrix();
        }
        int pixelHood = (getColumnCount()-1)/2;
        int[][] matrix = new int[getColumnCount()][getRowCount()];
        for (int i = 0; i < getColumnCount(); i++) {
            for (int j = 0; j < getRowCount(); j++) {
                switch (edgeMode) {
                    case EXISTS_NEIGHBORHOOD:
                        if (i + posX < pixelHood || j + posY < pixelHood || i + posX >= width + pixelHood || j + posY >= height + pixelHood) {
                            matrix[i][j] = 0;
                        } else {
                            matrix[i][j] = (Integer) getValueAt(i, j);
                        }
                        break;
                    case NO_CHANGE:
                        if(i == pixelHood && j == pixelHood){
                            matrix[i][j] = 1;
                        } else {
                            matrix[i][j] = 0;
                        }
                }

            }
        }
        return matrix;
    }

    private boolean itIsEdge(int posX, int posY, int width, int height) {
        return ImageCursor.itIsEdge(posX, posY, width, height, getColumnCount() - 1 / 2);
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

    protected Integer checkSumValue(Integer object, int excludeI, int excludeJ) {

        if(allowNonZeroSum==true) { return object; }

        int sum = 0;
        for (int i = 0; i < getColumnCount(); i++) {
            for (int j = 0; j < getRowCount(); j++) {
                if(i==excludeI && j==excludeJ) continue;
                sum += (Integer) getValueAt(i, j);
            }
        }

            if(sum == -object) {
                JOptionPane.showMessageDialog(this,
                        "Suma wartości maski wyniosłaby 0. Ustawiam " + (object +1) + " zamiast " +object + ".",
                        "Błąd danych wejściowych!",
                        JOptionPane.ERROR_MESSAGE);
                        object++;
            }
        return object;
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

    public void addActionListener(ActionListener actionListener) {
        actionListeners.add(actionListener);
    }

    public void removeActionListener(ActionListener actionListener) {
        actionListeners.remove(actionListener);
    }

    public void fireActionEvent() {
        for(ActionListener actionListener: actionListeners) {
            actionListener.actionPerformed(new ActionEvent((Object) this, actionEventId++, "Cell edited" ));
        }
    }
}
