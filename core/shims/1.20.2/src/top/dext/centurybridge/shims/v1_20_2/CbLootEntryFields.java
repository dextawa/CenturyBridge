package top.dext.centurybridge.shims.v1_20_2;

import java.util.List;
import java.util.function.Predicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** LootPoolEntry's conditions and their compiled predicate, set by the 1.20.1 entry serializer facades. */
@Mixin(net.minecraft.class_79.class)
public interface CbLootEntryFields {

    @Mutable
    @Accessor("field_988")
    void cb$setConditions(List<net.minecraft.class_5341> conditions);

    @Mutable
    @Accessor("field_989")
    void cb$setConditionPredicate(Predicate<net.minecraft.class_47> predicate);
}
