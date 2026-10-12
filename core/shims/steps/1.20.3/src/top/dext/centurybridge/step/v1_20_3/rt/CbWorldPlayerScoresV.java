package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.Collection;
import net.minecraft.class_266;
import net.minecraft.class_267;

/**
 * Marks a Scoreboard class converted from 1.20.2 that overrides getPlayerScores(objective) (its own method
 * implements it). 1.20.3 kept the method's name and erased type but answers PlayerScoreEntry records where 1.20.2
 * answered Scores: such a class answers Scores, which 1.20.2 calls take as they are and 1.20.3's own callers get
 * as entries (CbWorldScores.getPlayerScoresByObjective / scoreEntries).
 */
public interface CbWorldPlayerScoresV {
    Collection<class_267> method_1184(class_266 objective);
}
