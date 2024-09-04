package components;

import main.Game;
import utils.Planets;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Map;

/**
 * author: ahror
 * <p>
 * since: 8/31/24
 */
public class ShowPlanets extends RoundedDialog {
    private static ShowPlanets instance;
    private String selectedPlanet;
    private final String[] options;
    private final String currentPlanet;
    private JList<String> planetList;

    public ShowPlanets(Map<String, Float> planetGravity, String currentPlanet) {
        super(250,330);
        this.options = planetGravity.keySet().toArray(new String[0]);
        this.currentPlanet = currentPlanet;
        initializeUI();
    }

    private void initializeUI() {
        JLabel titleLabel = new JLabel("Select a planet", SwingConstants.CENTER);
        titleLabel.setForeground(new Color(230, 230, 230));
        titleLabel.setFont(Game.UIFont);
        titleLabel.setBorder(new EmptyBorder(0,0,10,0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        planetList = new JList<>(options);
        planetList.setAlignmentY(Component.CENTER_ALIGNMENT);
        planetList.setFixedCellHeight(22);
        planetList.setLayout(new GridLayout());
        planetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        planetList.setSelectedValue(currentPlanet, true);
        planetList.setBackground(new Color(0, 0, 0, 0));
        planetList.setForeground(new Color(150, 150, 150));
        planetList.setFont(Game.UIFont.deriveFont(14f));
        planetList.setOpaque(false);
        planetList.setSelectionForeground(Color.WHITE);
        planetList.setSelectionBackground(new Color(0, 0, 0, 0));
        planetList.setBorder(new EmptyBorder(10,0, 10, 0));
        JScrollPane scrollPane = new RoundedScrollPane(planetList, 15);
        scrollPane.setBackground(new Color(40, 40, 40));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        RoundedButton selectButton = new RoundedButton("Select", 15);
        selectButton.addActionListener(e -> selectAndClose());
        mainPanel.add(selectButton, BorderLayout.SOUTH);

        addInputListeners(mainPanel);
    }

    private void addInputListeners(JPanel mainPanel) {
        ActionMap actionMap;
        InputMap inputMap;
        inputMap = mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        actionMap = mainPanel.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectPlanet");
        actionMap.put("selectPlanet", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectAndClose();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeDialog");
        actionMap.put("closeDialog", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "moveUp");
        actionMap.put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = planetList.getSelectedIndex();
                if (selectedIndex > 0) {
                    planetList.setSelectedIndex(selectedIndex - 1);
                    planetList.ensureIndexIsVisible(selectedIndex - 1);
                }
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "moveDown");
        actionMap.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = planetList.getSelectedIndex();
                if (selectedIndex < planetList.getModel().getSize() - 1) {
                    planetList.setSelectedIndex(selectedIndex + 1);
                    planetList.ensureIndexIsVisible(selectedIndex + 1);
                }
            }
        });
    }

    private void selectAndClose() {
        selectedPlanet = planetList.getSelectedValue();
        Planets.setCurrentPlanet(selectedPlanet);
        dispose();
    }

    public static ShowPlanets getInstance() {
        if (instance == null) {
            instance = new ShowPlanets(Planets.planetGravity, "Earth");
        }
        return instance;
    }
}
