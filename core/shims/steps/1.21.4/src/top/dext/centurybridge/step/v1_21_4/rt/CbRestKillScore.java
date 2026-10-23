package top.dext.centurybridge.step.v1_21_4.rt;

import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1309;

/**
 * 1.21.3's kill score: LivingEntity.deathScore (protected int, gone in 1.21.4) and
 * Entity.awardKillScore(killed, score, source), which 1.21.4 shortened to (killed, source).
 * <p>
 * 1.21.3's LivingEntity.die (and ServerPlayer.die) called killer.awardKillScore(this, this.deathScore, source)
 * only when deathScore >= 0, and ServerPlayer.awardKillScore added the score to the player's (increaseScore).
 * 1.21.4 keeps Player.score and increaseScore but dropped both: CbRestDeathScoreMixin gives every living
 * entity its deathScore again (0, as 1.21.3's default), CbRestKillScoreMixin skips the award for a negative
 * score and adds the score in ServerPlayer.awardKillScore.
 * <p>
 * The score travels as 1.21.3 passed it: a call or a mod's super call naming one ({@link #method_5716},
 * {@link #superAwardKillScore}) hands it on to 1.21.4's two-argument method through {@link #PENDING};
 * without one it is the killed entity's deathScore, the value 1.21.3's die passed.
 */
public final class CbRestKillScore {

    private CbRestKillScore() {
    }

    /** LivingEntity's deathScore, put back by CbRestDeathScoreMixin */
    public interface DeathScore {
        int cb$deathScore();

        void cb$setDeathScore(int score);
    }

    private static final ThreadLocal<Integer> PENDING = new ThreadLocal<>();

    /** the score 1.21.3 passed with this award: the one a 1.21.3-shaped call named, else the killed's deathScore */
    public static int scoreFor(class_1297 killed) {
        Integer p = PENDING.get();
        if (p != null) {
            return p;
        }
        return killed instanceof DeathScore d ? d.cb$deathScore() : 0;
    }

    /** whether 1.21.3's die would have awarded this kill at all (deathScore >= 0) */
    public static boolean awards(class_1309 killed) {
        return ((DeathScore) killed).cb$deathScore() >= 0;
    }

    /** LivingEntity.deathScore read */
    public static int field_6232(class_1309 entity) {
        return ((DeathScore) entity).cb$deathScore();
    }

    /** LivingEntity.deathScore write */
    public static void field_6232(class_1309 entity, int score) {
        ((DeathScore) entity).cb$setDeathScore(score);
    }

    /** a mod's call entity.awardKillScore(killed, score, source): 1.21.4's virtual call, carrying the score */
    public static void method_5716(class_1297 entity, class_1297 killed, int score, class_1282 source) {
        Integer before = PENDING.get();
        PENDING.set(score);
        try {
            entity.method_5716(killed, source);
        } finally {
            PENDING.set(before);
        }
    }

    /** a mod's call serverPlayer.awardKillScore(killed, score, source) (ServerPlayer declared it in 1.21.3) */
    public static void method_5716(net.minecraft.class_3222 player, class_1297 killed, int score, class_1282 source) {
        method_5716((class_1297) player, killed, score, source);
    }

    /** a 1.21.3 override of awardKillScore(killed, score, source), as vanilla's (killed, source) reaches it */
    public interface Legacy {
        void method_5716(class_1297 killed, int score, class_1282 source);
    }

    /** the synthesized awardKillScore(killed, source) of a class overriding 1.21.3's shape */
    public static void awardKillScore(Legacy self, class_1297 killed, class_1282 source) {
        self.method_5716(killed, scoreFor(killed), source);
    }

    /** the vanilla awardKillScore above a mod class, reached by its super call */
    public interface Super {
        void cb$super$method_5716(class_1297 killed, class_1282 source);
    }

    /** a mod's super.awardKillScore(killed, score, source): vanilla's implementation, carrying the score */
    public static void superAwardKillScore(Super self, class_1297 killed, int score, class_1282 source) {
        Integer before = PENDING.get();
        PENDING.set(score);
        try {
            self.cb$super$method_5716(killed, source);
        } finally {
            PENDING.set(before);
        }
    }
}
