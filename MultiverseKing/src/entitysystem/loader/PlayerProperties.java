package entitysystem.loader;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import org.json.simple.JSONObject;

/**
 *
 * @author roah
 */
public class PlayerProperties {

    private PlayerProperties() {
    }

    private static class Holder {

        private final static PlayerProperties instance = new PlayerProperties();
    }

    public static PlayerProperties getInstance(AssetManager manager) {
        if (!initialized) {
            initialize(manager);
        }
        return Holder.instance;
    }
    /**
     */
    private static boolean initialized = false;
    private static int level;

    private static void initialize(AssetManager manager) {
        JSONObject data = (JSONObject) manager.loadAsset(new AssetKey<>("Data/PlayerProperties.json"));
        level = ((Number) data.get("Level")).intValue();
    }

    public int getLevel() {
        return level;
    }
}
