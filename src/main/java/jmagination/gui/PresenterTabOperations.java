package jmagination.gui;

import jmagination.ConstantsInitializers;
import jmagination.RunOperation;
import jmagination.operations.Operation;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by darek on 29.12.2016.
 */
public class PresenterTabOperations extends PresenterTab {

    JSplitPane choicePanel;
    JScrollPane choicePanelCategories;
    JPanel choicePanelContainer;
    JScrollPane choicePanelOperations;
    JPanel parametersPanel;
    JPanel parametersPanelNorth;
    JPanel parametersPanelCenter;
    JPanel parametersPanelSouth;


    GUIStyler.JButtonJM jButtonRevertOperationOutcome;
    GUIStyler.JButtonJM jButtonSaveOperationsOutcome;

    GUIStyler.JButtonJM jButtonLeaveOperationPanel;

    RunOperation runOperation;


    public PresenterTabOperations(ArrayList<Operation> availableOperations, RunOperation runOperation) {
        super();

        this.runOperation = runOperation;

        choicePanelCategories = new JScrollPane();
        choicePanelContainer = new JPanel();
        choicePanelContainer.setLayout(new BorderLayout());
        choicePanelOperations = new JScrollPane();
        choicePanelContainer.add(choicePanelOperations, BorderLayout.CENTER);


        choicePanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, choicePanelCategories, choicePanelContainer);
        choicePanel.setOneTouchExpandable(true);
        choicePanel.setResizeWeight(0.5);
        choicePanel.setDividerSize(ConstantsInitializers.GUI_DIMENSION_splitPaneDividerSize);


        parametersPanel = new JPanel();
        parametersPanel.setLayout(new BorderLayout());
        parametersPanel.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_ALT_COLOR);
        parametersPanel.setVisible(false);

        parametersPanelNorth = new JPanel();
        parametersPanelCenter = new JPanel();
        parametersPanelSouth = new JPanel();

        parametersPanel.add(parametersPanelNorth,BorderLayout.NORTH);
        parametersPanel.add(parametersPanelCenter,BorderLayout.CENTER);
        parametersPanel.add(parametersPanelSouth,BorderLayout.SOUTH);

        // parameters North



        // parameters South
        jButtonLeaveOperationPanel = new GUIStyler.JButtonJM("Powrót");
        jButtonRevertOperationOutcome = new GUIStyler.JButtonJM("Cofnij zmiany");
        jButtonSaveOperationsOutcome = new GUIStyler.JButtonJM("Zapisz");

        parametersPanelSouth.add(jButtonLeaveOperationPanel);
        parametersPanelSouth.add(jButtonRevertOperationOutcome);
        parametersPanelSouth.add(jButtonSaveOperationsOutcome);


        // parameters Center
        drawControls(availableOperations, runOperation, parametersPanelCenter);

        add(choicePanel);
        add(parametersPanel);

    }

    public PresenterTabOperations(ArrayList<Operation> availableOperations, Dimension dimension, RunOperation runOperation) {
        this(availableOperations, runOperation);

        choicePanel.setMinimumSize(dimension);
        choicePanel.setPreferredSize(dimension);
        parametersPanel.setMinimumSize(dimension);
        parametersPanel.setPreferredSize(dimension);

    }

    private void selectCommand() {
        choicePanel.setVisible(false);
        updateControls(false);
        parametersPanel.setVisible(true);
    }

    private void deSelectCommand() {
        choicePanel.setVisible(true);
        parametersPanel.setVisible(false);
    }

    public void updateControls(boolean enabled) {
        jButtonRevertOperationOutcome.setEnabled(enabled);
        jButtonSaveOperationsOutcome.setEnabled(enabled);
    }

    private void drawControls(ArrayList<Operation> availableOperations, RunOperation runOperation, JPanel panel) {

        DefaultListModel catListModel = new DefaultListModel();
        JList categoriesList = new JList(catListModel);
        categoriesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        choicePanelCategories.setViewportView(categoriesList);


        DefaultListModel opListModel = new DefaultListModel();
        JList operationsList = new JList(opListModel);
        operationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        choicePanelOperations.setViewportView(operationsList);

        GUIStyler.JButtonJM jButtonSelect = new GUIStyler.JButtonJM("Wybierz");
        choicePanelContainer.add(jButtonSelect,BorderLayout.SOUTH);


        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                panel.removeAll();

                Operation selectedOperation = (Operation) operationsList.getSelectedValue();
                selectedOperation.setRunOperation(runOperation);
                selectedOperation.drawConfigurationPanel(panel);

                jButtonLeaveOperationPanel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        runOperation.discardOperation(selectedOperation);
                        deSelectCommand();
                    }
                });

                ActionListener[] jButtonRevertOperationOutcomeActionListeners = jButtonRevertOperationOutcome.getActionListeners();
                for(ActionListener actionListener : jButtonRevertOperationOutcomeActionListeners) {
                    jButtonRevertOperationOutcome.removeActionListener(actionListener);
                }
                jButtonRevertOperationOutcome.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        runOperation.discardOperation(selectedOperation);
                    }
                });

                ActionListener[] jButtonSaveOperationsOutcomeActionListeners = jButtonSaveOperationsOutcome.getActionListeners();
                for(ActionListener actionListener : jButtonSaveOperationsOutcomeActionListeners) {
                    jButtonSaveOperationsOutcome.removeActionListener(actionListener);
                }
                jButtonSaveOperationsOutcome.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        runOperation.saveOperationsOutput(selectedOperation);
                    }
                });


                getParent().repaint();
                selectCommand();
            }
        };

        jButtonSelect.addActionListener(al);

        ArrayList<String> tags = new ArrayList<>();

        for (Operation op: availableOperations) {

            for(String cat: op.getCategories()) {
                boolean duplicate = false;
                for(String tag: tags) {
                    if(cat==tag) { duplicate = true; }
                }
                if(duplicate == false) {tags.add(cat); };
            }

        }

        for(String tag: tags) {
            catListModel.addElement(tag);
        }

        int initSelectedCategory = 0;
        categoriesList.setSelectedIndex(initSelectedCategory);
        String selectedCategory = (String) catListModel.getElementAt(initSelectedCategory);

        for (Operation op: availableOperations) {
            ArrayList<String> opCat = op.getCategories();
            for(String cat: opCat) {
                if(cat == selectedCategory) {
                    opListModel.addElement(op);
                    continue;
                }
            }
        }
        jButtonSelect.setEnabled(false);

        categoriesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(e.getValueIsAdjusting()==false){
                    JList list = (JList) e.getSource();
                    String selected = (String) list.getSelectedValue();

                    opListModel.removeAllElements();

                    for (Operation op: availableOperations) {
                        ArrayList<String> opCat = op.getCategories();
                        for(String cat: opCat) {
                            if(cat == selected) {
                                opListModel.addElement(op);
                            }
                        }
                    }
                    jButtonSelect.setEnabled(false);
                    choicePanelContainer.revalidate();
                    operationsList.revalidate();

                }
            }
        });

        operationsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(e.getValueIsAdjusting()==false){
                    JList list = (JList) e.getSource();
                    if(list.getSelectedIndex() == -1) {
                    } else {
                        jButtonSelect.setEnabled(true);
                    }
                } else {
                    jButtonSelect.setEnabled(false);
                }
            }
        });


    }

}
