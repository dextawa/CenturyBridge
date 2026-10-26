package top.dext.centurybridge.step.v26_1.mixin;

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
import top.dext.centurybridge.step.v26_1.rt.CbWallItemComponents;

/**
 * 1.21.11's Item.components (CbWallItemComponentsMixin) built where 1.21.11 built it: after 26.1's constructor body,
 * the restored Properties.components with the item name (translatable description id) and item model that 26.1's
 * finalizeInitializer sets. The item's holder answers it until 26.1 binds the components
 * (CbWallHolderBindComponentsMixin). Default priority: injector into a 26.1 method.
 */
@Mixin(Item.class)
public abstract class CbWallItemComponentsReadsMixin {

    @Shadow
    @Final
    protected String descriptionId;

    @Inject(method = "<init>(Lnet/minecraft/world/item/Item$Properties;)V", at = @At("RETURN"))
    private void cb$buildLegacyComponents(Item.Properties properties, CallbackInfo ci) {
        DataComponentMap.Builder components = ((CbWallItemComponents.PropertiesLegacy) properties).cb$components();
        components.set(DataComponents.ITEM_NAME, Component.translatable(this.descriptionId));
        components.set(DataComponents.ITEM_MODEL, properties.effectiveModel());
        ((CbWallItemComponents.ItemLegacy) this).cb$setComponents(components.build());
    }
}
