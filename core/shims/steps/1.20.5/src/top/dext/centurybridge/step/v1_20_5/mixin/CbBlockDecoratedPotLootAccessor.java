package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockLoot;

/** DecoratedPotBlockEntity's protected lootTable field, for CbBlockLoot */
@Mixin(net.minecraft.class_8172.class)
public interface CbBlockDecoratedPotLootAccessor extends CbBlockLoot.Field {

    @Override
    @Accessor("field_47156")
    net.minecraft.class_5321<net.minecraft.class_52> cb$lootTable();

    @Override
    @Accessor("field_47156")
    void cb$setLootTable(net.minecraft.class_5321<net.minecraft.class_52> key);
}
