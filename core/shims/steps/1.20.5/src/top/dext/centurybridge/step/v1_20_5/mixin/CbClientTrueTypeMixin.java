package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbClientRender;

/**
 * A TrueTypeGlyphProvider a 1.20.4 caller made from an stb_truetype font info (rt/CbClientRender.cbFace):
 * 1.20.4's provider owned the info and freed it on close; 1.20.5's frees its FreeType face and the buffer.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_395.class)
public abstract class CbClientTrueTypeMixin implements CbClientRender.TrueTypeApi {

    @Unique
    private org.lwjgl.stb.STBTTFontinfo cb$fontInfo;

    @Override
    public void cb$adoptFontInfo(org.lwjgl.stb.STBTTFontinfo info) {
        this.cb$fontInfo = info;
    }

    @Inject(method = "close", at = @At("TAIL"))
    private void cb$freeFontInfo(CallbackInfo ci) {
        if (this.cb$fontInfo != null) {
            this.cb$fontInfo.free();
            this.cb$fontInfo = null;
        }
    }
}
