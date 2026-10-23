package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_9279;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_4.rt.CbRestEntityNbt;

/**
 * EntityType.updateCustomEntityTag: 1.21.3 asked the entity's onlyOpCanSetNbt(), 1.21.4 asks its type. A
 * 1.21.3 mod entity's own override (CbRestEntityNbt.OnlyOp) is asked again; every other entity keeps the
 * type's answer.
 */
@Mixin(class_1299.class)
public abstract class CbRestEntityNbtMixin {

    @Redirect(method = "method_5881", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1299;method_65893()Z"))
    private static boolean cb$onlyOp(class_1299 type, class_1937 level, class_1657 player, class_1297 entity, class_9279 data) {
        return entity instanceof CbRestEntityNbt.OnlyOp a ? a.method_5833() : type.method_65893();
    }
}
