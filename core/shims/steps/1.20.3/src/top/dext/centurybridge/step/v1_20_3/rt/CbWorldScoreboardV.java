package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.class_266;
import net.minecraft.class_267;
import net.minecraft.class_274;
import net.minecraft.class_9015;

/**
 * What CbWorldScoreboardMixin adds to Scoreboard: 1.20.2's player-score methods that 1.20.3 replaced by ScoreHolder
 * ones of another shape (covers, each with its 1.20.2 body on the 1.20.3 API), and the scoreboard's own
 * onScoreChanged for the layer to call.
 */
public interface CbWorldScoreboardV {

    /** 1.20.2 getOrCreatePlayerScore(owner, objective) */
    class_267 method_1180(String owner, class_266 objective);

    /** 1.20.2 getPlayerScores(owner): the owner's scores by objective */
    Map<class_266, class_267> method_1166(String owner);

    /** 1.20.2 hasPlayerScore(owner, objective) */
    boolean method_1183(String owner, class_266 objective);

    /** 1.20.2 resetPlayerScore(owner, objective): every score of the owner when the objective is null */
    void method_1155(String owner, class_266 objective);

    /** 1.20.2 forAllObjectives(criteria, owner, action) */
    void method_1162(class_274 criteria, String owner, Consumer<class_267> action);

    /** the scoreboard's 1.20.3 onScoreChanged(holder, objective, score) (protected there) */
    void cb$announce(class_9015 holder, class_266 objective, class_267 score);
}
