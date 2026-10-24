package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.List;
import net.minecraft.class_2474;
import net.minecraft.class_3495;
import net.minecraft.class_3497;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemDatagenTags;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestFapiTags;

/**
 * Fabric API 0.116's TagProviderMixin removal half on 1.21.2's TagsProvider: the tag file written for a TagBuilder
 * carries the builder's "fabric:remove" entries (CbItemDatagenTags). The builder is the one the write lambda takes
 * from its entry, held for the write that lambda makes on the same thread.
 */
@Mixin(class_2474.class)
public abstract class CbItemTagProviderMixin {

    @Unique
    private static final ThreadLocal<class_3495> CB$BUILDER = new ThreadLocal<>();

    @ModifyVariable(method = "method_27046", at = @At("STORE"), ordinal = 0)
    private class_3495 cb$builder(class_3495 builder) {
        CB$BUILDER.set(builder);
        return builder;
    }

    @ModifyArg(method = "method_27046", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/class_2405;method_53496(Lnet/minecraft/class_7403;Lnet/minecraft/class_7225$class_7874;Lcom/mojang/serialization/Codec;Ljava/lang/Object;Ljava/nio/file/Path;)Ljava/util/concurrent/CompletableFuture;"),
            index = 3)
    private Object cb$remove(Object tagFile) {
        class_3495 builder = CB$BUILDER.get();
        CB$BUILDER.remove();
        List<class_3497> remove = builder != null ? CbItemDatagenTags.removeOf(builder) : List.of();
        if (!remove.isEmpty()) {
            ((CbRestFapiTags.TagFileHooks) tagFile).cb$setRemove(remove);
        }
        return tagFile;
    }
}
