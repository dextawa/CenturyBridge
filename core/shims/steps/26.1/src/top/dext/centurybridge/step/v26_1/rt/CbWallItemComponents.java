package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.core.component.DataComponentMap;

/**
 * 1.21.11's Item.components / Item.Properties.components, restored by mixin/CbWallItemComponentsMixin and
 * mixin/CbWallItemPropertiesComponentsMixin, read and filled by mixin/CbWallItemComponentsReadsMixin and
 * mixin/CbWallHolderBindComponentsMixin (a mixin cannot name a member another mixin adds).
 */
public final class CbWallItemComponents {
    private CbWallItemComponents() {}

    /** Item */
    public interface ItemLegacy {
        /** what 1.21.11's constructor built, or the bound map once 26.1 bound it; null before the constructor ran */
        DataComponentMap cb$components();

        void cb$setComponents(DataComponentMap components);
    }

    /** Item.Properties */
    public interface PropertiesLegacy {
        DataComponentMap.Builder cb$components();
    }
}
