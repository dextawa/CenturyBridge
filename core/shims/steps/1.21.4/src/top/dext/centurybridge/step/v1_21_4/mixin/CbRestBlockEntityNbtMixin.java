package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_1657;
import net.minecraft.class_1747;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2591;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_4.rt.CbRestBlockEntities;

/**
 * BlockItem.updateCustomBlockEntityTag: 1.21.3 asked the block entity's onlyOpCanSetNbt(), 1.21.4 asks its
 * type. A 1.21.3 mod block entity's own override (CbRestBlockEntities.OnlyOp) is asked again; every other
 * block entity keeps its type's answer.
 */
@Mixin(class_1747.class)
public abstract class CbRestBlockEntityNbtMixin {

    @Redirect(method = "method_7714", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2591;method_65166()Z"))
    private static boolean cb$onlyOp(class_2591<?> type, class_1937 level, class_1657 player, class_2338 pos, class_1799 stack) {
        return level.method_8321(pos) instanceof CbRestBlockEntities.OnlyOp a ? a.method_11011() : type.method_65166();
    }
}
