package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** EntityLootSubProvider's registries, which 1.21 keeps on the provider (CbLoot.registriesOf) */
@Mixin(net.minecraft.class_7789.class)
public interface CbLootEntityAccessor {

    @Accessor("field_51846")
    net.minecraft.class_7225.class_7874 cb$registries();
}
