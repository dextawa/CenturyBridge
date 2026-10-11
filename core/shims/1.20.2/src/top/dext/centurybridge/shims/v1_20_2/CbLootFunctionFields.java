package top.dext.centurybridge.shims.v1_20_2;

import java.util.List;
import java.util.function.Predicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * ConditionalLootFunction's conditions and their compiled predicate, set by the 1.20.1 serializer
 * facades after a codec has decoded the function's own fields: what 1.20.1's constructor received
 * from the base serializer (CbLootSerializers.readFunction).
 */
@Mixin(net.minecraft.class_120.class)
public interface CbLootFunctionFields {

    @Mutable
    @Accessor("field_1047")
    void cb$setPredicates(List<net.minecraft.class_5341> conditions);

    @Mutable
    @Accessor("field_1048")
    void cb$setCompositePredicates(Predicate<net.minecraft.class_47> predicate);
}
