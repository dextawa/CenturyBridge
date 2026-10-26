package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallItemComponents;

/**
 * 1.21.11's Item.Properties.components builder (class_1792$class_1793.field_49265): the common item components, then
 * every component the properties set, in order (1.21.11's component(type, value) set it here; 26.1's adds an
 * initializer step, and mixin/CbWallItemComponentsReadsMixin records the same value here). Item's constructor builds
 * 1.21.11's Item.components from it (CbWallItemComponentsMixin).
 */
@Mixin(value = Item.Properties.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallItemPropertiesComponentsMixin implements CbWallItemComponents.PropertiesLegacy {

    /** 1.21.11 Item.Properties.components (class_1792$class_1793.field_49265) */
    private final DataComponentMap.Builder components = DataComponentMap.builder().addAll(DataComponents.COMMON_ITEM_COMPONENTS);

    @Override
    public DataComponentMap.Builder cb$components() {
        return this.components;
    }
}
