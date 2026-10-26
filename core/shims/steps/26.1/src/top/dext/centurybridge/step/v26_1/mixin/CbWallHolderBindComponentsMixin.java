package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallItemComponents;

/**
 * 1.21.11's Item.components (CbWallItemComponentsMixin) where 26.1 keeps an item's components, its registry holder:
 * <ul>
 * <li>components() of an item's holder before 26.1 bound them (26.1 throws "Components not bound yet"; 1.21.11 had
 *     the item's map from its constructor on) answers what 1.21.11's constructor built -- Item.components() and
 *     new ItemStack(item) both read the holder;</li>
 * <li>bindComponents hands the bound map to the item's field, so 1.21.11 code reading the field sees the
 *     components that need the loaded registries too.</li>
 * </ul>
 * Bound holders and holders of anything but an item are 26.1's, untouched. Default priority: injectors into 26.1
 * methods.
 */
@Mixin(Holder.Reference.class)
public abstract class CbWallHolderBindComponentsMixin<T> {

    @Shadow
    private DataComponentMap components;

    @Shadow
    private T value;

    @Inject(method = "components", at = @At("HEAD"), cancellable = true)
    private void cb$unboundItemComponents(CallbackInfoReturnable<DataComponentMap> cir) {
        // the value, not isBound(): an intrusive holder holds its item from the item's constructor on, before
        // registration gives it its key
        if (this.components == null && this.value instanceof Item item) {
            DataComponentMap legacy = ((CbWallItemComponents.ItemLegacy) item).cb$components();
            if (legacy != null) {
                cir.setReturnValue(legacy);
            }
        }
    }

    @Inject(method = "bindComponents", at = @At("RETURN"))
    private void cb$itemComponents(DataComponentMap components, CallbackInfo ci) {
        if (this.value instanceof Item item) {
            ((CbWallItemComponents.ItemLegacy) item).cb$setComponents(components);
        }
    }
}
