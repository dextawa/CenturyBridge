package top.dext.centurybridge.shims.v1_20_2;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.compat.fapi.loot.v1.FabricLootPool;

/**
 * Every LootPool is a Fabric API loot tables v1 FabricLootPool, as 1.20.1's LootPoolMixin made it:
 * its entries, conditions and functions as unmodifiable lists (1.20.1 copied them out of its arrays;
 * 1.20.2 keeps lists), and its rolls.
 */
@Mixin(net.minecraft.class_55.class)
public abstract class CbFapiLootPoolV1 implements FabricLootPool {

    @Shadow @Final private List<net.minecraft.class_79> field_953;
    @Shadow @Final private List<net.minecraft.class_5341> field_954;
    @Shadow @Final private List<net.minecraft.class_117> field_956;
    @Shadow @Final private net.minecraft.class_5658 field_957;

    @Override
    public List<net.minecraft.class_79> getEntries() {
        return ImmutableList.copyOf(field_953);
    }

    @Override
    public List<net.minecraft.class_5341> getConditions() {
        return ImmutableList.copyOf(field_954);
    }

    @Override
    public List<net.minecraft.class_117> getFunctions() {
        return ImmutableList.copyOf(field_956);
    }

    @Override
    public net.minecraft.class_5658 getRolls() {
        return field_957;
    }
}
