package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallItemComponents;

/**
 * 1.21.11's Properties.component(type, value) set the value on the properties' builder: 26.1's records it as an
 * initializer step, and this sets the same value on the restored builder (CbWallItemPropertiesComponentsMixin). The
 * properties' 26.1 initializer is untouched. Default priority: injector into a 26.1 method.
 */
@Mixin(Item.Properties.class)
public abstract class CbWallItemPropertiesComponentsReadsMixin {

    @Inject(method = "component", at = @At("RETURN"))
    private <T> void cb$recordComponent(DataComponentType<T> type, T value, CallbackInfoReturnable<Item.Properties> cir) {
        ((CbWallItemComponents.PropertiesLegacy) this).cb$components().set(type, value);
    }
}
