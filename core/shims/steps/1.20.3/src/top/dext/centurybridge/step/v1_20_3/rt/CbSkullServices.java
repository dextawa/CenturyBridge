package top.dext.centurybridge.step.v1_20_3.rt;

import com.mojang.authlib.minecraft.MinecraftSessionService;

/**
 * SkullBlockEntity's two 1.20.2 statics, which 1.20.3 dropped: the profile cache (field_12089, a GameProfileCache --
 * 1.20.3 reuses the name for its own LoadingCache of lookups, so a field of that name and V's type cannot be given back
 * on the class) and the session service (field_12088). Mods' reads and writes of them (mixin @Shadow / @Accessor; the
 * fields were private) land here through fieldGetters / staticFieldSetters. CbSkullServicesMixin stores and clears them
 * where 1.20.2's setup / clear did, and 1.20.3's lookup reads them.
 */
public final class CbSkullServices {

    private static volatile net.minecraft.class_3312 profileCache;

    private static volatile MinecraftSessionService sessionService;

    private CbSkullServices() {
    }

    /** read of 1.20.2 SkullBlockEntity.profileCache */
    public static net.minecraft.class_3312 field_12089() {
        return profileCache;
    }

    /** write of 1.20.2 SkullBlockEntity.profileCache */
    public static void field_12089(net.minecraft.class_3312 value) {
        profileCache = value;
    }

    /** read of 1.20.2 SkullBlockEntity.sessionService */
    public static MinecraftSessionService field_12088() {
        return sessionService;
    }

    /** write of 1.20.2 SkullBlockEntity.sessionService */
    public static void field_12088(MinecraftSessionService value) {
        sessionService = value;
    }
}
