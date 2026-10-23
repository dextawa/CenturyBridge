package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1282;
import net.minecraft.class_1291;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_3218;

/**
 * 1.21.1's MobEffect hooks called without a level: 1.21.2 runs them with the entity's ServerLevel and only on
 * the server (its effect ticking, instant effects, mob removal and hurt are server-side). A 1.21.1 call is passed
 * the entity's level; on a client entity, where 1.21.2 never runs them, it does what 1.21.2 does there: nothing
 * (applyEffectTick answers true, "keep the effect", as the default does).
 */
public final class CbItemEffects {

    private static class_3218 server(class_1297 entity) {
        return entity.method_37908() instanceof class_3218 s ? s : null;
    }

    /** applyEffectTick(entity, amplifier) */
    public static boolean method_5572(class_1291 effect, class_1309 entity, int amplifier) {
        class_3218 level = server(entity);
        return level == null || effect.method_5572(level, entity, amplifier);
    }

    /** applyInstantenousEffect(source, indirect, entity, amplifier, health) */
    public static void method_5564(class_1291 effect, class_1297 source, class_1297 indirect, class_1309 entity, int amplifier, double health) {
        class_3218 level = server(entity);
        if (level != null) {
            effect.method_5564(level, source, indirect, entity, amplifier, health);
        }
    }

    /** onMobRemoved(entity, amplifier, reason) */
    public static void method_58617(class_1291 effect, class_1309 entity, int amplifier, class_1297.class_5529 reason) {
        class_3218 level = server(entity);
        if (level != null) {
            effect.method_58617(level, entity, amplifier, reason);
        }
    }

    /** onMobHurt(entity, amplifier, source, amount) */
    public static void method_58614(class_1291 effect, class_1309 entity, int amplifier, class_1282 source, float amount) {
        class_3218 level = server(entity);
        if (level != null) {
            effect.method_58614(level, entity, amplifier, source, amount);
        }
    }

    // ---- frameDefaults: the level a converted override's rewritten super call passes when the override was
    // entered directly (the mod's own call) rather than from 1.21.2's shape: the entity's ServerLevel -- on a
    // client entity, where 1.21.2 never calls these, null, which MobEffect's and MobEffectInstance's own bodies
    // the super call reaches there do not read

    public static class_3218 level(class_1291 effect, class_1309 entity, int amplifier) {
        return server(entity);
    }

    public static class_3218 level(class_1291 effect, class_1297 source, class_1297 indirect, class_1309 entity, int amplifier,
                                   double health) {
        return server(entity);
    }

    public static class_3218 level(class_1291 effect, class_1309 entity, int amplifier, class_1297.class_5529 reason) {
        return server(entity);
    }

    public static class_3218 level(class_1291 effect, class_1309 entity, int amplifier, class_1282 source, float amount) {
        return server(entity);
    }

    public static class_3218 level(net.minecraft.class_1293 instance, class_1309 entity, class_1297.class_5529 reason) {
        return server(entity);
    }

    public static class_3218 level(net.minecraft.class_1293 instance, class_1309 entity, class_1282 source, float amount) {
        return server(entity);
    }

    private CbItemEffects() {
    }
}
