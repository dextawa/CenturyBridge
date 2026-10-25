package cb.s7gear.mixin;

import net.minecraft.class_1759;
import net.minecraft.class_1799;
import net.minecraft.class_9331;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * a 1.21.1 mod mixin redirecting CompassItem.getDescriptionId(stack)'s LODESTONE_TRACKER test: every compass is named
 * a lodestone compass (GearTestMod.nameReads)
 */
@Mixin(class_1759.class)
public abstract class CompassDescriptionIdMixin {

    @Redirect(method = "method_7866(Lnet/minecraft/class_1799;)Ljava/lang/String;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1799;method_57826(Lnet/minecraft/class_9331;)Z"))
    private boolean cbs7gear$alwaysLodestone(class_1799 stack, class_9331<?> type) {
        return true;
    }
}
