package entitysystem.loader;

import entitysystem.attribut.CardType;
import entitysystem.units.ability.AbilityComponent;
import entitysystem.units.LifeComponent;
import entitysystem.movement.MovementStatsComponent;
import entitysystem.units.CollisionComponent;
import org.json.simple.JSONObject;

/**
 * loader to load entity units from file.
 *
 * @author roah
 */
public class UnitLoader {

    private final MovementStatsComponent uStats;
    private final LifeComponent uLife;
    private final AbilityComponent abilityComp;
    private final CollisionComponent collision;

    UnitLoader(JSONObject data, EntityLoader eLoader) {
        Number speed = (Number) data.get("speed");
        Number movePoint = (Number) data.get("movePoint");
        Number life = (Number) data.get("life");
        
        uLife = new LifeComponent(life.intValue());
        uStats = new MovementStatsComponent(speed.floatValue(), movePoint.byteValue());
        abilityComp = eLoader.loadAbility(data.get("ability").toString());
        
        collision = new CollisionComponent(eLoader.getCollision((JSONObject)data.get("collision")));
    }

    public CollisionComponent getCollisionComp() {
        return collision;
    }

    /**
     * Stats data component of the unit.
     *
     * @return
     */
    public MovementStatsComponent getuStats() {
        return uStats;
    }

    /**
     * Life component of the unit.
     *
     * @return
     */
    public LifeComponent getuLife() {
        return uLife;
    }

    /**
     * Ability component the unit have.
     *
     * @return
     */
    public AbilityComponent getAbilityComp() {
        return abilityComp;
    }
}