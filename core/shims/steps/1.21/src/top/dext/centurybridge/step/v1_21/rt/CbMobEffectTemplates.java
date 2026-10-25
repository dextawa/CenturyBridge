package top.dext.centurybridge.step.v1_21.rt;

/**
 * Runtime side of the MobEffect.AttributeTemplate route (CbMobEffectTemplateMixin / CbMobEffectMixin): the restored
 * 1.20.6 create(name, amplifier) as the layer calls it, and the effect whose createModifiers is running (1.21 asks each
 * template from a static helper that no longer knows the effect, 1.20.6 asked from the effect with its description id).
 */
public final class CbMobEffectTemplates {
    private CbMobEffectTemplates() {
    }

    /** the template with 1.20.6's create(name, amplifier) restored on it (CbMobEffectTemplateMixin) */
    public interface Restored {
        net.minecraft.class_1322 method_52522(String name, int amplifier);
    }

    /** the effect whose createModifiers (method_55650) is running on this thread, innermost first */
    private static final ThreadLocal<java.util.ArrayDeque<net.minecraft.class_1291>> CREATING = ThreadLocal.withInitial(java.util.ArrayDeque::new);

    public static void begin(net.minecraft.class_1291 effect) {
        CREATING.get().push(effect);
    }

    public static void end() {
        java.util.ArrayDeque<net.minecraft.class_1291> d = CREATING.get();
        if (!d.isEmpty()) {
            d.pop();
        }
    }

    /** 1.20.6's name argument: the description id of the effect asking, as 1.20.6's MobEffect passed it */
    public static String name(net.minecraft.class_1291 effect) {
        return effect != null ? effect.method_5567() : "";
    }

    /** the effect of the running createModifiers, or null outside one */
    public static net.minecraft.class_1291 creating() {
        return CREATING.get().peek();
    }

    /** 1.21's create(amplifier) call, as 1.20.6's create(name, amplifier) with 1.20.6's name */
    public static net.minecraft.class_1322 create(Object template, net.minecraft.class_1291 effect, int amplifier) {
        return ((Restored) template).method_52522(name(effect), amplifier);
    }
}
