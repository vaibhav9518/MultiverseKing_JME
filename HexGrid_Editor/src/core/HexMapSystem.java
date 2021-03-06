package core;

import core.gui.HexMapPanel;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import core.control.GhostControl;
import gui.deprecated.EditorMainGUI;
import gui.deprecated.FileManagerPopup;
import java.util.List;
import org.hexgridapi.core.appstate.MouseControlSystem;
import org.hexgridapi.core.HexTile;
import org.hexgridapi.core.MapData;
import org.hexgridapi.core.appstate.AbstractHexGridAppState;
import org.hexgridapi.core.control.ChunkControl;
import org.hexgridapi.core.control.TileSelectionControl;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;
import test.EditorMain;

/**
 * To be extended to create other editor.
 *
 * @author roah
 */
public final class HexMapSystem extends AbstractHexGridAppState {

    private EditorMain app;
    private GhostControl ghostControl;
    private EditorMainGUI editorMainGUI;
    private TileSelectionControl tileSelectionControl;
    private HexMapPanel hexMapPanel;

    public HexMapSystem(MapData mapData, AssetManager assetManager, Node rootNode) {
        super(mapData, assetManager, rootNode, true);
    }

    @Override
    public void initializeSystem(AppStateManager stateManager, Application app) {
        this.app = (EditorMain) app;

        MouseControlSystem mouseControl = app.getStateManager().getState(MouseControlSystem.class);
        if (mouseControl == null) {
            mouseControl = new MouseControlSystem(this.app.getRootNode());
            app.getStateManager().attach(mouseControl);
        }
        tileSelectionControl = mouseControl.getSelectionControl();

        hexMapPanel = new HexMapPanel((EditorMain) app, mouseControl, this);
        initialiseGhostGrid((SimpleApplication) app);
    }

    protected void initialiseGhostGrid(SimpleApplication app) {
        Node node = new Node("GhostNode");
        ghostControl = new GhostControl(app, meshParam, new Vector2Int(), this);
        node.addControl(ghostControl);
        gridNode.attachChild(node);
    }

    // <editor-fold defaultstate="collapsed" desc="Getters && Setters">

    public String getMapName() {
        return mapData.getMapName();
    }
    
    public void enableGhostUpdate(boolean enable) {
        ghostControl.setEnabled(true);
    }

    public void removeTile() {
        if (tileSelectionControl.getSelectedList().isEmpty()) {
            mapData.setTile(tileSelectionControl.getSelectedPos(), null);
        } else {
            mapData.setTile(tileSelectionControl.getSelectedList().toArray(
                    new HexCoordinate[tileSelectionControl.getSelectedList().size()]),
                    new HexTile[]{null});
        }
    }

    public void setNewTile() {
        if (tileSelectionControl.getSelectedList().isEmpty()) {
            mapData.setTile(tileSelectionControl.getSelectedPos(), new HexTile());
        } else {
            mapData.setTile(tileSelectionControl.getSelectedList().toArray(
                    new HexCoordinate[tileSelectionControl.getSelectedList().size()]),
                    new HexTile[]{new HexTile()});
        }
    }

    public void setTilePropertiesTexTure(String textureKey) {
        if (!tileSelectionControl.getSelectedList().isEmpty()) {
            HexCoordinate[] tileList = tileSelectionControl.getSelectedList().toArray(
                    new HexCoordinate[tileSelectionControl.getSelectedList().size()]);
            HexTile[] t = mapData.getTile(tileList);
            for (int i = 0; i < t.length; i++) {
                if (t[i] != null) {
                    t[i] = t[i].cloneChangedTextureKey(mapData.getTextureKey(textureKey));
                } else {
                    t[i] = new HexTile(0, mapData.getTextureKey(textureKey));
                }
            }
            mapData.setTile(tileList, t);
        } else {
            HexTile t = mapData.getTile(tileSelectionControl.getSelectedPos());
            if (t != null) {
                t = t.cloneChangedTextureKey(mapData.getTextureKey(textureKey));
            } else {
                t = new HexTile(0, mapData.getTextureKey(textureKey));
            }
            mapData.setTile(tileSelectionControl.getSelectedPos(), t);
        }
    }

    public void setTilePropertiesHeight(int height) {
        mapData.setTile(tileSelectionControl.getSelectedPos(), mapData.getTile(tileSelectionControl.getSelectedPos()).cloneChangedHeight(height));
    }

