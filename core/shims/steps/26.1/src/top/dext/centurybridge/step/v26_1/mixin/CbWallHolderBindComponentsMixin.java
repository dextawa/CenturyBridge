package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallItemComponents;

/**
 * When 26.1 binds an item's components, 1.21.11's Item.components field (CbWallItemComponentsMixin) takes the bound map,
 * so 1.21.11 code reading the field sees the components 1.21.11's constructor would have built, the ones that need the
 * loaded registries included. Other holders are untouched. Default priority: injector into a 26.1 method.
 */
@Mixin(Holder.Reference.class)
public abstract class CbWallHolderBindComponentsMixin<T> {

    @Shadow
    public abstract boolean isBound();

    @Shadow
    public abstract T value();

    @Inject(method = "bindComponents", at = @At("RETURN"))
    private void cb$itemComponents(DataComponentMap components, CallbackInfo ci) {
        if (isBound() && value() instanceof Item item) {
            ((CbWallItemComponents.ItemLegacy) item).cb$setComponents(components);
        }
    }
}
