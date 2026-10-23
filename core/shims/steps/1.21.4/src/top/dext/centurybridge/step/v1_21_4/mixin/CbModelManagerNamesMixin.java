package top.dext.centurybridge.step.v1_21_4.mixin;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelBlockModel;

/** 1.21.3's model manager named every model file it loaded (BlockModel.name = the model's id); 1.21.4's loads them the same way */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_1092.class)
public abstract class CbModelManagerNamesMixin {

    @Inject(method = "method_65750", at = @At("RETURN"))
    private static void cb$name(java.util.Map.Entry<?, ?> entry, CallbackInfoReturnable<Pair<net.minecraft.class_2960, net.minecraft.class_793>> cir) {
        Pair<net.minecraft.class_2960, net.minecraft.class_793> loaded = cir.getReturnValue();
        if (loaded != null && loaded.getSecond() != null) {
            ((CbModelBlockModel.CbLegacyBlockModel) loaded.getSecond()).cb$setName(loaded.getFirst().toString());
        }
    }
}