    public void setTilePropertiesUp() {
        if (!tileSelectionControl.getSelectedList().isEmpty()) {
            HexCoordinate[] tileList = tileSelectionControl.getSelectedList().toArray(
                    new HexCoordinate[tileSelectionControl.getSelectedList().size()]);
            HexTile[] t = mapData.getTile(tileList);
            for (int i = 0; i < t.length; i++) {
                if (t[i] != null) {
                    t[i] = t[i].cloneChangedHeight(t[i].getHeight() + 1);
                } else {
                    t[i] = new HexTile(1);
                }
            }
            mapData.setTile(tileList, t);
        } else {
            HexTile t = mapData.getTile(tileSelectionControl.getSelectedPos());
            if (t != null) {
                t = t.cloneChangedHeight(t.getHeight() + 1);
            } else {
                t = new HexTile(1);
            }
            mapData.setTile(tileSelectionControl.getSelectedPos(), t);
        }
    }

    public void setTilePropertiesDown() {
        if (!tileSelectionControl.getSelectedList().isEmpty()) {
            HexCoordinate[] tileList = tileSelectionControl.getSelectedList().toArray(
                    new HexCoordinate[tileSelectionControl.getSelectedList().size()]);
            HexTile[] t = mapData.getTile(tileList);
            for (int i = 0; i < t.length; i++) {
                if (t[i] != null) {
                    t[i] = t[i].cloneChangedHeight((t[i].getHeight() - 1));
                } else {
                    t[i] = new HexTile(-1);
                }
            }
            mapData.setTile(tileList, t);
        } else {
            HexTile t = mapData.getTile(tileSelectionControl.getSelectedPos());
            if (t != null) {
                t = t.cloneChangedHeight(t.getHeight() - 1);
            } else {
                t = new HexTile(-1);
            }
            mapData.setTile(tileSelectionControl.getSelectedPos(), t);
        }
    }

    /**
     * @return the currently selected tile.
     */
    public HexTile getTile() {
        return mapData.getTile(tileSelectionControl.getSelectedPos());
//        return mapData.getTile(tileSelectionControl.getTileList().toArray(new HexCoordinate[tileSelectionControl.getTileList().size()]));
    }

    public int getTileHeight() {
        HexTile tile = mapData.getTile(tileSelectionControl.getSelectedPos());
        if (tile != null) {
            return tile.getHeight();
        } else {
            return 0;
        }
    }

    public int getTileTextureValue() {
        HexTile tile = mapData.getTile(tileSelectionControl.getSelectedPos());
        if (tile != null) {
            return tile.getTextureKey();
        } else {
            return 0;
        }
    }

    public String getTileTextureKey() {
        HexTile tile = mapData.getTile(tileSelectionControl.getSelectedPos());
        if (tile != null) {
            return mapData.getTextureValue(tile.getTextureKey());
        } else {
            return mapData.getTextureValue(0);
        }
    }

    public List<String> getTextureKeys() {
        return mapData.getTextureKeys();
    }

    public String getTextureDefault() {
        return mapData.getTextureKeys().get(0);
    }

    public String getTextureValueFromKey(int textureKey) {
        return mapData.getTextureValue(textureKey);
    }

    public void setEnabledGhost(boolean enable) {
        ghostControl.setEnabled(enable);
    }
    // </editor-fold>

    public void save(FileManagerPopup popup) {
//        if (!mapData.containTilesData() || !mapData.saveArea(popup.getInput())) {
//            popup.popupBox("    " + popup.getInput() + " couldn't be saved.");
//        }
    }

    @Override
    public void updateSystem(float tpf) {
    }

    @Override
    protected void insertedChunk(ChunkControl control) {
        ghostControl.updateCulling();
    }

    @Override
    protected void updatedChunk(ChunkControl control) {
    }

    @Override
    protected void removedChunk(Vector2Int pos) {
        ghostControl.updateCulling();
    }

    public void loadFromFile(FileManagerPopup popup) {
        if (popup != null && popup.getInput() != null) {
            if (!mapData.loadArea(popup.getInput())) {
                popup.popupBox("    " + popup.getInput() + " couldn't be loaded.");
            } else {
                popup.removeFromScreen();
            }
        } else {
            if (popup != null) {
                popup.popupBox("    " + "There is nothing to load.");
            }
            reloadSystem();
        }
    }
    
    /**
     * @deprecated
     */
    public void clearSelectionGroup() {
//        tileSelectionControl.clearSelectionGroup();
        editorMainGUI.showCurrentSelectionCount(tileSelectionControl.getSelectedList().size());
    }

    public void reloadSystem() {
        mapData.Cleanup();
    }

    @Override
    public void cleanupSystem() {
//        app.getStateManager().getState(MouseControlAppState.class).removeTileInputListener(this);
    }
}
