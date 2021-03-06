package editor.area;

import com.jme3.math.FastMath;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import editor.EditorSystem;
import editor.map.MapEditorSystem;
import entitysystem.field.position.HexPositionComponent;
import gui.FileManagerPopup;
import hexsystem.area.AreaEventSystem;
import hexsystem.area.AreaPropsComponent;
import hexsystem.area.MapDataAppState;
import kingofmultiverse.MultiverseMain;
import org.hexgridapi.core.appstate.MouseControlSystem;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.HexTile;
import org.hexgridapi.core.MapData;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.TileInputListener;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.events.TileChangeListener;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;
import utility.ElementalAttribut;

/**
 *
 * @author roah
 */
public final class AreaEditorSystem extends MapEditorSystem implements TileChangeListener, TileInputListener {

    private MapData mapData;
    private ElementalAttribut mapElement;
    private AreaTileWidget tileWidgetMenu;
    private TileEventMenu tileEventMenu;
    private FileManagerPopup popup = null;
    private AreaEditorGUI gui;

    public AreaEditorSystem(FileManagerPopup popup) {
        if(popup != null && popup.isLoading()){
            this.popup = popup;
        }
    }

    @Override
    protected EntitySet initialiseSystem() {
        mapData = ((MultiverseMain) app).getStateManager().getState(MapDataAppState.class).getMapData();
//        app.getStateManager().attach(new AreaGridSystem(mapData));
//        app.getStateManager().attach(new AreaEventRenderDebugSystem());
//        app.getStateManager().attach(new AreaEventSystem());
//        app.getStateManager().attach(new AreaMouseAppState());
        if (popup != null) {
            loadFromFile(popup);
        } else {
            generateEmptyArea();
        }
        app.getStateManager().getState(MouseControlSystem.class).registerTileInputListener(this);
        gui = new AreaEditorGUI(((MultiverseMain)app).getScreen(), ((MultiverseMain)app).getScreen().getElementById("EditorMainMenu"), this);
        return entityData.getEntities(AreaPropsComponent.class, HexPositionComponent.class);
    }

    // <editor-fold defaultstate="collapsed" desc="Tile propertie Getters && Setters">
    /**
     *
     * @param coord
     * @param height how many to add
     */
    void setTileProperties(HexCoordinate coord, byte height) {
        mapData.setTileHeight(coord, (byte) (mapData.getTile(coord).getHeight() + height));
    }

    void setTileProperties(HexCoordinate coord, String eAttribut) {
        mapData.setTileTextureKey(coord, eAttribut);
    }

    void setTileProperties(HexCoordinate coord, HexTile tile) {
        mapData.setTile(coord, tile);
    }

    ElementalAttribut getTileEAttribut(HexCoordinate coord) {
        try {
            return ElementalAttribut.valueOf(mapData.getTextureValue(mapData.getTile(coord).getTextureKey()));
        } catch (IllegalArgumentException e){
            return null;
        }
    }

    int getTileHeight(HexCoordinate coord) {
        return mapData.getTile(coord).getHeight();
    }

    void setMapElement(ElementalAttribut eAttribut) {
        mapData.setDefaultTexture(eAttribut.toString());
    }

    ElementalAttribut getMapElement() {
        return mapElement;
    }
    // </editor-fold>

    @Override
    public void generateEmptyArea() {
//        mapData.registerTileChangeListener(this);
//        if (mapData.getAllChunkPos().isEmpty()) {
//            mapData.addChunk(new Vector2Int(), null);
//        }
    }

    @Override
    public void reloadSystem() {
        mapData.Cleanup();
        app.getStateManager().getState(AreaEventSystem.class).clearAllCurrentEvent();
        closeTileEventMenu();
        generateEmptyArea();
    }

    @Override
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

    @Override
    public void save(FileManagerPopup popup) {
        if (isEmpty() || !mapData.saveArea(popup.getInput())) {
            popup.popupBox("    " + popup.getInput() + " couldn't be saved.");
        }
    }

    /**
     * @return True if there is something who can be saved.
     */
    public boolean isEmpty() {
        return !mapData.containTilesData();
    }

    @Override
    protected void updateSystem(float tpf) {
        if (tileWidgetMenu != null) {
            tileWidgetMenu.update(tpf);
        }
    }

    @Override
    protected void addEntity(Entity e) {
        System.err.println("Not Supported yet.");
    }

