package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbClientShaders;

/**
 * GameRenderer's 1.20.4 shader pieces (rt/CbClientShaders): the two core shaders 1.20.5 dropped, loaded with
 * the rest on every shader reload, and cycleEffect() -- 1.20.4's step through its post effects (the effect
 * index starting at EFFECT_NONE, as 1.20.4's constructor set it), only with a player as the camera entity; the
 * current effect closed first and loadEffect closing it again, as 1.20.4's pair did.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_757.class)
public abstract class CbClientGameRendererShadersMixin {

    @Shadow
    @Final
    net.minecraft.class_310 field_4015;

    @Shadow
    net.minecraft.class_279 field_4024;

    @Shadow
    private void method_3168(net.minecraft.class_2960 location) {
        throw new AssertionError();
    }

    /** 1.20.4 GameRenderer.effectIndex */
    @Unique
    private int cb$effectIndex = CbClientShaders.field_4010();

    @Inject(method = "method_34538", at = @At("TAIL"))
    private void cb$legacyCoreShaders(net.minecraft.class_5912 resources, CallbackInfo ci) {
        CbClientShaders.reload(resources);
    }

    /** 1.20.4 GameRenderer.cycleEffect() */
    public void method_35771() {
        if (!(this.field_4015.method_1560() instanceof net.minecraft.class_1657)) {
            return;
        }
        if (this.field_4024 != null) {
            this.field_4024.close();
        }
        this.cb$effectIndex = (this.cb$effectIndex + 1) % (CbClientShaders.SHADERS.length + 1);
        if (this.cb$effectIndex == CbClientShaders.field_4010()) {
            this.field_4024 = null;
        } else {
            this.method_3168(CbClientShaders.shader(this.cb$effectIndex));
        }
    }
}
