package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** SetAttributesFunction.ModifierBuilder's modifier id: 1.20.6's withUuid(uuid) set the builder's uuid (CbLoot) */
@Mixin(net.minecraft.class_137.class_6156.class)
public interface CbLootModifierBuilderAccessor {

    @Mutable
    @Accessor("field_31860")
    void cb$setId(net.minecraft.class_2960 id);
}
