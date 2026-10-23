package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_6903;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** RegistryOps.lookupProvider (private): the registry info lookup RegistryOps.create(ops, provider) made (CbRestFapiResources) */
@Mixin(class_6903.class)
public interface CbRestRegistryOpsAccessor {

    @Accessor("field_40852")
    class_6903.class_7863 cb$lookup();
}
