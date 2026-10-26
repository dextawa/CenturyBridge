package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallItemComponents;

/**
 * 1.21.11's Item.components field (class_1792.field_49263): the item's component map, built by its constructor from
 * the properties (1.21.11's Properties.buildAndValidateComponents: the properties' components plus the item name and
 * model). 26.1 builds an item's components only when its registry binds them (DataComponentInitializers, after the
 * registries load) and Item.components() throws until then; 1.21.11 code -- a converted mod's constructor, a layer's
 * injector into an item constructor -- read them right away. The field holds what 1.21.11's constructor built
 * (mixin/CbWallItemComponentsReadsMixin builds it from the restored Properties.components) and, once 26.1 binds the
 * item's components, the bound map (mixin/CbWallHolderBindComponentsMixin). components() answers it only while 26.1
 * has bound nothing (where 26.1 throws); bound, components() is 26.1's.
 *
 * Known difference: a component the properties set through 26.1's delayedComponent / delayedHolderComponent (values
 * that need the loaded registries) is missing from the map until the item's components are bound.
 */
@Mixin(value = Item.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallItemComponentsMixin implements CbWallItemComponents.ItemLegacy {

    /** 1.21.11 Item.components (class_1792.field_49263) */
    private DataComponentMap components;

    @Override
    public DataComponentMap cb$components() {
        return this.components;
    }

    @Override
    public void cb$setComponents(DataComponentMap components) {
        this.components = components;
    }
}
