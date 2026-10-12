package top.dext.centurybridge.step.v1_21.rt;

import java.util.Optional;

/** core/ shapes of 1.21 a 1.20.6 implementation lacks */
public final class CbItemCore {

    private CbItemCore() {
    }

    /**
     * Registry.getAny() (abstractFills: a mod class implementing Registry, DefaultedRegistry or
     * WritableRegistry has none): 1.21's MappedRegistry answers its first entry; a 1.20.6 registry's
     * holders() lists its entries in that order.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional getAny(net.minecraft.class_2378 registry) {
        return ((java.util.stream.Stream) registry.method_40270()).findFirst();
    }
}
