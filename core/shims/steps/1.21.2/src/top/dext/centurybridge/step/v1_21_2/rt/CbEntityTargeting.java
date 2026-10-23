package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.class_1267;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_3218;
import net.minecraft.class_4051;
import net.minecraft.class_4824;
import net.minecraft.class_4828;
import net.minecraft.class_7893;
import top.dext.centurybridge.step.v1_21_2.mixin.CbEntityTargetingAccess;

/**
 * 1.21.1's targeting API on 1.21.2.
 *
 * <p>1.21.1's TargetingConditions.test(tester, target) read the target's own level for the peaceful check;
 * 1.21.2's test(level, tester, target) reads the level passed in, and its selector is a
 * TargetingConditions.Selector test(entity, level) where 1.21.1's was a Predicate&lt;LivingEntity&gt;. 1.21.1's AI
 * factories StartAttacking.create / StopAttackingIfTargetInvalid.create took Predicates, Functions and
 * BiConsumers of the mob where 1.21.2's take (level, mob) interfaces.
 *
 * <p>A 1.21.1 predicate becomes a 1.21.2 selector that ignores the level ({@link #selector}); a 1.21.2 selector
 * read back as a 1.21.1 predicate asks it with the entity's own level ({@link #predicate}) -- what 1.21.2's own
 * selectors read (two of the 26 vanilla ones read the level: GoatAi's mob-griefing rule and world border,
 * EnderMan's isAngryAt, both the level the entity is in). Each undoes the other: a predicate made from a
 * selector gives back that selector, and the other way round.
 */
public final class CbEntityTargeting {

    // ---------------------------------------------------------------------------------- selector <-> predicate
    /** a 1.21.1 Predicate&lt;LivingEntity&gt; as 1.21.2's Selector: the level is not the predicate's to read */
    public static class_4051.class_10254 selector(Predicate<class_1309> predicate) {
        if (predicate == null) {
            return null;
        }
        if (predicate instanceof FromSelector f) {
            return f.selector;
        }
        return new FromPredicate(predicate);
    }

    /** a 1.21.2 Selector as 1.21.1's Predicate&lt;LivingEntity&gt;: asked with the entity's own level */
    public static Predicate<class_1309> predicate(class_4051.class_10254 selector) {
        if (selector == null) {
            return null;
        }
        if (selector instanceof FromPredicate f) {
            return f.predicate;
        }
        return new FromSelector(selector);
    }

    private record FromPredicate(Predicate<class_1309> predicate) implements class_4051.class_10254 {
        @Override
        public boolean method_18303(class_1309 entity, class_3218 level) {
            return predicate.test(entity);
        }
    }

    private record FromSelector(class_4051.class_10254 selector) implements Predicate<class_1309> {
        @Override
        public boolean test(class_1309 entity) {
            return selector.method_18303(entity, CbEntityLevels.serverLevel(entity));
        }
    }

    // ---------------------------------------------------------------------------------- TargetingConditions
    /** 1.21.1 TargetingConditions.selector(predicate): 1.21.2's with the predicate as a selector */
    public static class_4051 selector(class_4051 self, Predicate<class_1309> predicate) {
        return self.method_18420(selector(predicate));
    }

    /**
     * 1.21.1 TargetingConditions.test(tester, target), on any level: the 1.21.1 body, step for step -- the selector
     * asked with the target's own level, the peaceful check on the target's level's difficulty (1.21.2 reads the
     * level passed in; 1.21.1 read target.level()).
     */
    public static boolean test(class_4051 self, class_1309 tester, class_1309 target) {
        CbEntityTargetingAccess c = (CbEntityTargetingAccess) self;
        if (tester == target) {
            return false;
        }
        if (!target.method_36608()) {
            return false;
        }
        class_4051.class_10254 sel = c.cb$selector();
        if (sel != null && !sel.method_18303(target, CbEntityLevels.serverLevel(target))) {
            return false;
        }
        if (tester == null) {
            return !c.cb$isCombat() || target.method_33190() && target.method_37908().method_8407() != class_1267.field_5801;
        }
        if (c.cb$isCombat() && (!tester.method_18395(target) || !tester.method_5973(target.method_5864()) || tester.method_5722(target))) {
            return false;
        }
        double range = c.cb$range();
        if (range > 0.0) {
            double visibility = c.cb$testInvisible() ? target.method_18390(tester) : 1.0;
            double max = Math.max(range * visibility, 2.0);
            double d = tester.method_5649(target.method_23317(), target.method_23318(), target.method_23321());
            if (d > max * max) {
                return false;
            }
        }
        return !c.cb$checkLineOfSight() || !(tester instanceof class_1308 mob) || mob.method_5985().method_6369(target);
    }

    /**
     * 1.21.1 Fox.FoxAlertableEntitiesSelector.test(entity): 1.21.2's test(entity, level), which reads no level
     * (its body is 1.21.1's)
     */
    public static boolean foxAlertable(net.minecraft.class_4019.class_4022 self, class_1309 entity) {
        return self.method_18303(entity, CbEntityLevels.serverLevel(entity));
    }

    /** 1.21.1 Wolf.PREY_SELECTOR (a Predicate): 1.21.2's Selector read as one, the same object on every read */
    public static Predicate<class_1309> wolfPreySelector() {
        return Holders.WOLF_PREY;
    }

    /** 1.21.1 Turtle.BABY_ON_LAND_SELECTOR (a Predicate): as above */
    public static Predicate<class_1309> turtleBabyOnLandSelector() {
        return Holders.TURTLE_BABY;
    }

    /** read on first use: the vanilla classes initialize their selectors in their own static initializers */
    private static final class Holders {
        static final Predicate<class_1309> WOLF_PREY = predicate(net.minecraft.class_1493.field_18004);
        static final Predicate<class_1309> TURTLE_BABY = predicate(net.minecraft.class_1481.field_6921);
    }

    // ---------------------------------------------------------------------------------- AI factories
    /** 1.21.1 StartAttacking.create(targetFinder) */
    public static <E extends class_1308> class_7893<E> startAttacking(Function<E, Optional<? extends class_1309>> finder) {
        return class_4824.method_47119((level, mob) -> finder.apply(mob));
    }

    /** 1.21.1 StartAttacking.create(canAttack, targetFinder) */
    public static <E extends class_1308> class_7893<E> startAttacking(Predicate<E> canAttack, Function<E, Optional<? extends class_1309>> finder) {
        return class_4824.method_47120((level, mob) -> canAttack.test(mob), (level, mob) -> finder.apply(mob));
    }

    /** 1.21.1 StopAttackingIfTargetInvalid.create(onTargetErased) */
    public static <E extends class_1308> class_7893<E> stopAttacking(BiConsumer<E, class_1309> onErased) {
        return class_4828.method_47137((level, mob, target) -> onErased.accept(mob, target));
    }

    /** 1.21.1 StopAttackingIfTargetInvalid.create(stopAttackingWhen) */
    public static <E extends class_1308> class_7893<E> stopAttacking(Predicate<class_1309> stopWhen) {
        return class_4828.method_47138((level, target) -> stopWhen.test(target));
    }

    /** 1.21.1 StopAttackingIfTargetInvalid.create(stopAttackingWhen, onTargetErased, canGrowTiredOfTryingToReachTarget) */
    public static <E extends class_1308> class_7893<E> stopAttacking(Predicate<class_1309> stopWhen, BiConsumer<E, class_1309> onErased, boolean tired) {
        return class_4828.method_47139((level, target) -> stopWhen.test(target), (level, mob, target) -> onErased.accept(mob, target), tired);
    }

    private CbEntityTargeting() {
    }
}
