package top.dext.centurybridge.step.v1_20_5.rt;

import top.dext.centurybridge.step.v1_20_5.facade.class_1310;

/**
 * 1.20.4's MobType on 1.20.5's entity type tags.
 * <p>
 * 1.20.4 asked an entity {@code getMobType()} -- UNDEAD for the undead mobs, ARTHROPOD for spiders,
 * bees, silverfish and endermites, ILLAGER for illagers, WATER for water mobs, UNDEFINED otherwise -- and
 * a mod declared what its entity is by overriding it. 1.20.5 asks the entity's type for the tags
 * #undead, #arthropod, #illager and #aquatic and the ones made from them (inverted healing and harm,
 * ignores poison and regeneration, wither friends, illager friends, sensitive to smite / bane of
 * arthropods / impaling). For an entity whose class leaves getMobType alone (every vanilla one) the
 * cover on LivingEntity answers from those tags, exactly what 1.20.4 answered for the vanilla classes;
 * an entity whose class overrides it (a 1.20.4 mod's) answers its override, and the places 1.20.5 asks
 * the tags about a live entity ask that answer instead ({@link #declared}).
 */
public final class CbRestMobType {

    /**
     * Added to a mod entity class that overrides 1.20.4's getMobType() (adapter hook): what its class
     * declares (the cover on LivingEntity, mixin/CbRestLivingEntityMixin, answers the type's tags)
     */
    public interface Declares {
        class_1310 method_6046();
    }

    /** the 1.20.4 MobType of an entity type, from the 1.20.5 tags that replaced it */
    public static class_1310 of(net.minecraft.class_1299<?> type) {
        if (type.method_20210(net.minecraft.class_3483.field_46232)) {
            return class_1310.field_6289;
        }
        if (type.method_20210(net.minecraft.class_3483.field_48289)) {
            return class_1310.field_6293;
        }
        if (type.method_20210(net.minecraft.class_3483.field_48287)) {
            return class_1310.field_6291;
        }
        if (type.method_20210(net.minecraft.class_3483.field_48288)) {
            return class_1310.field_6292;
        }
        return class_1310.field_6290;
    }

    /**
     * The MobType an entity's class declares by a 1.20.4 override of getMobType, or null when the
     * class leaves it to the entity's type (then 1.20.5's tags decide, as for every vanilla entity).
     */
    public static class_1310 declared(net.minecraft.class_1297 entity) {
        if (entity instanceof Declares declares) {
            return declares.method_6046();
        }
        return null;
    }

    /** an entity's 1.20.4 MobType: what its class declares, else what its type's tags say */
    public static class_1310 mobType(net.minecraft.class_1297 entity) {
        class_1310 d = declared(entity);
        return d != null ? d : of(entity.method_5864());
    }

    // ------------------------------------------------------------------ the entity being attacked

    /** stands for "no target" on the stack below (ArrayDeque holds no null) */
    private static final Object NO_TARGET = new Object();

    /** the entities whose damage bonus is being evaluated on this thread, innermost first */
    private static final ThreadLocal<java.util.ArrayDeque<Object>> TARGETS = ThreadLocal.withInitial(java.util.ArrayDeque::new);

    /**
     * EnchantmentHelper.getDamageBonus(stack, type) evaluated with {@code target} as the entity being
     * attacked. 1.20.4 handed the damage bonus the target's MobType; 1.20.5 hands it the target's
     * EntityType, which knows nothing of a class declaring a MobType. The attacks that ask for it
     * (Player.attack, Mob.doHurtTarget, ThrownTrident.onHitEntity: mixin/CbRestAttackMixins) come
     * through here, so the enchantments' side can ask {@link #targetDeclared}. Per thread and re-entrant
     * (an attack inside a bonus evaluation keeps its own target); the target is dropped again however
     * the evaluation ends.
     */
    public static float cbDamageBonus(net.minecraft.class_1799 stack, net.minecraft.class_1299<?> type, net.minecraft.class_1297 target) {
        java.util.ArrayDeque<Object> s = TARGETS.get();
        s.push(target == null ? NO_TARGET : target);
        try {
            return net.minecraft.class_1890.method_8218(stack, type);
        } finally {
            s.pop();
        }
    }

    /**
     * The MobType the class of the entity being attacked declares, when a damage bonus is being
     * evaluated for it on this thread and {@code type} is that entity's type; else null (no attack
     * under way, a call about another type, or a class that leaves its MobType to the type's tags).
     */
    public static class_1310 targetDeclared(net.minecraft.class_1299<?> type) {
        Object t = TARGETS.get().peek();
        if (t instanceof net.minecraft.class_1297 target && target.method_5864() == type) {
            return declared(target);
        }
        return null;
    }

    private CbRestMobType() {
    }
}
