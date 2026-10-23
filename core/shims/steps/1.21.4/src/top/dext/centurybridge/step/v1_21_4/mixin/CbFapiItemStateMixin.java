package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.compat.fapi.renderer.CbFapiRenderer;

/**
 * ItemStackRenderState keeps the stack it was resolved for, and while it renders that stack and its display context are the item
 * being drawn: a 1.21.3 model's emitItemQuads takes both (1.21.4's emit has neither; CbFapiRenderer).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10444.class)
public abstract class CbFapiItemStateMixin implements CbFapiRenderer.CbItemStackHolder {
    @Shadow
    net.minecraft.class_811 field_55337;

    @Unique
    private net.minecraft.class_1799 cb$stack;

    @Override
    public void cb$setStack(net.minecraft.class_1799 stack) {
        this.cb$stack = stack;
    }

    @Inject(method = "method_65605()V", at = @At("HEAD"))
    private void cb$clear(CallbackInfo ci) {
        this.cb$stack = null;
    }

    @Inject(method = "method_65604(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;II)V", at = @At("HEAD"))
    private void cb$beginRender(CallbackInfo ci) {
        CbFapiRenderer.beginItem(this.cb$stack, this.field_55337);
    }

    @Inject(method = "method_65604(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;II)V", at = @At("RETURN"))
    private void cb$endRender(CallbackInfo ci) {
        CbFapiRenderer.endItem();
    }
}
