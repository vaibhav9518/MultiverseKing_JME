package entitysystem.attribut;

/**
 *
 * @author roah
 */
public enum Rarity {

    /**
     * 4 duplicate card limit by deck, 50 equipped card limit by world.
     */
    COMMON,
    /**
     * 4 duplicate card limit by deck, 12 equipped card limit by world.
     */
    RARE,
    /**
     * 3 duplicate card limit by deck, 9 equipped card limit by world.
     */
    ULTRA_RARE,
    /**
     * No duplicate card, 5 equipped card limit by world.
     */
    EPIQUE,
    /**
     * No duplicate card, 1 equipped card limit by world, 1 use by battle.
     */
    UNIQUE,
    /**
     * No duplicate card, 1 card by Galaxy, 1 use by battle.
     */
    ANTIQUE,
    /**
     * No duplicate card, 1 card by Universe, 1 use by battle.
     */
    UNIVERSE,
    /**
     *
     */
    MULTIVERSE; // No duplicate card, 1 card by Multiverse, 1 use by battle.
}