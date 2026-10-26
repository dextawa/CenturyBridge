package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallItemComponents;

/**
 * 1.21.11's Item.components (CbWallItemComponentsMixin) where 26.1's Item reads and builds: the constructor, after
 * 26.1's own body, builds the map 1.21.11's built -- the restored Properties.components with the item name
 * (translatable description id) and item model 26.1's finalizeInitializer sets; components() answers that map while
 * 26.1 has not bound the item's components (where 26.1's body throws "Components not bound yet"), and is 26.1's once
 * bound. Default priority: injectors into 26.1 methods.
 */
@Mixin(Item.class)
public abstract class CbWallItemComponentsReadsMixin {

    @Shadow
    @Final
    protected String descriptionId;

    @Shadow
    @Final
    private Holder.Reference<Item> builtInRegistryHolder;

    @Inject(method = "<init>(Lnet/minecraft/world/item/Item$Properties;)V", at = @At("RETURN"))
    private void cb$buildLegacyComponents(Item.Properties properties, CallbackInfo ci) {
        DataComponentMap.Builder components = ((CbWallItemComponents.PropertiesLegacy) properties).cb$components();
        components.set(DataComponents.ITEM_NAME, Component.translatable(this.descriptionId));
        components.set(DataComponents.ITEM_MODEL, properties.effectiveModel());
        ((CbWallItemComponents.ItemLegacy) this).cb$setComponents(components.build());
    }

    @Inject(method = "components", at = @At("HEAD"), cancellable = true)
    private void cb$unboundComponents(CallbackInfoReturnable<DataComponentMap> cir) {
        if (!this.builtInRegistryHolder.areComponentsBound()) {
            DataComponentMap legacy = ((CbWallItemComponents.ItemLegacy) this).cb$components();
            if (legacy != null) {
                cir.setReturnValue(legacy);
            }
        }
    }
}
