package entitysystem.attribut;

/**
 * 
 * @author roah
 */
public enum CardType {

    /**
     * Used for card who affect the way unit behave under defined condition.
     */
    ABILITY,
    /**
     * Used for card who affect the Titan Equipement.
     */
    EQUIPEMENT,
    /**
     * Used for card who generate unit on the field.
     */
    UNIT,
    /**
     * Used for card who generate an effect on the field.
     */
    SPELL,
    /**
     * Used for card who generate object on the field, 
     * object got activated under difined condition.
     * (object can be unit... or, unit when sleepin then spell when activated...)
     */
    TRAP;
    /**
     * Used for card who affect the way unit move on the field.
     */
//    PATHFIND;
}