package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_8790;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.facade.fapi.FabricRecipeExporter;

/**
 * Fabric API 0.116 injected FabricRecipeExporter into RecipeOutput (0.106 does not): every output answers
 * getRecipeIdentifier(id), the id itself unless the output overrides it (a 1.21.1 mod's, or FabricRecipeProvider's).
 */
@Mixin(class_8790.class)
public interface CbItemRecipeOutputMixin extends FabricRecipeExporter {
}
