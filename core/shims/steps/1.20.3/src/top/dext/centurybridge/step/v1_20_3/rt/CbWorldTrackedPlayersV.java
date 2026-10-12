package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.Collection;

/**
 * Marks a Scoreboard class converted from 1.20.2 that overrides getTrackedPlayers() (its own method implements
 * it). 1.20.3 kept the method's name and erased type but answers ScoreHolders where 1.20.2 answered names: such a
 * class answers names, which 1.20.2 calls take as they are and 1.20.3's own callers get as holders
 * (CbWorldScores.getTrackedPlayers / trackedHolders).
 */
public interface CbWorldTrackedPlayersV {
    Collection<String> method_1178();
}
