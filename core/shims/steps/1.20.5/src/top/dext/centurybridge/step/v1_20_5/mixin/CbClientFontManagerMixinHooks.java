package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplitClient;

/**
 * The injector into 1.20.5's own FontManager.getFontSetRaw that goes with {@link CbClientFontManagerMixin} (its restored
 * 1.20.4 member stays there, at {@code CbVanillaReads.PRIORITY}): the font id looked up through 1.20.4's renames first.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_378.class)
public abstract class CbClientFontManagerMixinHooks {

    @ModifyVariable(method = "method_57023", at = @At("HEAD"), argsOnly = true)
    private net.minecraft.class_2960 cb$actualId(net.minecraft.class_2960 id) {
        return ((CbPrioritySplitClient.FontRenames) (Object) this).cb$v1_20_5$renames().getOrDefault(id, id);
    }
}
