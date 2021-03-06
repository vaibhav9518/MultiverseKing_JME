package core.gui;

import core.HexMapSystem;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;
import org.hexgridapi.core.appstate.MapDataAppState;
import org.hexgridapi.core.appstate.MouseControlSystem;
import org.hexgridapi.core.mapgenerator.MapGenerator;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.events.TileChangeListener;
import org.hexgridapi.events.TileSelectionListener;
import org.hexgridapi.utility.HexCoordinate;
import test.EditorMain;

/**
 *
 * @author roah
 */
public class HexMapPanel extends AbstractAction {

    private final HexMapSystem editorSystem;
    private final MouseControlSystem mouseSystem;
    private final EditorMain editorMain;
    private JPanel hexMapPanel;
    private Boolean currentIsGhost;
    private boolean currentIsGroup = false;
    private JPanel tileProperties;
    private HashMap<String, JComponent> comps = new HashMap<>();
    private boolean update = true;

    public HexMapPanel(EditorMain editorMain, MouseControlSystem mouseSystem, HexMapSystem editorSystem) {
        this.mouseSystem = mouseSystem;
        this.editorSystem = editorSystem;
        this.editorMain = editorMain;

        for (Component menu : editorMain.getRootWindow().getJMenuBar().getComponents()) {
            if (((JMenu) menu).getActionCommand().equals("Hex Editor")) {
                ((JMenu) menu).add(new AbstractAction("Save Map") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        onAction(e);
                    }
                });
            }
        }
        editorMain.getRootWindow().add(buildMenu(), BorderLayout.EAST);
        editorMain.getRootWindow().revalidate();

        register();
    }

    private void register() {
        editorMain.enqueue(new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                mouseSystem.getSelectionControl().registerTileListener(selectionListener);
                editorMain.getStateManager().getState(MapDataAppState.class).getMapData().registerTileChangeListener(tileListener);
                return null;
            }
        });
    }

    private JPanel buildMenu() {
        hexMapPanel = new JPanel();
        hexMapPanel.setLayout(new BoxLayout(hexMapPanel, BoxLayout.PAGE_AXIS));
        hexMapPanel.setAlignmentX(0);
        hexMapPanel.setBorder(BorderFactory.createTitledBorder("Map Property"));
        hexMapPanel.setPreferredSize(new Dimension(170, 300));
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        addComp(hexMapPanel, separator);

        JCheckBox box = new JCheckBox(new AbstractAction("Show ghost") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAction(e);
            }
        });
        box.setSelected(true);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        addComp(hexMapPanel, box);
        JLabel mapNameLabel = new JLabel("Map Name : ");
        mapNameLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        hexMapPanel.add(mapNameLabel);
        JTextField mapName = new JTextField(editorSystem.getMapName());
        comps.put("mapName", mapName);
        mapName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        hexMapPanel.add(mapName);

        JLabel mapGenerator = new JLabel("Random Generator : ");
        mapGenerator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        hexMapPanel.add(mapGenerator);

        JPanel seedPan = new JPanel();
        seedPan.setLayout(new BoxLayout(seedPan, BoxLayout.LINE_AXIS));
        seedPan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        seedPan.setAlignmentX(0);
        JLabel currentSeed = new JLabel(String.valueOf(MapGenerator.getInstance().getCurrentSeed()));
        comps.put("currentSeed", currentSeed);
        seedPan.add(currentSeed);
        seedPan.add(Box.createRigidArea(new Dimension(5, 0)));
        JButton genSeed = new JButton(new AbstractAction("Gen Seed") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((JLabel) comps.get("currentSeed")).setText(String.valueOf(MapGenerator.getInstance().generateNewSeed()));
            }
        });
        genSeed.setPreferredSize(new Dimension(50, 23));
        seedPan.add(genSeed);
        comps.put("genSeed", genSeed);
        addComp(hexMapPanel, seedPan);
