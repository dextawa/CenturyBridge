package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_6903;
import net.minecraft.class_7225;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/** RegistryOps.HolderLookupAdapter (package-private): the provider it wraps, and a new one over a provider (CbRestFapiResources) */
@Mixin(targets = "net.minecraft.class_6903$class_9683")
public interface CbRestHolderLookupAdapterAccessor {

    @Accessor("field_51501")
    class_7225.class_7874 cb$provider();

    @Invoker("<init>")
    static class_6903.class_7863 cb$new(class_7225.class_7874 provider) {
        throw new AssertionError();
    }
}
