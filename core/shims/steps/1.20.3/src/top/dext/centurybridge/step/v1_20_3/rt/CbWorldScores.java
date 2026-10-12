package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_2218;
import net.minecraft.class_266;
import net.minecraft.class_267;
import net.minecraft.class_269;
import net.minecraft.class_47;
import net.minecraft.class_5670;
import net.minecraft.class_9013;
import net.minecraft.class_9014;
import net.minecraft.class_9015;
import net.minecraft.class_9022;

/**
 * Scoreboard: the 1.20.2 scoreboard API (score owners as names, a Score that is the live stored score) on 1.20.3's
 * ScoreHolder / ScoreAccess one. The stored 1.20.3 Score object is the 1.20.2 Score (CbWorldScoreV, added by
 * CbWorldScoreMixin); it learns its scoreboard, objective and owner here, wherever 1.20.2 code reaches it.
 */
public final class CbWorldScores {

    /**
     * ScoreboardNameProvider.getScoreboardName(context) became getScoreHolder(context). A 1.20.2 call gets the
     * holder's scoreboard name, which is what 1.20.2's providers answered (the entity's scoreboard name, or the
     * fixed name).
     */
    public static String getScoreboardName(class_5670 provider, class_47 context) {
        class_9015 holder = provider.method_55379(context);
        return holder == null ? null : holder.method_5820();
    }

    /** a 1.20.2 score owner (a name), as the holder 1.20.3 asks for: one known by name only */
    public static class_9015 nameHolder(String name) {
        return name == null ? null : class_9015.method_55422(name);
    }

    /** the 1.20.2 owner name of a 1.20.3 holder: its scoreboard name, which 1.20.3 keys scores by */
    public static String holderName(class_9015 holder) {
        return holder == null ? null : holder.method_5820();
    }

    // ------------------------------------------------------------------ the 1.20.2 Score

    /** 1.20.2's Score.SCORE_COMPARATOR: lower scores first, equal ones by owner name, case-insensitive, reversed */
    public static final Comparator<class_267> SCORE_COMPARATOR = (a, b) -> {
        int sa = ((CbWorldScoreV) a).method_1126();
        int sb = ((CbWorldScoreV) b).method_1126();
        if (sa > sb) {
            return 1;
        }
        if (sa < sb) {
            return -1;
        }
        return ((CbWorldScoreV) b).method_1129().compareToIgnoreCase(((CbWorldScoreV) a).method_1129());
    };

    private record Pending(class_269 scoreboard, class_266 objective, String owner) {
    }

    private static final ThreadLocal<Pending> PENDING_SCORE = new ThreadLocal<>();

    /**
     * new Score(scoreboard, objective, owner) of 1.20.2 is 1.20.3's Score(): the constructor's arguments go to the
     * score under construction (CbWorldScoreMixin takes them when Score() returns), a new 1.20.2 score whose first
     * setScore is announced whatever its value.
     */
    public static void pendingScore(class_269 scoreboard, class_266 objective, String owner) {
        PENDING_SCORE.set(new Pending(scoreboard, objective, owner));
    }

    /** gives a score being constructed the 1.20.2 constructor arguments that came just before it, if any */
    public static void takePendingScore(class_267 score) {
        Pending p = PENDING_SCORE.get();
        if (p != null) {
            PENDING_SCORE.remove();
            ((CbWorldScoreV) score).cb$attach(p.scoreboard(), p.objective(), p.owner(), true);
        }
    }

    /**
     * the stored score 1.20.3 answers for that holder and objective, as the 1.20.2 Score (null when none); one
     * not reached by 1.20.2 code before learns where it is stored now
     */
    private static class_267 stored(class_269 scoreboard, class_9015 holder, class_266 objective, boolean forceUpdate) {
        class_9013 info = scoreboard.method_55430(holder, objective);
        if (info == null) {
            return null;
        }
        if (!(info instanceof class_267 score)) {
            throw new IllegalStateException("Scoreboard " + scoreboard.getClass().getName() + " answers a "
                + info.getClass().getName() + " for a stored score, not the Score 1.20.2 code works on");
        }
        ((CbWorldScoreV) score).cb$attach(scoreboard, objective, holder.method_5820(), forceUpdate);
        return score;
    }

    /**
     * 1.20.2's getOrCreatePlayerScore(owner, objective): the stored score, created when missing -- and a new one
     * was set to 0 at once, which announced it, as 1.20.3's own creation does not.
     */
    public static class_267 getOrCreatePlayerScore(class_269 scoreboard, String owner, class_266 objective) {
        class_9015 holder = class_9015.method_55422(owner);
        boolean existed = scoreboard.method_55430(holder, objective) != null;
        scoreboard.method_1180(holder, objective);
        class_267 score = stored(scoreboard, holder, objective, !existed);
        if (!existed) {
            ((CbWorldScoreV) score).method_1128(0);
        }
        return score;
    }

    /** 1.20.2's hasPlayerScore(owner, objective) */
    public static boolean hasPlayerScore(class_269 scoreboard, String owner, class_266 objective) {
        return scoreboard.method_55430(class_9015.method_55422(owner), objective) != null;
    }

