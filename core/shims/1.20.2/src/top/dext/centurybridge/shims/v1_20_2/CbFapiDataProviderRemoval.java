package top.dext.centurybridge.shims.v1_20_2;

import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.compat.fapi.impl.datagen.CbFapiTagBuilders;

/**
 * DataProvider.saveJsonStable(writer, json, path) (method_10320, static): the JSON of a tag file TagProvider is writing
 * gets its builder's "fabric:remove" list (CbFapiTagProviderRemoval carries the builder) before the write is scheduled.
 * Any other JSON this method writes passes untouched: nothing is carried then. The signature is the same from 1.20.2 to
 * 1.21.11.
 */
@Mixin(net.minecraft.class_2405.class)
public interface CbFapiDataProviderRemoval {

    @Inject(method = "method_10320", at = @At("HEAD"), require = 1)
    private static void cb$addRemove(net.minecraft.class_7403 writer, JsonElement json, Path path,
                                     CallbackInfoReturnable<CompletableFuture<?>> cir) {
        CbFapiTagBuilders.writeCarriedRemovals(json);
    }
}
