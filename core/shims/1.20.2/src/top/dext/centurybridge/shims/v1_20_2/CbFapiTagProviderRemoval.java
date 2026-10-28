package top.dext.centurybridge.shims.v1_20_2;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.compat.fapi.impl.datagen.CbFapiTagBuilders;

/**
 * A generated tag file lists its builder's removals under "fabric:remove" (1.20.1's datagen TagProviderMixin).
 * TagProvider's per-tag lambda (method_27046) carries the tag's builder, taken from its Map.Entry argument by type at
 * HEAD, to DataProvider.saveJsonStable (CbFapiDataProviderRemoval), which the lambda calls on the same thread with the
 * JSON it writes -- after 1.20.2's Fabric API has added "replace" to it, so the file carries both. The carry is cleared
 * at the lambda's return.
 *
 * No handler captures the lambda's arguments by position or its locals, so later releases reshaping it leave both
 * applicable: 1.20.5 adds a TagLookup argument and builds the JSON inside DataProvider.saveStable (method_53496), which
 * still calls saveJsonStable (method_10320) synchronously.
 */
@Mixin(net.minecraft.class_2474.class)
abstract class CbFapiTagProviderRemoval {

    @ModifyVariable(method = "method_27046", at = @At("HEAD"), argsOnly = true, require = 1)
    private Map.Entry<?, ?> cb$carryBuilder(Map.Entry<?, ?> entry) {
        CbFapiTagBuilders.carry(entry.getValue());
        return entry;
    }

    @Inject(method = "method_27046", at = @At("RETURN"), require = 1)
    private void cb$dropBuilder(CallbackInfoReturnable<CompletableFuture<?>> cir) {
        CbFapiTagBuilders.carry(null);
    }
}
