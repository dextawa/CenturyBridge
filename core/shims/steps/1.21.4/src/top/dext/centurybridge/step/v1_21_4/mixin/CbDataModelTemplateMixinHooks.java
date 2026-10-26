package top.dext.centurybridge.step.v1_21_4.mixin;

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_4.rt.CbPrioritySplitClient;

/**
 * The injector into 1.21.4's own ModelTemplate.create(id, mapping, output) that goes with
 * {@link CbDataModelTemplateMixin} (its restored 1.21.3 members stay there, at {@code CbVanillaReads.PRIORITY}): a
 * subclass creates as 1.21.3 did, through both restored members virtually.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_4942.class)
public abstract class CbDataModelTemplateMixinHooks {

    @Inject(method = "method_25852", at = @At("HEAD"), cancellable = true)
    private void cb$v1_21_4$legacyCreate(net.minecraft.class_2960 id, net.minecraft.class_4944 mapping,
            BiConsumer<net.minecraft.class_2960, net.minecraft.class_10411> output, CallbackInfoReturnable<net.minecraft.class_2960> cir) {
        if ((Object) this.getClass() != net.minecraft.class_4942.class) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            BiConsumer<net.minecraft.class_2960, Supplier<com.google.gson.JsonElement>> legacy = (BiConsumer) output;
            cir.setReturnValue(((CbPrioritySplitClient.ModelTemplateCreate) (Object) this).cb$v1_21_4$legacyCreate(id, mapping, legacy));
        }
    }
}
