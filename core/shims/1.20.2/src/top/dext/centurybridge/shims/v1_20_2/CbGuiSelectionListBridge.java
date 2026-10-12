package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Three AbstractSelectionList hooks 1.20.2 removed, with 1.20.1's meaning on the 1.20.2 list:
 * <ul>
 * <li>setRenderSelection(boolean): 1.20.1 drew the selected entry's highlight only while it was set
 * (default true); 1.20.2 always draws it. The highlight test of renderItem honours the flag again.</li>
 * <li>setRenderTopAndBottom(boolean): 1.20.1 drew the list's top and bottom edges (the dirt bands and
 * the shadow gradients under the header and over the footer) only while it was set (default true).
 * 1.20.2 lists draw no bands and draw the two gradients with the list background; cleared, the flag
 * drops those two gradients, the edges a 1.20.2 list still has.</li>
 * <li>renderBackground(GuiGraphics): 1.20.1's render called this empty hook first, so a subclass could
 * paint behind the list; 1.20.2 has no such call. render calls it first again.</li>
 * </ul>
 * Vanilla 1.20.2 lists never clear the flags and do not override the hook: they render unchanged.
 */
@Mixin(net.minecraft.class_350.class)
public abstract class CbGuiSelectionListBridge {

    @Shadow
    protected abstract boolean method_25332(int index);

    @Unique
    private boolean cb$renderSelection = true;
    @Unique
    private boolean cb$renderTopAndBottom = true;

    /** 1.20.1 setRenderSelection(boolean) */
    public void method_29344(boolean renderSelection) {
        this.cb$renderSelection = renderSelection;
    }

    /** 1.20.1 setRenderTopAndBottom(boolean) */
    public void method_31323(boolean renderTopAndBottom) {
        this.cb$renderTopAndBottom = renderTopAndBottom;
    }

    /** 1.20.1 renderBackground(GuiGraphics): an empty hook for subclasses */
    protected void method_25325(net.minecraft.class_332 graphics) {
    }

    @Inject(method = "method_25394", at = @At("HEAD"))
    private void cb$backgroundHook(net.minecraft.class_332 graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.method_25325(graphics);
    }

    @Redirect(method = "method_44397", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_350;method_25332(I)Z"))
    private boolean cb$selectionShown(net.minecraft.class_350<?> self, int index) {
        return this.cb$renderSelection && this.method_25332(index);
    }

    @Redirect(method = "method_25394", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_332;method_51740(Lnet/minecraft/class_1921;IIIIIII)V"))
    private void cb$edgeShadow(net.minecraft.class_332 graphics, net.minecraft.class_1921 layer, int x1, int y1, int x2, int y2,
                               int colorFrom, int colorTo, int z) {
        if (this.cb$renderTopAndBottom) {
            graphics.method_51740(layer, x1, y1, x2, y2, colorFrom, colorTo, z);
        }
    }
}
