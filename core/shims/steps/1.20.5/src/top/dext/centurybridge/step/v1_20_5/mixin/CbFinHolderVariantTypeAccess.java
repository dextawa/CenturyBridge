package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** EntitySubPredicates' holder-set variant type: its codec, for 1.20.4's EntityVariantPredicate (facade) */
@Mixin(net.minecraft.class_7378.class_9419.class)
public interface CbFinHolderVariantTypeAccess {

    @Accessor("field_49974")
    com.mojang.serialization.MapCodec<?> cbS4$codec();
}
