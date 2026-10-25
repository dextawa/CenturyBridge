package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The injectors into 1.20.2's own methods that go with {@link CbGuiStateSwitchingButtonBridge} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(net.minecraft.class_361.class)
public abstract class CbGuiStateSwitchingButtonBridgeHooks extends net.minecraft.class_339 {

    @Shadow
    protected boolean field_2194;

    private CbGuiStateSwitchingButtonBridgeHooks() {
        super(0, 0, 0, 0, null);
    }

    @Inject(method = "method_48579", at = @At("HEAD"), cancellable = true)
    private void cb$legacySheet(net.minecraft.class_332 graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        top.dext.centurybridge.rt.v1_20_2.CbGuiRestoredAccess.StateSwitchingSheet sheet = (top.dext.centurybridge.rt.v1_20_2.CbGuiRestoredAccess.StateSwitchingSheet) (Object) this;
        if (sheet.cb$sheet() == null) {
            return;
        }
        com.mojang.blaze3d.systems.RenderSystem.disableDepthTest();
        int u = sheet.cb$xTexStart();
        int v = sheet.cb$yTexStart();
        if (this.field_2194) {
            u += sheet.cb$xDiffTex();
        }
        if (this.method_25367()) {
            v += sheet.cb$yDiffTex();
        }
        graphics.method_25302(sheet.cb$sheet(), this.method_46426(), this.method_46427(), u, v, this.field_22758, this.field_22759);
        com.mojang.blaze3d.systems.RenderSystem.enableDepthTest();
        ci.cancel();
    }
}
