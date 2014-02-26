/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem.chunksystem;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import hexsystem.HexSettings;
import java.lang.reflect.Array;
import java.util.ArrayList;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
public class MeshManager {

    private float hexSize;    //hex radius.
    private float hexWidth;   //Make life easier.
    
    public MeshManager(HexSettings settings) {
        this.hexSize = settings.getHEX_RADIUS();
        this.hexWidth = FastMath.sqrt(3) * hexSize;
    }
    /**
     * Create a flat grid at specifiate position, specifiate size and specifiate attribut.
     * @param position grid position to start the mesh.
     * @param size number of tile to put in mesh.
     * @param eAttribut texture to use.
     * @return newly created mesh.
     */
    Mesh getMesh(Vector2Int position, Vector2Int size, int eAttribut) {
        return getMesh(position, size, 0, eAttribut);
    }
    /**
     * Create a flat mesh of 1 tile at specifiate position.
     * @param position grid position where the tile need to be.
     * @return newly created tile mesh.
     */
    Mesh getMesh(Vector2Int position) {
        return getMesh(position, new Vector2Int(0, 0), 0, 0);
    }
    /**
     * Create a grid at specifiate position, size, height and texture coordinate.
     * @param position where to start the mesh.
     * @param size number of tile inside the mesh.
     * @param height height of the mesh.
     * @param element texture to use.
     * @return newly generated tile grid mesh.
     */
    Mesh getMesh(Vector2Int position, Vector2Int size, int height, int element){
        Vector3f[] vertices = getVerticesPosition(position, size, height);
        Vector3f[] texCoord = getTexCoord(size, element);
        int[] index = getIndex(size.y, 0);
        
        return setCollisionBound(setAllBuffer(vertices, texCoord, index));
    }
    
    Mesh getMergedMesh(ArrayList<Vector2Int[]> meshParameter) {
        ArrayList<Vector3f[]> vertices = new ArrayList<Vector3f[]>();
        ArrayList<Vector3f[]> texCoord = new ArrayList<Vector3f[]>();
        ArrayList<int[]> index = new ArrayList<int[]>();
        
        int mergedVerticeCount = 0;
        int mergedtextCoordCount = 0;
        int mergedIndexCount = 0;
        
        for(int i = 0; i < meshParameter.size(); i++) {            
            vertices.add(getVerticesPosition(meshParameter.get(i)[0], meshParameter.get(i)[1], meshParameter.get(i)[2].y));
            texCoord.add(getTexCoord(meshParameter.get(i)[1], meshParameter.get(i)[2].x));
            index.add(getIndex(meshParameter.get(i)[1].y, mergedVerticeCount));
            
//            System.out.println(meshParameter.get(i)[0] +" "+ meshParameter.get(i)[1]+ " " + meshParameter.get(i)[2].x); //debug
            
            mergedVerticeCount += vertices.get(i).length;
            mergedtextCoordCount += texCoord.get(i).length;
            mergedIndexCount += index.get(i).length;
        }
        Vector3f[] mergedVertices = new Vector3f[mergedVerticeCount];
        Vector3f[] mergedtextCoord = new Vector3f[mergedtextCoordCount];
        int[] mergedIndex = new int[mergedIndexCount];
        
        mergedVerticeCount = 0;
        mergedtextCoordCount = 0;
        mergedIndexCount = 0;
        for(int i = 0; i < meshParameter.size(); i++){
            System.arraycopy(vertices.get(i), 0, mergedVertices, mergedVerticeCount, vertices.get(i).length);
            System.arraycopy(texCoord.get(i), 0, mergedtextCoord, mergedtextCoordCount, texCoord.get(i).length);
            System.arraycopy(index.get(i), 0, mergedIndex, mergedIndexCount, index.get(i).length);
            
            mergedVerticeCount += vertices.get(i).length;
            mergedtextCoordCount += texCoord.get(i).length;
            mergedIndexCount += index.get(i).length;
        }
        return setCollisionBound(setAllBuffer(mergedVertices, mergedtextCoord, mergedIndex));
    }
    
    private int[] getIndex(int size, int offset){
        int[] index = new int[size*2*3];
        int j = 0;
        for(int i = 0; i < size*4; i+=4){
            index[j] = i+2+offset;
            index[j+1] = i+3+offset;
            index[j+2] = i+offset;
                    
            index[j+3] = i+1+offset;
            index[j+4] = i+2+offset;
            index[j+5] = i+offset;
            
            j+= 6;
        }
        return index;
    }
    
    private Vector3f[] getVerticesPosition(Vector2Int position, Vector2Int size, float height){
//        System.out.println(position +" "+ size + " " + height); //debug
        Vector3f[] vertices = new Vector3f[4*size.y];
        int index = 0;
        for(int i = 0; i < size.y; i++){
            if((position.y+i&1) == 0){ //even
                vertices[index] = new Vector3f(-(hexWidth/2) + (position.x * hexWidth), height, hexSize+((position.y+i)*(hexSize*1.5f)));
                vertices[index+1] = new Vector3f((size.x * hexWidth)-(hexWidth/2)+(position.x * hexWidth), height, hexSize+((position.y+i)*(hexSize*1.5f)) );
                vertices[index+2] = new Vector3f((size.x * hexWidth)-(hexWidth/2)+(position.x * hexWidth), height, -hexSize+((position.y+i)*(hexSize*1.5f)) );
                vertices[index+3] = new Vector3f(-(hexWidth/2) + (position.x * hexWidth), height, -hexSize+((position.y+i)*(hexSize*1.5f)) );
            } else {
                vertices[index] = new Vector3f((position.x * hexWidth), height+0.001f, hexSize+((position.y+i)*(hexSize*1.5f)));
                vertices[index+1] = new Vector3f((size.x * hexWidth)+(position.x * hexWidth), height+0.001f, hexSize+((position.y+i)*(hexSize*1.5f)));
                vertices[index+2] = new Vector3f((size.x * hexWidth)+(position.x * hexWidth), height+0.001f, -hexSize+((position.y+i)*(hexSize*1.5f)));
                vertices[index+3] = new Vector3f((position.x * hexWidth), height+0.001f, -hexSize+((position.y+i)*(hexSize*1.5f)));
            }
            index+= 4;
        }
        
        return vertices;
    }
    
    private Vector3f[] getTexCoord(Vector2Int size, int element){
        Vector3f[] texCoord = new Vector3f[4*size.y];
        int index = 0;
        for(int i = 0; i< size.y; i++){
            texCoord[index] = new Vector3f(0f, 0f, element);
            texCoord[index+1] = new Vector3f(size.x, 0f, element);
            texCoord[index+2] = new Vector3f(size.x, 1f, element);
            texCoord[index+3] = new Vector3f(0f, 1f, element);
            index+=4;
        }        
        return texCoord;
    }
    
    private Mesh setCollisionBound(Mesh meshToUpdate) {
        meshToUpdate.createCollisionData();
        meshToUpdate.updateBound();
        return meshToUpdate;
    }
    
    private Mesh setAllBuffer(Vector3f[] vertices, Vector3f[] texCoord, int[] index){
        Mesh result = new Mesh();
        result.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        result.setBuffer(VertexBuffer.Type.TexCoord, 3, BufferUtils.createFloatBuffer(texCoord));
        result.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(index));
        return result;
    }

    
}