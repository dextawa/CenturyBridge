package top.dext.centurybridge.step.v1_21_9.mixin;

import java.util.Map;
import net.minecraft.class_11938;
import net.minecraft.class_11943;
import net.minecraft.class_2394;
import net.minecraft.class_3999;
import net.minecraft.class_4184;
import net.minecraft.class_4604;
import net.minecraft.class_702;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_9.rt.CbParticleGroup;
import top.dext.centurybridge.step.v1_21_9.rt.CbParticleOptions;

/**
 * ParticleEngine's groups for 1.21.8 particles (CbParticles): a group key that is none of 1.21.9's own gets a legacy
 * group (1.21.9 would make a QuadParticleGroup, which takes only SingleQuadParticles), and the legacy groups are
 * extracted after 1.21.9's own, as 1.21.8 drew its sheets after the vanilla ones it kept in its list. Only keys a
 * 1.21.8 particle answers reach this; a frame without them is exactly 1.21.9's.
 *
 * Every particle the engine makes goes through createParticle's private body: a 1.21.8 type that 1.21.9 gave
 * options of their own (CbParticleOptions.Legacy, passed by 1.21.8 code) becomes those options there, before the
 * provider lookup; any other options pass unchanged.
 */
@Mixin(class_702.class)
public abstract class CbParticleEngineMixin {

    @Shadow
    @Final
    private Map<class_3999, class_11938<?>> field_3830;

    @Inject(method = "method_74281(Lnet/minecraft/class_3999;)Lnet/minecraft/class_11938;", at = @At("HEAD"), cancellable = true)
    private void cb$legacyGroup(class_3999 type, CallbackInfoReturnable<class_11938<?>> cir) {
        if (CbParticleGroup.legacy(type)) {
            cir.setReturnValue(new CbParticleGroup((class_702) (Object) this, type));
        }
    }

    @Inject(method = "method_74283(Lnet/minecraft/class_11943;Lnet/minecraft/class_4604;Lnet/minecraft/class_4184;F)V", at = @At("TAIL"))
    private void cb$extractLegacy(class_11943 state, class_4604 frustum, class_4184 camera, float partialTick, CallbackInfo ci) {
        for (class_11938<?> group : this.field_3830.values()) {
            if (group instanceof CbParticleGroup legacy && !legacy.method_74284()) {
                state.method_74318(legacy.method_74276(frustum, camera, partialTick));
            }
        }
    }

    @ModifyVariable(method = "method_3055(Lnet/minecraft/class_2394;DDDDDD)Lnet/minecraft/class_703;", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private class_2394 cb$legacyOptions(class_2394 options) {
        return CbParticleOptions.current(options);
    }
}
