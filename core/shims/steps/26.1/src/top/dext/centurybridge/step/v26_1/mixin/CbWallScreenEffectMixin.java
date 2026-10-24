package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacySplit;

/**
 * 1.21.11's ScreenEffectRenderer.renderScreenEffect(sleeping, partialTick, collector) (class_4603.method_23067): 26.1
 * passes what 1.21.11 read from the options itself -- whether the camera is first person and whether the GUI is
 * hidden -- as arguments (renderScreenEffect(firstPerson, sleeping, partialTick, collector, hideGui)). The old shape
 * is given back reading them from the options as 1.21.11 did; a subclass overriding it is called in place of 26.1's
 * shape, its super call passing on the values vanilla passed (rt/CbWallLegacySplit).
 */
@Environment(EnvType.CLIENT)
@Mixin(ScreenEffectRenderer.class)
public abstract class CbWallScreenEffectMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private boolean cb$framed;
    @Unique
    private boolean cb$firstPerson;
    @Unique
    private boolean cb$hideGui;

    @Shadow
    public abstract void renderScreenEffect(boolean firstPerson, boolean sleeping, float partialTick, SubmitNodeCollector collector, boolean hideGui);

    /** 1.21.11 ScreenEffectRenderer.renderScreenEffect(sleeping, partialTick, collector) */
    public void renderScreenEffect(boolean sleeping, float partialTick, SubmitNodeCollector collector) {
        boolean firstPerson = cb$framed ? cb$firstPerson : minecraft.options.getCameraType().isFirstPerson();
        boolean hideGui = cb$framed ? cb$hideGui : minecraft.options.hideGui;
        CbWallLegacySplit.enter(this);
        try {
            renderScreenEffect(firstPerson, sleeping, partialTick, collector, hideGui);
        } finally {
            CbWallLegacySplit.exit(this);
        }
    }

    @Inject(method = "renderScreenEffect(ZZFLnet/minecraft/client/renderer/SubmitNodeCollector;Z)V", at = @At("HEAD"), cancellable = true)
    private void cb$legacy(boolean firstPerson, boolean sleeping, float partialTick, SubmitNodeCollector collector, boolean hideGui, CallbackInfo ci) {
        if (CbWallLegacySplit.legacy(this, ScreenEffectRenderer.class, "renderScreenEffect", boolean.class, float.class, SubmitNodeCollector.class)) {
            ci.cancel();
            boolean framed = cb$framed;
            cb$framed = true;
            cb$firstPerson = firstPerson;
            cb$hideGui = hideGui;
            try {
                renderScreenEffect(sleeping, partialTick, collector);
            } finally {
                cb$framed = framed;
            }
        }
    }
}
