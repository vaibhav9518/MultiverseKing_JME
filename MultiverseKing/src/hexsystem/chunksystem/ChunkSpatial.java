/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem.chunksystem;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import hexsystem.HexSettings;
import java.util.ArrayList;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author roah
 */
class ChunkSpatial {
    private final MeshManager meshManager;
    private final Material hexMat;
    private Node rootChunk;
    private Geometry[][] geo;

    
    ChunkSpatial(MeshManager meshManager, Material hexMat) {
        this.meshManager = meshManager;
        this.hexMat = hexMat;
    }
    
    void initialize(Node rootChunk, HexSettings hexSettings, int subChunkSize, ElementalAttribut eAttribut){
        this.rootChunk = rootChunk;
        int subChunkCount = hexSettings.getCHUNK_SIZE()/subChunkSize;
        geo = new Geometry[subChunkCount][subChunkCount];
//        Mesh tileMesh = meshManager.getFlatMesh(Vector2Int.ZERO, new Vector2Int(subChunkSize, subChunkSize));
        
        for (int x = 0; x < subChunkCount; x++) {
            for (int y = 0; y < subChunkCount; y++) {
                geo[x][y] = new Geometry(Integer.toString(x)+"|"+Integer.toString(y), meshManager.getMesh(Vector2Int.ZERO, new Vector2Int(subChunkSize, subChunkSize), eAttribut.ordinal()));
                geo[x][y].setLocalTranslation(getSubChunkLocalWorldPosition(x, y, hexSettings, subChunkSize));
                geo[x][y].setMaterial(hexMat);
                rootChunk.attachChild(geo[x][y]);
            }
        }
    }

    /**
     * @todo custom cull
     * @param enabled 
     */
    void setEnabled(boolean enabled) {
        CullHint culling = CullHint.Inherit;
        if(!enabled){
            culling = Spatial.CullHint.Always;
        }
        for (int x = 0; x < geo.length; x++) {
            for (int y = 0; y < geo[x].length; y++) {
                geo[x][y].setCullHint(culling);
            }
        }
    }
    int test = 0;
    void updateSubChunk(Vector2Int subChunkLocalGridPos, ArrayList<Vector2Int[]> meshParameter, int subChunkSize, HexSettings hexSettings) {
        ArrayList<Geometry> geo = new ArrayList<Geometry>();
        if (test == 2)
//            this.rootChunk.detachChild(this.geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y]);
        test++;
//        this.geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y] = new Geometry(Integer.toString(subChunkLocalGridPos.x)+"|"+Integer.toString(subChunkLocalGridPos.y), meshManager.getMergedMesh(meshParameter));
//        this.geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y].setMaterial(hexMat);
//        this.geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y].setLocalTranslation(getSubChunkLocalWorldPosition(subChunkLocalGridPos.x, subChunkLocalGridPos.y, hexSettings, subChunkSize));
        this.geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y].setMesh(meshManager.getMergedMesh(meshParameter));
//        rootChunk.attachChild(this.geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y]);
//        rootChunk.detachChildNamed("testHeight");
//        Geometry test = new Geometry("testHeight", meshManager.getHeight(meshParameter));
//        test.setMaterial(hexMat);
//        rootChunk.attachChild(test);
//        test.getMesh().setMode(Mesh.Mode.Lines);
//        test.setCullHint(CullHint.Never);
    }

    /**
     * Convert subChunk local grid position to world position.
     * @param subChunklocalGridPos 
     * @return world position of this subChunk.
     * @deprecated no use of it
     */
    Vector3f getSubChunkWorldPos(Vector2Int subChunkLocalGridPos){
        return geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y].getWorldTranslation();
    }

    /**
     * Convert SubChunk local grid position to local world position, relative to chunkNode.
     * @param subChunkLocaGridPosX
     * @param subChunkLocalGridPosY
     * @return 
     */
    private Vector3f getSubChunkLocalWorldPosition(int subChunkLocaGridPosX, int subChunkLocalGridPosY, HexSettings hexSettings, int subChunkSize) {
        float resultX = (subChunkLocaGridPosX*subChunkSize) * hexSettings.getHEX_WIDTH() + (hexSettings.getHEX_WIDTH()/2);
        float resultY = 0;
        float resultZ = (subChunkLocalGridPosY*subChunkSize) * (float)(hexSettings.getHEX_RADIUS()*1.5);
        
        return new Vector3f(resultX, resultY, resultZ);
    }
}