//        hexMapPanel.add(seedPan);
        hexMapPanel.add(Box.createRigidArea(new Dimension(0,3)));
        JButton genMap = new JButton(new AbstractAction("GenerateMap") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAction(e);
            }
        });
        genMap.setAlignmentX(0);
        genMap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        comps.put("GenerateMap", genMap);
        hexMapPanel.add(genMap);
        
        separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 2));
        addComp(hexMapPanel, separator);

        buildCursorMenu();
        return hexMapPanel;
    }

    private void buildCursorMenu() {
        JPanel cursorProperties = new JPanel();
        cursorProperties.setLayout(new BoxLayout(cursorProperties, BoxLayout.PAGE_AXIS));
        cursorProperties.setAlignmentX(0);
        cursorProperties.setBorder(BorderFactory.createTitledBorder("Cursor Property"));
        cursorProperties.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        cursorProperties.add(Box.createRigidArea(new Dimension(0, 3)));
        JLabel coordinate = new JLabel("Coordinate : null");
        addComp(cursorProperties, coordinate);
        comps.put("coordinate", coordinate);
        JLabel chunkPos = new JLabel("Chunk : null");
        addComp(cursorProperties, chunkPos);
        comps.put("chunkPos", chunkPos);
        cursorProperties.add(Box.createRigidArea(new Dimension(0, 3)));

        addComp(hexMapPanel, cursorProperties);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        onAction(e);
    }

    private void onAction(ActionEvent e) {
        if (e.getActionCommand().contains("comboBox") && update) {
            final int value = Integer.valueOf(e.getActionCommand().split("\\.")[1]);
            editorMain.enqueue(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    editorSystem.setTilePropertiesTexTure(editorSystem.getTextureValueFromKey(value));
                    return null;
                }
            });
            update = false;
            return;
        } else if (e.getActionCommand().contains("comboBox")) {
            update = true;
            return;
        }
        switch (e.getActionCommand()) {
            case "GenerateMap":
                MapGenerator.getInstance().generateMap(Integer.valueOf(((JLabel)comps.get("currentSeed")).getText()));
                break;
            case "Save Map":
                CustomDialog customDialog = new CustomDialog(editorMain.getRootWindow(), ((JTextField) comps.get("mapName")).getText());
                customDialog.setLocationRelativeTo(editorMain.getRootWindow());
                customDialog.setVisible(true);
                String s = customDialog.getValidatedText();
                if (s != null) {
                    //The text is valid.
                    editorMain.enqueue(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            editorSystem.save(null);
                            return null;
                        }
                    });
                    System.err.println("Save Map " + s);
                }
                break;
            case "Show ghost":
                //TODO
                System.err.println("TODO : " + e.getActionCommand());
                break;
            case "Destroy":
                editorMain.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        editorSystem.removeTile();
                        return null;
                    }
                });
                break;
            case "btnUp":
                editorMain.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        editorSystem.setTilePropertiesUp();
                        return null;
                    }
                });
                break;
            case "btnDown":
                editorMain.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        editorSystem.setTilePropertiesDown();
                        return null;
                    }
                });
                break;
            case "Generate/Reset":
                editorMain.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        editorSystem.setNewTile();
                        return null;
                    }
                });
