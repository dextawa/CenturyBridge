package top.dext.centurybridge.step.v1_21_6.rt;

import net.minecraft.class_1297;

/**
 * 1.21.5's leash hooks on 1.21.6 (javap of both). 1.21.5's Leashable.tickLeash measured distanceTo(holder), asked
 * handleLeashAtDistance(holder, d) and returned when it answered false (PathfinderMob restricted itself to the holder,
 * a sitting TamableAnimal dropped the leash past 10 and answered false, AbstractHorse / Camel stopped eating / dashing
 * past 6), then broke the leash past 10, pulled with elasticRangeLeashBehaviour(holder, d) past 6 (AbstractBoat had
 * its own pull) and followed the slow-fall check, or ran closeRangeLeashBehaviour. 1.21.6 rewrote the physics
 * (whenLeashedTo, a 12-block snap, checkElasticInteractions / onElasticLeashPull, angular damping) and dropped both
 * hooks. The layer restores them (CbLeashRestoredMixin and the vanilla overrides) and CbLeashRouteMixin consults them
 * in 1.21.6's tickLeash only for an entity whose class takes the hook from a converted mod (its own override) or
 * whose hook a converted mod mixed ({@link CbMixedMembers}); every other entity runs 1.21.6's tick unchanged.
 */
public final class CbLeash {

    private CbLeash() {
    }

    /** the restored hooks, as the route calls them */
    public interface Legacy {
        boolean method_60956(class_1297 holder, float distance);

        void method_61162(class_1297 holder, float distance);
    }

    static final String HANDLE = "method_60956";
    static final String HANDLE_DESC = "(Lnet/minecraft/class_1297;F)Z";
    static final String ELASTIC = "method_61162";
    static final String ELASTIC_DESC = "(Lnet/minecraft/class_1297;F)V";

    /** the classes whose handleLeashAtDistance the layer restores (1.21.5's declarations) */
    static final java.util.Set<Class<?>> VANILLA_HANDLE = java.util.Set.of(net.minecraft.class_9817.class,
        net.minecraft.class_1314.class, net.minecraft.class_1321.class, net.minecraft.class_1496.class, net.minecraft.class_7689.class);
    /** the classes whose elasticRangeLeashBehaviour the layer restores */
    static final java.util.Set<Class<?>> VANILLA_ELASTIC = java.util.Set.of(net.minecraft.class_9817.class, net.minecraft.class_10255.class);

    /**
     * the restored declarations whose 1.21.5 body super-calls another restored one (javap: TamableAnimal's, not
     * sitting, reached PathfinderMob's; AbstractHorse's, Camel's and PathfinderMob's make no super call)
     */
    static final java.util.Map<Class<?>, Class<?>> HANDLE_SUPER = java.util.Map.of(net.minecraft.class_1321.class, net.minecraft.class_1314.class);

    private static final ClassValue<Boolean> HANDLE_ROUTE = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            return route(type, HANDLE, HANDLE_DESC, VANILLA_HANDLE, HANDLE_SUPER);
        }
    };

    private static final ClassValue<Boolean> ELASTIC_ROUTE = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            return route(type, ELASTIC, ELASTIC_DESC, VANILLA_ELASTIC, java.util.Map.of());
        }
    };

    /**
     * Whether the class takes the hook from a converted mod: the declaring class of the public method dispatch reaches
     * is not one of the layer's restored declarations (a mod's override), or it is one and a converted mod mixed it or
     * a restored declaration its 1.21.5 body super-calls.
     */
    private static boolean route(Class<?> type, String name, String desc, java.util.Set<Class<?>> vanilla,
                                 java.util.Map<Class<?>, Class<?>> supers) {
        Class<?> declaring;
        try {
            declaring = type.getMethod(name, class_1297.class, float.class).getDeclaringClass();
        } catch (NoSuchMethodException e) {
            return false;
        }
        if (!vanilla.contains(declaring)) {
            return true;
        }
        for (Class<?> d = declaring; d != null; d = supers.get(d)) {
            if (CbMixedMembers.mixed(d.getName().replace('.', '/') + "." + name + desc)) {
                return true;
            }
        }
        return false;
    }

    /**
     * TamableAnimal's super call in its restored handleLeashAtDistance (1.21.5: invokespecial Animal's, reaching
     * PathfinderMob's): PathfinderMob's restored member with invokespecial semantics, looked up with TamableAnimal's own
     * Lookup (MethodHandles.lookup() in the merged method), so dispatch does not come back into TamableAnimal's override
     */
    public static java.lang.invoke.MethodHandle tamableSuper(java.lang.invoke.MethodHandles.Lookup tamable) {
        try {
            return tamable.findSpecial(net.minecraft.class_1314.class, HANDLE,
                java.lang.invoke.MethodType.methodType(boolean.class, class_1297.class, float.class), net.minecraft.class_1321.class);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("PathfinderMob's restored handleLeashAtDistance", e);
        }
    }

    public static boolean callTamableSuper(java.lang.invoke.MethodHandle sup, net.minecraft.class_1321 self, class_1297 holder, float distance) {
        try {
            return (boolean) sup.invokeExact(self, holder, distance);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }

    /** whether 1.21.6's tickLeash asks the restored handleLeashAtDistance for this entity */
    public static boolean routeHandle(class_1297 entity) {
        return HANDLE_ROUTE.get(entity.getClass());
    }

    /** whether 1.21.6's elastic pull is answered by the restored elasticRangeLeashBehaviour for this entity */
    public static boolean routeElastic(class_1297 entity) {
        return ELASTIC_ROUTE.get(entity.getClass());
    }

    /** the entity and its motion before 1.21.6's checkElasticInteractions, for a routed elastic pull */
    private static final ThreadLocal<Object[]> BEFORE_PULL = new ThreadLocal<>();

    public static void beforePull(class_1297 entity) {
        BEFORE_PULL.set(new Object[] {entity, entity.method_18798()});
    }

    /** the motion recorded for this entity before 1.21.6's pull, or null (taken once) */
    public static net.minecraft.class_243 takeBeforePull(class_1297 entity) {
        Object[] b = BEFORE_PULL.get();
        BEFORE_PULL.remove();
        return b != null && b[0] == entity ? (net.minecraft.class_243) b[1] : null;
    }
}
