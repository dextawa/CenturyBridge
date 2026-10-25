package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * 1.21.5 type tests against classes 1.21.6 removed.
 */
public final class CbMobs {

    private CbMobs() {
    }

    /**
     * instanceof FlyingMob: 1.21.5's FlyingMob subclasses were Ghast and Phantom (javap: the only classes extending
     * class_1307) plus mods' own (now extending the facade); 1.21.6's Ghast and Phantom extend Mob directly.
     */
    public static boolean isFlyingMob(Object o) {
        return o instanceof top.dext.centurybridge.step.v1_21_6.facade.FlyingMob
            || o instanceof net.minecraft.class_1571 || o instanceof net.minecraft.class_1593;
    }
}
