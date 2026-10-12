package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** BlockLootSubProvider.map (protected final): CbItemGenerics.blockLootMap reads it for 1.20.4 code */
@Mixin(net.minecraft.class_7788.class)
public interface CbItemBlockLootAccessor {

    @Accessor("field_40610")
    Map cb$map();
}
