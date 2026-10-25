package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1068;
import net.minecraft.class_2960;
import net.minecraft.class_640;
import net.minecraft.class_8685;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * PlayerListEntry: 1.20.1 answered the skin piece by piece (getSkinTexture, getCapeTexture, ...);
 * 1.20.2 answers one SkinTextures record (getSkinTextures). The 1.20.1 getters come back answering
 * from that record, for converted calls, super calls and the getters a subclass leaves alone; a
 * subclass overriding any of them has vanilla's getSkinTextures() rebuilt from them
 * (CbNetClient.cbSkin).
 */
@Mixin(value = class_640.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbNetPlayerListEntryBridge {

    @Unique
    private class_8685 cb$skin() {
        return ((class_640) (Object) this).method_52810();
    }

    /** was getSkinTexture() */
    public class_2960 method_2968() {
        return cb$skin().comp_1626();
    }

    /** was getCapeTexture(): null without a cape */
    public class_2960 method_2979() {
        return cb$skin().comp_1627();
    }

    /** was getElytraTexture() */
    public class_2960 method_2957() {
        return cb$skin().comp_1628();
    }

    /** was getModel(): "default" or "slim" */
    public String method_2977() {
        return cb$skin().comp_1629().method_52856();
    }

    /**
     * was hasSkinTexture(): 1.20.1 answered whether the profile's own skin had been loaded (until
     * then, and for a profile without one, getSkinTexture fell back to the default skin). 1.20.2
     * answers the default skin in exactly those cases, so this is whether the skin is not the
     * profile's default one.
     */
    public boolean method_2967() {
        class_640 self = (class_640) (Object) this;
        return !cb$skin().comp_1626().equals(class_1068.method_52854(self.method_2966()).comp_1626());
    }

    /** was hasCapeTexture(): whether a cape has been loaded */
    public boolean method_35757() {
        return cb$skin().comp_1627() != null;
    }

    /** was loadTextures(): starts the skin lookup, which 1.20.2 starts on the first getSkinTextures() */
    protected void method_2969() {
        cb$skin();
    }
}
