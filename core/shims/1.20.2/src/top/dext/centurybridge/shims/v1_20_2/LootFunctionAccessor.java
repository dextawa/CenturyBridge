package top.dext.centurybridge.shims.v1_20_2;

import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ConditionalLootFunction's protected conditions, for the 1.20.1 serializer facade. */
@Mixin(net.minecraft.class_120.class)
public interface LootFunctionAccessor {

    @Accessor("field_1047")
    List<net.minecraft.class_5341> cb$conditions();
}
