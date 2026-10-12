package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** SpawnEggItem's default entity type, for 1.20.4's getType(tag) (rt/CbItemTagHooks) */
@Mixin(net.minecraft.class_1826.class)
public interface CbSpawnEggAccessor {

    /** defaultType */
    @Accessor("field_8917")
    net.minecraft.class_1299<?> cbS4$defaultType();
}