//                System.err.println("TODO : " + e.getActionCommand());
                break;
            default:
                System.err.println("No associated action for : " + e.getActionCommand());
        }
    }
    private TileSelectionListener selectionListener = new TileSelectionListener() {
        @Override
        public void onTileSelectionUpdate(HexCoordinate currentSelection, ArrayList<HexCoordinate> selectedList) {
            if (currentIsGhost == null || selectedList != null && !selectedList.isEmpty() != currentIsGroup
                    || (editorSystem.getTile() == null ? true : false) != currentIsGhost) {
                if (selectedList != null && !selectedList.isEmpty()) {
                    buildMultiTileMenu(selectedList);
                } else {
                    buildSingleTileMenu();
                }
            } else {
                if (selectedList != null && !selectedList.isEmpty()) {
//                    updateMultiTileMenu(selectedList);
                } else {
                    updateSingleTileMenu();
                }
            }
            ((JLabel) comps.get("coordinate")).setText("Coordinate : " + currentSelection.getAsOffset());
            ((JLabel) comps.get("chunkPos")).setText("Coordinate : " + currentSelection.getCorrespondingChunk());
        }
    };
    private TileChangeListener tileListener = new TileChangeListener() {
        @Override
        public void onTileChange(TileChangeEvent... events) {
            if (!currentIsGroup && events.length == 1
                    && events[0].getTilePos().equals(mouseSystem.getSelectionControl().getSelectedPos())) {
                if ((editorSystem.getTile() == null ? true : false) != currentIsGhost) {
                    buildSingleTileMenu();
                } else {
                    updateSingleTileMenu();
                }
            }
        }
    };

    private void buildTileMenu() {
        currentIsGhost = editorSystem.getTile() == null ? true : false;
        if (tileProperties == null) {
            tileProperties = new JPanel();
            tileProperties.setLayout(new BoxLayout(tileProperties, BoxLayout.PAGE_AXIS));
            tileProperties.setAlignmentX(0);
            tileProperties.setBorder(BorderFactory.createTitledBorder("Tile Property"));
            addComp(hexMapPanel, tileProperties);
        } else {
            tileProperties.removeAll();
        }
    }

    private void buildSingleTileMenu() {
        buildTileMenu();
        // Component Value
        int compCount = 1;
        currentIsGroup = false;
        if (currentIsGhost) {
            compCount += addGenerateBtn();
        } else {
            compCount += addHeightBtn(false);
            compCount += addTextureList();
            compCount += addDestroyBtn();
            tileProperties.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        tileProperties.setMaximumSize(new Dimension(hexMapPanel.getMaximumSize().width, compCount * 23 + 10));
        hexMapPanel.revalidate();
    }

    private void buildMultiTileMenu(ArrayList<HexCoordinate> selectedList) {
        buildTileMenu();
        currentIsGroup = !selectedList.isEmpty();
        int compCount = 1;
        compCount += addTextureList();
        compCount += addHeightBtn(true);
        compCount += addGenerateBtn();
        compCount += addDestroyBtn();
        tileProperties.setMaximumSize(new Dimension(hexMapPanel.getMaximumSize().width, compCount * 23 + 10));

        hexMapPanel.revalidate();
    }

    // <editor-fold defaultstate="collapsed" desc="Add Component Method">
    private int addGenerateBtn() {
        if (!comps.containsKey("generate")) {
            JButton generate = new JButton(new AbstractAction("Generate/Reset") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onAction(e);
                }
            });
            addComp(tileProperties, generate);
            comps.put("generate", generate);
        } else {
            addComp(tileProperties, comps.get("generate"));
        }
        return 1;
    }

    private int addHeightBtn(boolean isMulti) {
        if (!comps.containsKey("heightPanel")) {
            JPanel heightPanel = new JPanel();
            heightPanel.setAlignmentX(0);
            heightPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            heightPanel.setLayout(new BorderLayout());
            BasicArrowButton btn = new BasicArrowButton(BasicArrowButton.NORTH);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onAction(new ActionEvent(e.getSource(), e.getID(), "btnUp"));
                }
            });
            heightPanel.add(btn, BorderLayout.NORTH);
            btn = new BasicArrowButton(BasicArrowButton.SOUTH);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onAction(new ActionEvent(e.getSource(), e.getID(), "btnDown"));
                }
            });
            heightPanel.add(btn, BorderLayout.SOUTH);
            JLabel height;
            if (!isMulti) {
                height = new JLabel("height : " + editorSystem.getTileHeight());
            } else {
                height = new JLabel("height : undefined");
            }
            heightPanel.add(height, BorderLayout.CENTER);
            comps.put("height", height);
            addComp(tileProperties, heightPanel);
            heightPanel.validate();
            comps.put("heightPanel", heightPanel);
        } else {
            addComp(tileProperties, comps.get("heightPanel"));
            if (!isMulti) {
                ((JLabel) comps.get("height")).setText("height : " + editorSystem.getTileHeight());
            } else {
                ((JLabel) comps.get("height")).setText("height : undefined");
            }
        }
        return 3;
    }

    private int addTextureList() {
        if (!comps.containsKey("textureList")) {
            ComboBoxRenderer combo = new ComboBoxRenderer(
                    editorMain.getAssetManager(), editorSystem.getTextureKeys());
            JComboBox textureList = new JComboBox(combo.getArray());
            textureList.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onAction(new ActionEvent(e.getSource(), e.getID(), e.getActionCommand() + "." + ((JComboBox) comps.get("textureList")).getSelectedIndex()));
//                        System.err.println(editorSystem.getTextureKeys().get(((JComboBox)comps.get("textureList")).getSelectedIndex()));
                }
            });
            textureList.setRenderer(combo);
            textureList.setMaximumRowCount(4);
            textureList.setAlignmentX(0);
            textureList.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
            addComp(tileProperties, textureList);
            comps.put("textureList", textureList);
//                int var = editorSystem.getTextureKeys().indexOf(editorSystem.getTileTextureKey());
//                ((JComboBox)comps.get("textureList")).setSelectedIndex(var);
//                update = false;
        } else {
            addComp(tileProperties, comps.get("textureList"));
            update = false;
            int var = editorSystem.getTextureKeys().indexOf(editorSystem.getTileTextureKey());
            ((JComboBox) comps.get("textureList")).setSelectedIndex(var);
        }
        return 1;
    }

    private int addDestroyBtn() {
        if (!comps.containsKey("destroy")) {
            JButton destroy = new JButton(new AbstractAction("Destroy") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onAction(e);
                }
            });
            destroy.setAlignmentX(0);
            destroy.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
            addComp(tileProperties, destroy);
            comps.put("destroy", destroy);
        } else {
            addComp(tileProperties, comps.get("destroy"));
        }
        return 1;
    }

    private void addComp(JPanel pan, Component comp) {
        pan.add(Box.createRigidArea(new Dimension(0, 3)));
        pan.add(comp);
    }

    // </editor-fold>
    private void updateSingleTileMenu() {
        currentIsGhost = editorSystem.getTile() == null ? true : false;
        if (!currentIsGhost) {
            ((JComboBox) comps.get("textureList")).setSelectedItem(editorSystem.getTileTextureKey());
            ((JLabel) comps.get("height")).setText("height : " + editorSystem.getTileHeight());
            update = false;
            editorMain.enqueue(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    int var = editorSystem.getTextureKeys().indexOf(editorSystem.getTileTextureKey());
                    ((JComboBox) comps.get("textureList")).setSelectedIndex(var);
                    return null;
                }
            });
        }
        hexMapPanel.revalidate();
    }

    private void updateMultiTileMenu(ArrayList<HexCoordinate> selectedList) {
//        currentIsGroup = !selectedList.isEmpty();
        System.err.println("update group");
//        hexMapPanel.validate();
    }

    public JComponent getComponent(String UID) {
        return comps.get(UID);
    }
}
