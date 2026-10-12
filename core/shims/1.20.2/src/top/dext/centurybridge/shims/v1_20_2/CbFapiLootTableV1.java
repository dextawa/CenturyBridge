package top.dext.centurybridge.shims.v1_20_2;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.compat.fapi.loot.v1.FabricLootSupplier;

/**
 * Every LootTable is a Fabric API loot tables v1 FabricLootSupplier, as 1.20.1's LootTableMixin made
 * it: its pools and functions as unmodifiable lists (1.20.1 copied them out of its arrays; 1.20.2
 * keeps lists).
 */
@Mixin(net.minecraft.class_52.class)
public abstract class CbFapiLootTableV1 implements FabricLootSupplier {

    @Shadow @Final private List<net.minecraft.class_55> field_943;
    @Shadow @Final private List<net.minecraft.class_117> field_944;

    @Override
    public List<net.minecraft.class_55> getPools() {
        return ImmutableList.copyOf(field_943);
    }

    @Override
    public List<net.minecraft.class_117> getFunctions() {
        return ImmutableList.copyOf(field_944);
    }
}
