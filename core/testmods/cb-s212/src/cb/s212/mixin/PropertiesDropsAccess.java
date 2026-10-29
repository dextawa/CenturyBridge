package cb.s212.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** 1.21.1 BlockBehaviour.Properties.drops: a loot table key (1.21.2: the DependantName field_54007) */
@Mixin(net.minecraft.class_4970.class_2251.class)
public interface PropertiesDropsAccess {
    @Accessor(value = "field_10666", remap = false)
    net.minecraft.class_5321<net.minecraft.class_52> cbs212$drops();

    @Accessor(value = "field_10666", remap = false)
    void cbs212$setDrops(net.minecraft.class_5321<net.minecraft.class_52> key);
}
