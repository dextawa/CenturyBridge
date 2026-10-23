package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_2922;
import net.minecraft.class_7871;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelCarving;

/** BiomeGenerationSettings.Builder's carver lookup, for 1.21.1's addCarver(LIQUID, key) (CbLevelCarving) */
@Mixin(net.minecraft.class_5485.class_5495.class)
public interface CbLevelCarvingBuilderAccess extends CbLevelCarving.BuilderLookup {

    @Override
    @Accessor("field_40895")
    class_7871<class_2922<?>> cb$carvers();
}
