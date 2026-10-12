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

    /** what a LivingEntity carries again (mixin/CbRestLivingEntityMixin): getMobType(), called virtually */
    public interface Api {
        class_1310 cbRest$mobType();
    }

    /** whether a class overrides 1.20.4's getMobType (the cover lives on LivingEntity itself) */
    private static final ClassValue<Boolean> OVERRIDES = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> c) {
            try {
                return c.getMethod("method_6046").getDeclaringClass() != net.minecraft.class_1309.class;
            } catch (NoSuchMethodException e) {
                return false;
            }
        }
    };

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
        if (entity instanceof net.minecraft.class_1309 living && OVERRIDES.get(entity.getClass())) {
            return ((Api) living).cbRest$mobType();
        }
        return null;
    }

    /** an entity's 1.20.4 MobType: what its class declares, else what its type's tags say */
    public static class_1310 mobType(net.minecraft.class_1297 entity) {
        class_1310 d = declared(entity);
        return d != null ? d : of(entity.method_5864());
    }

    private CbRestMobType() {
    }
}
