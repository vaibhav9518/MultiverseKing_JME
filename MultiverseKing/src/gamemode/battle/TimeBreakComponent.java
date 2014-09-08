package gamemode.battle;

import com.simsilica.es.PersistentComponent;

/**
 *
 * @author roah
 */
public class TimeBreakComponent implements PersistentComponent {

    private final boolean timeBreak;

    public TimeBreakComponent() {
        this.timeBreak = false;
    }

    public TimeBreakComponent(boolean timeBreak) {
        this.timeBreak = timeBreak;
    }

    public boolean isTimeBreak() {
        return timeBreak;
    }
}
