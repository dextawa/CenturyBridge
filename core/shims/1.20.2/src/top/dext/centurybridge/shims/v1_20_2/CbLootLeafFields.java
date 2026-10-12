package top.dext.centurybridge.shims.v1_20_2;

import java.util.List;
import java.util.function.BiFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * LeafEntry's weight, quality, functions and their compiled function, set by the 1.20.1 leaf
 * entry serializer facades: what 1.20.1's constructor received from LeafEntry.Serializer.
 */
@Mixin(net.minecraft.class_85.class)
public interface CbLootLeafFields {

    @Mutable
    @Accessor("field_995")
    void cb$setWeight(int weight);

    @Mutable
    @Accessor("field_994")
    void cb$setQuality(int quality);

    @Mutable
    @Accessor("field_996")
    void cb$setFunctions(List<net.minecraft.class_117> functions);

    @Mutable
    @Accessor("field_997")
    void cb$setCompiledFunctions(BiFunction<net.minecraft.class_1799, net.minecraft.class_47, net.minecraft.class_1799> function);
}
