package top.dext.centurybridge.step.v1_20_3.rt;

import net.minecraft.class_267;

/**
 * The 1.20.2 shape of Scoreboard.onScoreChanged(Score), as a scoreboard class converted from 1.20.2 overrides it:
 * the hook synthesized there for 1.20.3's onScoreChanged(holder, objective, score) reaches the override through
 * this (CbWorldScores.scoreChangedBridge).
 */
public interface CbWorldScoreChangeListener {
    void method_1176(class_267 score);
}
