package top.dext.centurybridge.step.v1_21_4.mixin;

import com.mojang.datafixers.util.Pair;
import java.util.function.Function;
import net.minecraft.class_1735;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_465;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_4.rt.CbRestSlots;

/**
 * AbstractContainerScreen.renderSlot draws an empty slot's icon from the GUI sprite atlas. A 1.21.3 slot's own
 * getNoItemIcon names its atlas (CbRestSlots.LegacyIcon); its icon is drawn from that atlas, as 1.21.3's
 * renderSlot did (minecraft.getTextureAtlas(atlas).apply(sprite)). Every other slot is drawn as 1.21.4 draws it.
 */
@Mixin(class_465.class)
public abstract class CbRestSlotIconMixin {

    @Redirect(method = "method_2385", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_332;method_52706(Ljava/util/function/Function;Lnet/minecraft/class_2960;IIII)V"))
    private void cb$legacyIcon(class_332 graphics, Function<class_2960, ?> renderType, class_2960 sprite, int x, int y, int w, int h,
            class_332 graphicsArg, class_1735 slot) {
        if ((Object) slot instanceof CbRestSlots.LegacyIcon legacy) {
            Pair<class_2960, class_2960> icon = legacy.method_7679();
            if (icon != null && !CbRestSlots.GUI_ATLAS.equals(icon.getFirst())) {
                graphics.method_52709((Function) renderType, class_310.method_1551().method_1549(icon.getFirst()).apply(icon.getSecond()), x, y, w, h);
                return;
            }
        }
        graphics.method_52706((Function) renderType, sprite, x, y, w, h);
    }
}
