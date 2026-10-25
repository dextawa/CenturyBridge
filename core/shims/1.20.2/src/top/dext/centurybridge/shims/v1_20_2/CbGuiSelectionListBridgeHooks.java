package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The injectors into 1.20.2's own methods that go with {@link CbGuiSelectionListBridge} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(net.minecraft.class_350.class)
public abstract class CbGuiSelectionListBridgeHooks {

    @Shadow
    protected abstract boolean method_25332(int index);

    @Inject(method = "method_25394", at = @At("HEAD"))
    private void cb$backgroundHook(net.minecraft.class_332 graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ((top.dext.centurybridge.rt.v1_20_2.CbGuiRestoredAccess.SelectionList) (Object) this).cb$renderBackground(graphics);
    }

    @Redirect(method = "method_44397", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_350;method_25332(I)Z"))
    private boolean cb$selectionShown(net.minecraft.class_350<?> self, int index) {
        return ((top.dext.centurybridge.rt.v1_20_2.CbGuiRestoredAccess.SelectionList) (Object) this).cb$renderSelection() && this.method_25332(index);
    }

    @Redirect(method = "method_25394", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_332;method_51740(Lnet/minecraft/class_1921;IIIIIII)V"))
    private void cb$edgeShadow(net.minecraft.class_332 graphics, net.minecraft.class_1921 layer, int x1, int y1, int x2, int y2,
                               int colorFrom, int colorTo, int z) {
        if (((top.dext.centurybridge.rt.v1_20_2.CbGuiRestoredAccess.SelectionList) (Object) this).cb$renderTopAndBottom()) {
            graphics.method_51740(layer, x1, y1, x2, y2, colorFrom, colorTo, z);
        }
    }
}
