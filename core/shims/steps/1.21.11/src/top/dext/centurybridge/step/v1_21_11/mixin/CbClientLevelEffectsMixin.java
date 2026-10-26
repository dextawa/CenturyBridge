package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11743;
import net.minecraft.class_2350;
import net.minecraft.class_2874;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_11.facade.DimensionSpecialEffects;
import top.dext.centurybridge.step.v1_21_11.rt.CbDimensionEffectsRoutes;
import top.dext.centurybridge.step.v1_21_11.rt.CbEffectsLevel;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * 1.21.10's ClientLevel.effects() (method_28103), restored: the DimensionSpecialEffects for the level's dimension type,
 * chosen once when the level is made, as 1.21.10's constructor did. 1.21.11's own drawing points ask it through
 * rt/CbDimensionEffectsRoutes (only for a mod's effects object or a mod's mixin; otherwise 1.21.11's values stand):
 * here getShade's answer (1.21.10 read constantAmbientLight there) and, at the end of the constructor,
 * whether the level has an EndFlashState (1.21.10 made it for force-bright effects, 1.21.11 for END skyboxes: 1.21.11's
 * own hasEndFlashes call stays where it is and decides first).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = class_638.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbClientLevelEffectsMixin implements CbEffectsLevel {
    @Shadow
    @Final
    @Mutable
    private class_11743 field_62026;

    /** 1.21.10's ClientLevel.effects field (field_24606), restored under its name: effects() reads it as 1.21.10's did */
    private DimensionSpecialEffects field_24606;

    @Inject(method = "<init>(Lnet/minecraft/class_634;Lnet/minecraft/class_638$class_5271;Lnet/minecraft/class_5321;Lnet/minecraft/class_6880;IILnet/minecraft/class_761;ZJI)V", at = @At("RETURN"))
    private void cb$chooseEffects(CallbackInfo ci) {
        class_638 self = (class_638) (Object) this;
        this.field_24606 = DimensionSpecialEffects.method_28111(self.method_8597());
        boolean flashes = this.field_62026 != null;
        boolean want = CbDimensionEffectsRoutes.endFlashes(self, flashes);
        if (want != flashes) {
            this.field_62026 = want ? new class_11743() : null;
        }
    }

    /**
     * getShade: 1.21.11's body runs as it is (its dimensionType().cardinalLightType() read included); when 1.21.10's
     * effects are consulted, the answer is the same body's for the cardinal light type constantAmbientLight stands for
     */
    @Inject(method = "method_24852(Lnet/minecraft/class_2350;Z)F", at = @At("RETURN"), cancellable = true)
    private void cb$shade(class_2350 direction, boolean shade, CallbackInfoReturnable<Float> cir) {
        class_638 self = (class_638) (Object) this;
        if (CbDimensionEffectsRoutes.consulted(self, CbDimensionEffectsRoutes.SHADE) == null) {
            return;
        }
        float v = CbDimensionEffectsRoutes.shade(direction, shade,
            CbDimensionEffectsRoutes.cardinalLight(self, class_2874.class_12325.field_64380));
        if (v != cir.getReturnValueF()) {
            cir.setReturnValue(v);
        }
    }

    @CbVanillaReads("CbDimensionEffectsRoutes (CbClientLevelEffectsMixin, CbDimEffectsLightingMixin, CbDimEffectsLightTextureMixin, "
        + "CbDimEffectsFogMixin, CbDimEffectsSkyMixin): when a converted mod mixes effects(), 1.21.11's shade, lighting, "
        + "end flash, lightmap, fog and sky points take the effects it answers")
    public DimensionSpecialEffects method_28103() {
        return this.field_24606;
    }

    @Override
    public DimensionSpecialEffects cb$levelEffects() {
        return this.method_28103();
    }
}
