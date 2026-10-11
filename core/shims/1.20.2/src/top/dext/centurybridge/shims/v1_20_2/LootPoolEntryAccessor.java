package top.dext.centurybridge.shims.v1_20_2;

import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** LootPoolEntry's protected conditions, for the 1.20.1 serializer facade and field reads. */
@Mixin(net.minecraft.class_79.class)
public interface LootPoolEntryAccessor {

    @Accessor("field_988")
    List<net.minecraft.class_5341> cb$conditions();
}
