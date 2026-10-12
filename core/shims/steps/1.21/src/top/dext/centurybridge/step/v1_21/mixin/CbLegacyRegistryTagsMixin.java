package top.dext.centurybridge.step.v1_21.mixin;

import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v1_21.rt.CbLegacyRegistries;

/**
 * MappedRegistry.bindTags: a world registry that replaced a 1.20.6 builtin registry binds its tags into
 * CenturyBridge's stand-in too, over the stand-in's objects, after the stand-in has added its own entries
 * to them (CbLegacyRegistries.onBindTags). Every other registry, and the stand-ins themselves, bind what
 * they are given.
 */
@Mixin(net.minecraft.class_2370.class)
public abstract class CbLegacyRegistryTagsMixin {

    @SuppressWarnings({"rawtypes"})
    @ModifyVariable(method = "method_40257", at = @At("HEAD"), argsOnly = true)
    private Map cbLegacy$tags(Map tags) {
        Object self = this;
        return CbLegacyRegistries.onBindTags((net.minecraft.class_2378<?>) self, tags);
    }
}
