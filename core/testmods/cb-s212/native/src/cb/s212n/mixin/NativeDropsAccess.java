package cb.s212n.mixin;

import java.util.Optional;
import net.minecraft.class_10162;
import net.minecraft.class_2248;
import net.minecraft.class_5321;
import net.minecraft.class_52;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** 1.21.2 BlockBehaviour.Properties.drops: the loot table as a function of the block id */
@Mixin(net.minecraft.class_4970.class_2251.class)
public interface NativeDropsAccess {
    @Accessor(value = "field_54007", remap = false)
    class_10162<class_2248, Optional<class_5321<class_52>>> cbs212n$drops();
}
