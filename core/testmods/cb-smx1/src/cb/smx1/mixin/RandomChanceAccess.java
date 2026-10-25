package cb.smx1.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** LootItemRandomChanceCondition.probability (field_1296; 1.20.2 made the class a record: comp_1881) */
@Mixin(net.minecraft.class_219.class)
public interface RandomChanceAccess {
    @Accessor(value = "field_1296", remap = false)
    float cbsmx1$probability();
}
