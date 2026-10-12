package top.dext.centurybridge.step.v1_21.mixin;

import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v1_21.rt.CbLegacyRegistries;

/**
 * MappedRegistry.bindTags: the tags 1.20.6's vanilla data defined and 1.21's dropped are added where the
 * world's data does not define them (CbItemTags); a world registry that replaced a 1.20.6 builtin registry
 * binds its tags into CenturyBridge's stand-in too, over the stand-in's objects, after the stand-in has
 * added its own entries to them (CbLegacyRegistries.onBindTags). The stand-ins themselves bind what they
 * are given.
 */
@Mixin(net.minecraft.class_2370.class)
public abstract class CbLegacyRegistryTagsMixin {

    @SuppressWarnings({"rawtypes"})
    @ModifyVariable(method = "method_40257", at = @At("HEAD"), argsOnly = true)
    private Map cbLegacy$tags(Map tags) {
        net.minecraft.class_2378<?> registry = (net.minecraft.class_2378<?>) (Object) this;
        return CbLegacyRegistries.onBindTags(registry, top.dext.centurybridge.step.v1_21.rt.CbItemTags.withDroppedTags(registry, tags));
    }
}
