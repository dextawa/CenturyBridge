package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.facade.PostChain;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestPostEffect;

/**
 * The injectors into 1.21.2's own GameRenderer methods that go with {@link CbRestPostEffectRestored} (default priority):
 * the mirror follows the id after setPostEffect, clearPostEffect and checkEntityPostEffect wrote it; a mirror a mod wrote
 * goes into the id before getPostEffect and render read it. A chain a mod loaded from a 1.21.1 json (no 1.21.2 id) runs
 * where 1.21.1's render ran postEffect (just before 1.21.2 runs the chain of its id, which is then null), with 1.21.1's
 * render state and partial tick; resize and close reach it as 1.21.1's did. Unmixed there is no such chain and these
 * only keep the mirror.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_757.class)
public abstract class CbRestPostEffectRestoredHooks {

    @Inject(method = {"method_62904", "method_62905", "method_3167"}, require = 3, at = @At("TAIL"))
    private void cb$v1_21_2$mirrorPostEffect(CallbackInfo ci) {
        ((CbRestPostEffect.Game) this).cb$v1_21_2$mirrorPostEffect();
    }

    @Inject(method = "method_62906", at = @At("HEAD"))
    private void cb$v1_21_2$reconcilePostEffect(CallbackInfoReturnable<net.minecraft.class_2960> cir) {
        ((CbRestPostEffect.Game) this).cb$v1_21_2$reconcilePostEffect();
    }

    @Inject(method = "method_3192", require = 1, at = @At(value = "FIELD", opcode = org.objectweb.asm.Opcodes.GETFIELD, ordinal = 0,
        target = "Lnet/minecraft/class_757;field_53898:Lnet/minecraft/class_2960;"))
    private void cb$v1_21_2$renderPostEffect(net.minecraft.class_9779 deltaTracker, boolean renderLevel, CallbackInfo ci) {
        PostChain chain = ((CbRestPostEffect.Game) this).cb$v1_21_2$modChain();
        if (chain != null && ((CbRestPostEffect.Game) this).cb$v1_21_2$effectActive()) {
            com.mojang.blaze3d.systems.RenderSystem.disableBlend();
            com.mojang.blaze3d.systems.RenderSystem.disableDepthTest();
            com.mojang.blaze3d.systems.RenderSystem.resetTextureMatrix();
            chain.method_1258(deltaTracker.method_60636());
        }
    }

    @Inject(method = "method_3169", at = @At("TAIL"))
    private void cb$v1_21_2$resizePostEffect(int width, int height, CallbackInfo ci) {
        PostChain chain = ((CbRestPostEffect.Game) this).cb$v1_21_2$modChain();
        if (chain != null) {
            chain.method_1259(width, height);
        }
    }

    @Inject(method = "close", at = @At("TAIL"))
    private void cb$v1_21_2$closePostEffect(CallbackInfo ci) {
        PostChain chain = ((CbRestPostEffect.Game) this).cb$v1_21_2$modChain();
        if (chain != null) {
            chain.close();
        }
    }
}