    @Override
    protected void updateEntity(Entity e) {
        System.err.println("Not Supported yet.");
    }

    @Override
    protected void removeEntity(Entity e) {
        System.err.println("Not Supported yet.");
    }

    @Override
    public void tileChange(TileChangeEvent event) {
        if (tileWidgetMenu.isVisible()) {
//            openWidgetMenu(event.getTilePos());
        }
    }

    @Override
    public void onMouseAction(MouseInputEvent event) {
        closeTileMenu();
    }

    @Override
    public void rightMouseActionResult(MouseInputEvent event) {
        openWidgetMenu(event.getPosition());
        if(tileEventMenu != null && !event.getPosition().equals(tileEventMenu.getInspectedTilePos())){
            closeTileEventMenu();
        }
    }

    /**
     * Window related to the selected hex.
     *
     * @param coord of the selected hex
     */
    private void openWidgetMenu(HexCoordinate tilePos) {
        if (tileWidgetMenu == null) {
            tileWidgetMenu = new AreaTileWidget(((MultiverseMain) app).getScreen(), app.getCamera(), this, tilePos);
        }
        tileWidgetMenu.show(tilePos, mapData.getTile(tilePos).getHeight());
    }

    private void closeTileMenu() {
        closeTileWidgetMenu();
        closeTileEventMenu();
    }

    private void closeTileEventMenu() {
        if (tileEventMenu != null && tileEventMenu.isVisible()) {
            tileEventMenu.hide();
        }
    }

    private void closeTileWidgetMenu() {
        if (tileWidgetMenu != null && tileWidgetMenu.isVisible()) {
            tileWidgetMenu.hide();
        }
    }

    void openEventMenu(HexCoordinate inspectedTilePos) {
        if (tileEventMenu == null) {
            tileEventMenu = new TileEventMenu(app.getStateManager().getState(AreaEventSystem.class), ((MultiverseMain) app).getScreen(), app.getStateManager().getState(EditorSystem.class).getGUI(), inspectedTilePos);;
        } else if (tileEventMenu.getInspectedTilePos() != inspectedTilePos) {
            tileEventMenu.setInpectedTile(inspectedTilePos);
        }
        if (!tileEventMenu.isVisible()) {
            tileEventMenu.show();
        }
    }

    void openAssetMenu(HexCoordinate inspectedTilePos) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void cleanupSystem() {
//        mapData.Cleanup();
//        app.getStateManager().getState(AreaEventSystem.class).clearAllCurrentEvent();
        
//        app.getStateManager().detach(app.getStateManager().getState(AreaMouseAppState.class));
        app.getStateManager().getState(MouseControlSystem.class).removeTileInputListener(this);
//        app.getStateManager().detach(app.getStateManager().getState(AreaGridSystem.class));
//        app.getStateManager().detach(app.getStateManager().getState(AreaEventRenderDebugSystem.class));
//        app.getStateManager().detach(app.getStateManager().getState(AreaEventSystem.class));
        if (tileWidgetMenu != null) {
            tileWidgetMenu.removeFromScreen();
        }
        if (tileEventMenu != null) {
            tileEventMenu.removeFromScreen();
        }
    }

    /**
     * Virtual max size of the map, chunk param only.
     * @return 
     */
    Vector2Int getChunkMapSize() {
//        Vector2Int maxResult = new Vector2Int();
//        Vector2Int minResult = new Vector2Int();
//        for(Vector2Int pos : mapData.getAllChunkPos()){
//            if(pos.x > maxResult.x){
//                maxResult.x = pos.x;
//            } else if(pos.x < minResult.x){
//                minResult.x = pos.x;
//            }
//            if(pos.y > maxResult.y){
//                maxResult.y = pos.y;
//            } else if(pos.y < minResult.y){
//                minResult.y = pos.y;
//            }
//        }
//        return new Vector2Int(FastMath.abs(minResult.x) + FastMath.abs(maxResult.x)+1,
//                FastMath.abs(minResult.y) + FastMath.abs(maxResult.y)+1);
        return new Vector2Int();
    }
    
    Vector2Int getTileMapSize(){
        Vector2Int chunkMapSize = getChunkMapSize();
        return new Vector2Int(chunkMapSize.x * HexSetting.CHUNK_SIZE,
                chunkMapSize.y * HexSetting.CHUNK_SIZE);
    }
}
