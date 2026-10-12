package top.dext.centurybridge.step.v1_20_3.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_3.rt.CbAdvContext;

/**
 * 1.20.2 loaded each advancement with a DeserializationContext of its own (the advancement's id,
 * the server's loot data) and handed it to every criterion's createInstance; 1.20.3 decodes the
 * advancement with its codec, which carries none. This opens that same context around 1.20.3's
 * per-advancement decode (ServerAdvancementManager's apply lambda, which parses and validates one
 * advancement and catches what goes wrong), so a 1.20.2 trigger's createInstance -- reached through
 * the codec CenturyBridge synthesized into it -- gets the context it got in 1.20.2.
 */
@Mixin(net.minecraft.class_2989.class)
public abstract class CbAdvServerAdvancementManagerMixin {

    @Shadow
    @Final
    private net.minecraft.class_60 field_44468;

    @Inject(method = "method_20723(Lcom/google/common/collect/ImmutableMap$Builder;Lnet/minecraft/class_2960;Lcom/google/gson/JsonElement;)V",
        at = @At("HEAD"))
    private void cb$beginAdvancement(ImmutableMap.Builder<?, ?> builder, net.minecraft.class_2960 id, JsonElement json,
            CallbackInfo ci) {
        CbAdvContext.begin(id, this.field_44468);
    }

    @Inject(method = "method_20723(Lcom/google/common/collect/ImmutableMap$Builder;Lnet/minecraft/class_2960;Lcom/google/gson/JsonElement;)V",
        at = @At("RETURN"))
    private void cb$endAdvancement(ImmutableMap.Builder<?, ?> builder, net.minecraft.class_2960 id, JsonElement json,
            CallbackInfo ci) {
        CbAdvContext.end();
    }
}
