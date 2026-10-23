package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** EntitySubPredicates.EntityVariantPredicateType: its codec and getter, for 1.20.4's EntityVariantPredicate (facade) */
@Mixin(net.minecraft.class_7378.class_7380.class)
public interface CbFinVariantTypeAccess {

    @Accessor("field_49794")
    com.mojang.serialization.MapCodec<?> cbS4$codec();

    @Accessor("field_38741")
    java.util.function.Function<net.minecraft.class_1297, java.util.Optional<?>> cbS4$getter();
}
