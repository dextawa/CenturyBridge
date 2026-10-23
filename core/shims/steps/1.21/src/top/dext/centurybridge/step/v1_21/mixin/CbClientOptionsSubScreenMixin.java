package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbClientGui;

/**
 * OptionsSubScreen for a 1.20.6 subclass (CbClientGui.cbLegacyUnder): 1.20.6's init (title, footer, the layout's
 * widgets, positions; 1.21's also builds an options list of its own and asks addOptions) and 1.20.6's onClose
 * (every OptionsList child applies its unsaved changes; 1.21's applies only the list it built itself).
 */
@Mixin(net.minecraft.class_4667.class)
public abstract class CbClientOptionsSubScreenMixin extends net.minecraft.class_437 {

    @Shadow
    @Final
    public net.minecraft.class_8132 field_49503;

    @Shadow
    @Final
    protected net.minecraft.class_437 field_21335;

    protected CbClientOptionsSubScreenMixin(net.minecraft.class_2561 title) {
        super(title);
    }

    @Shadow
    protected abstract void method_57732();

    @Shadow
    protected abstract void method_31387();

    @Inject(method = "method_25426", at = @At("HEAD"), cancellable = true)
    private void cb$legacyInit(CallbackInfo ci) {
        if (CbClientGui.cbLegacyUnder(this, net.minecraft.class_4667.class)) {
            this.method_57732();
            this.method_31387();
            this.field_49503.method_48206(widget -> this.method_37063(widget));
            this.method_48640();
            ci.cancel();
        }
    }

    @Inject(method = "method_25419", at = @At("HEAD"), cancellable = true)
    private void cb$legacyClose(CallbackInfo ci) {
        if (CbClientGui.cbLegacyUnder(this, net.minecraft.class_4667.class)) {
            for (net.minecraft.class_364 child : this.method_25396()) {
                if (child instanceof net.minecraft.class_353 list) {
                    list.method_59869();
                }
            }
            this.field_22787.method_1507(this.field_21335);
            ci.cancel();
        }
    }
}
