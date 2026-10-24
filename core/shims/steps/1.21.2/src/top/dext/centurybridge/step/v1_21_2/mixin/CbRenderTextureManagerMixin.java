package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderPost;

/**
 * 1.21.1 TextureManager.bindForSetup(location), gone in 1.21.2 (its callers bind the texture themselves), put back with
 * 1.21.1's body (CbRenderPost.bindForSetup: the texture loaded if missing and bound, on the render thread), so a mod's
 * call reaches a mod subclass's override and that override's super call reaches 1.21.1's body.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_1060.class)
public abstract class CbRenderTextureManagerMixin {

    /** 1.21.1 TextureManager.bindForSetup(location) */
    public void method_22813(net.minecraft.class_2960 location) {
        CbRenderPost.bindForSetup((net.minecraft.class_1060) (Object) this, location);
    }
}
