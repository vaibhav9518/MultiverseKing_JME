package entitysystem.field;

import com.simsilica.es.PersistentComponent;
import java.util.ArrayList;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;

/**
 *
 * @author roah
 */
public class CollisionComponent implements PersistentComponent {

    /**
     * Collision layer of the entity and collision size.
     */
    private final Collision collision;

    /**
     * Create a new collision component for a 1 Hex size unit defined layer.
     */
    public CollisionComponent(Byte layer) {
        collision = new Collision();
        ArrayList<HexCoordinate> data = new ArrayList<HexCoordinate>();
        data.add(new HexCoordinate(HexCoordinate.OFFSET, new Vector2Int()));
        collision.addLayer((byte) 0, collision.new CollisionData((byte) 0, data));
    }

    public CollisionComponent(Collision collision) {
        this.collision = collision;
    }

    public Byte[] getUsedLayers() {
        return collision.getLayers();
    }

    public ArrayList<HexCoordinate> getCollisionOnLayer(Byte layer) {
        return collision.getCollisionLayer(layer).getCoord();
    }
}
