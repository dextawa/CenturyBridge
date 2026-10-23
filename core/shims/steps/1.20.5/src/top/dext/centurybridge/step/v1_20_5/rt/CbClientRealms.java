package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.4's Realms 32-bit warning on 1.20.5 (s4client): the pieces the warning's facades (facade/Realms32BitWarningStatus,
 * Realms32bitWarningScreen) read from Minecraft and Options, which the layer gives back by mixin.
 */
@Environment(EnvType.CLIENT)
public final class CbClientRealms {

    private CbClientRealms() {
    }

    /** Minecraft.is64Bit() and getRealms32BitWarningStatus(), as mixin/CbClientMinecraftMixin gives them back */
    public interface Minecraft64 {
        boolean cb$is64Bit();
    }

    /** Options.skipRealms32bitWarning, as mixin/CbClientOptionsMixin gives it back */
    public interface Options32Bit {
        boolean cb$skipRealms32bitWarning();

        void cb$setSkipRealms32bitWarning(boolean skip);
    }

    /**
     * the realm creation task 1.20.5's upload screens take: none. 1.20.4's upload screens only uploaded to an
     * existing realm (1.20.5 passes a task when the upload is the last step of creating one, and skips it when
     * there is none)
     */
    public static net.minecraft.class_4445 noCreationTask() {
        return null;
    }

    /**
     * the world version 1.20.5's FileUpload adds to its upload cookie, which 1.20.4's did not send: the version of
     * this game, the one that last wrote a world it uploads (1.20.5's upload screen names the uploaded level's own
     * version; 1.20.4's constructor is not given the level)
     */
    public static String uploadWorldVersion() {
        return net.minecraft.class_155.method_16673().method_48019();
    }
}
