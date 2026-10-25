package cb.s7gear.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_7923;
import net.minecraft.class_9331;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * a 1.21.1 mod mixin redirecting Item.isEnchantable(stack)'s MAX_DAMAGE test: a totem of undying (a stack of one) is enchantable
 * (GearTestMod.overrideReads)
 */
@Mixin(class_1792.class)
public abstract class TotemEnchantableMixin {

    @Redirect(method = "method_7870(Lnet/minecraft/class_1799;)Z",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1799;method_57826(Lnet/minecraft/class_9331;)Z"))
    private boolean cbs7gear$totemToo(class_1799 stack, class_9331<?> type) {
        return stack.method_57826(type) || "totem_of_undying".equals(class_7923.field_41178.method_10221(stack.method_7909()).method_12832());
    }
}
