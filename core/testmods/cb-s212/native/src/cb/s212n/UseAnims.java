package cb.s212n;

import cb.s212n.mixin.NativeUseAnimInvoker;

/**
 * 1.21.2 reading of a UseAnim (cb-s212's driver asks by reflection): "name/ordinal/id/serializedName", and a constant
 * made natively with 1.21.2's constructor the way a 1.21.2 enum extension makes one
 */
public final class UseAnims {
    private UseAnims() {
    }

    public static String describe(Object anim) {
        net.minecraft.class_1839 a = (net.minecraft.class_1839) anim;
        return a.name() + "/" + a.ordinal() + "/" + a.method_62838() + "/" + a.method_15434();
    }

    public static String nativeMade() {
        int n = net.minecraft.class_1839.values().length;
        return describe(NativeUseAnimInvoker.cbs212n$create("CBS212N_ANIM", n, n, "cbs212n_anim"));
    }
}
