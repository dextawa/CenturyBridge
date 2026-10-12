package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockLoot;

/** RandomizableContainerBlockEntity's protected lootTable field, for CbBlockLoot */
@Mixin(net.minecraft.class_2621.class)
public interface CbBlockRandomizableLootAccessor extends CbBlockLoot.Field {

    @Override
    @Accessor("field_12037")
    net.minecraft.class_5321<net.minecraft.class_52> cb$lootTable();

    @Override
    @Accessor("field_12037")
    void cb$setLootTable(net.minecraft.class_5321<net.minecraft.class_52> key);
}
