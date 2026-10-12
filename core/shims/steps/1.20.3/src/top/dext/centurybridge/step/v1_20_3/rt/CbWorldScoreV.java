package top.dext.centurybridge.step.v1_20_3.rt;

import net.minecraft.class_266;
import net.minecraft.class_269;

/**
 * What CbWorldScoreMixin adds to 1.20.3's Score: 1.20.2's Score was the stored score of one owner in one
 * objective of one scoreboard, and changing it announced the change to that scoreboard; 1.20.3's is the bare
 * stored value. The same stored object serves as the 1.20.2 Score: it gets that owner, objective and scoreboard
 * the first time 1.20.2 code reaches it (CbWorldScores), and 1.20.2's methods come back on it (covers).
 */
public interface CbWorldScoreV {

    /** gives the score the scoreboard, objective and owner it is stored under, unless it has them already */
    void cb$attach(class_269 scoreboard, class_266 objective, String owner, boolean forceUpdate);

    boolean cb$attached();

    /** 1.20.2 getScore() */
    int method_1126();

    /** 1.20.2 setScore(score): announces the change (and a new score's first value) to the scoreboard */
    void method_1128(int score);

    /** 1.20.2 add(amount): refused on a read-only objective */
    void method_1124(int amount);

    /** 1.20.2 increment() */
    void method_1130();

    /** 1.20.2 reset(): the score set to 0 */
    void method_1132();

    /** 1.20.2 getObjective() */
    class_266 method_1127();

    /** 1.20.2 getOwner(): the owner's scoreboard name */
    String method_1129();

    /** 1.20.2 getScoreboard() */
    class_269 method_1122();
}
