package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1124;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestRecipeBookClient;

/** SessionSearchTrees.rebuildAfterLanguageChange: the 1.21.1 recipe search is rebuilt too, as 1.21.1's was (its tooltips change) */
@Environment(EnvType.CLIENT)
@Mixin(class_1124.class)
public abstract class CbRestSearchTreesMixin {

    @Inject(method = "method_60348", at = @At("TAIL"))
    private void cb$rebuild(CallbackInfo ci) {
        CbRestRecipeBookClient.rebuildAfterLanguageChange((class_1124) (Object) this);
    }
}
