package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** 1.20.2 LootContextTypes lost getId(); the id<->set BiMap is a private static. */
@Mixin(net.minecraft.class_173.class)
public interface CbLootContextTypesAccessor {
    @Accessor("field_1178")
    static com.google.common.collect.BiMap<net.minecraft.class_2960, net.minecraft.class_176> cbRegistry() {
        throw new AssertionError();
    }
}