    /**
     * 1.20.2's getPlayerScores(owner): the owner's scores by objective. 1.20.2 handed out its own map (or a new
     * empty one); this is a map of the same live scores, built for the call.
     */
    public static Map<class_266, class_267> getPlayerScores(class_269 scoreboard, String owner) {
        class_9015 holder = class_9015.method_55422(owner);
        Map<class_266, class_267> out = new HashMap<>();
        for (class_266 objective : scoreboard.method_1166(holder).keySet()) {
            class_267 score = stored(scoreboard, holder, objective, false);
            if (score != null) {
                out.put(objective, score);
            }
        }
        return out;
    }

    /**
     * 1.20.2's getPlayerScores(objective): every owner's score in it, sorted by SCORE_COMPARATOR. 1.20.3 kept the
     * method's name and erased type but answers PlayerScoreEntry records, which 1.20.2 code would cast to Score.
     */
    public static Collection<class_267> getPlayerScoresByObjective(class_269 scoreboard, class_266 objective) {
        List<class_267> out = new ArrayList<>();
        for (class_9015 holder : scoreboard.method_1178()) {
            class_267 score = stored(scoreboard, holder, objective, false);
            if (score != null) {
                out.add(score);
            }
        }
        out.sort(SCORE_COMPARATOR);
        return out;
    }

    /**
     * 1.20.2's getTrackedPlayers(): the owners' names. 1.20.3 kept the method's name and erased type but answers
     * ScoreHolders, which 1.20.2 code would cast to String.
     */
    public static Collection<String> getTrackedPlayers(class_269 scoreboard) {
        List<String> out = new ArrayList<>();
        for (class_9015 holder : scoreboard.method_1178()) {
            out.add(holder.method_5820());
        }
        return out;
    }

    /** 1.20.2's resetPlayerScore(owner, objective): one score, or with a null objective every score of the owner */
    public static void resetPlayerScore(class_269 scoreboard, String owner, class_266 objective) {
        class_9015 holder = class_9015.method_55422(owner);
        if (objective == null) {
            scoreboard.method_55429(holder);
        } else {
            scoreboard.method_1155(holder, objective);
        }
    }

    /** a 1.20.2 score changed: its scoreboard's onScoreChanged, with the holder and objective the score is stored under */
    public static void scoreChanged(class_269 scoreboard, class_267 score) {
        ((CbWorldScoreboardV) scoreboard).cb$announce(holderOf(score), objectiveOf(score), score);
    }

    /** a stored score's display or number format changed: announced like a change of its value */
    static void announce(class_267 score) {
        scoreChanged(((CbWorldScoreV) score).method_1122(), score);
    }

    /** the holder a 1.20.2 score is stored under (for a 1.20.2 call of onScoreChanged(Score)) */
    public static class_9015 holderOf(class_267 score) {
        return class_9015.method_55422(((CbWorldScoreV) score).method_1129());
    }

    /** the objective a 1.20.2 score is stored under */
    public static class_266 objectiveOf(class_267 score) {
        return ((CbWorldScoreV) score).method_1127();
    }

    /**
     * 1.20.3's onScoreChanged(holder, objective, score) synthesized in a scoreboard class converted from 1.20.2 that
     * overrides onScoreChanged(Score): the score learns where it is stored, then the override hears of it.
     */
    public static void scoreChangedBridge(CbWorldScoreChangeListener scoreboard, class_9015 holder, class_266 objective,
                                          class_267 score) {
        ((CbWorldScoreV) score).cb$attach((class_269) scoreboard, objective, holder.method_5820(), false);
        scoreboard.method_1176(score);
    }

    // ------------------------------------------------------------------ objectives

    /** Objective / Scoreboard.addObjective: 1.20.2 objectives had no display auto-update */
    public static boolean noDisplayAutoUpdate() {
        return false;
    }

    /** ...and no number format: null, the default 1.20.3's own objectives start with */
    public static class_9022 defaultNumberFormat() {
        return null;
    }

    // ------------------------------------------------------------------ OperationArgument.Operation

    /** a 1.20.3 ScoreAccess as a 1.20.2 Score (the score itself when the access is one of {@link #access}) */
    public static class_267 scoreOf(class_9014 access) {
        if (access instanceof CbWorldScoreAccessView view) {
            return view.score();
        }
        return new CbWorldAccessScore(access);
    }

    /** a 1.20.2 Score as a 1.20.3 ScoreAccess (the access itself when the score is one of {@link #scoreOf}) */
    public static class_9014 access(class_267 score) {
        if (score instanceof CbWorldAccessScore s) {
            return s.access();
        }
        return new CbWorldScoreAccessView(score);
    }

    /** a 1.20.2 lambda or method reference implementing Operation, as the 1.20.3 Operation */
    public static class_2218.class_2219 operation(CbWorldScoreOperation legacy) {
        return (target, source) -> legacy.apply(scoreOf(target), scoreOf(source));
    }

    private CbWorldScores() {
    }
}
