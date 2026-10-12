package top.dext.centurybridge.step.v1_21.mixin;

import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v1_21.rt.CbEnchantments;

/**
 * MappedRegistry.bindTags of a world's enchantment registry: CenturyBridge's records of 1.20.6 mods'
 * enchantments join the tags 1.21 reads where 1.20.6 read an enchantment's flags
 * (CbEnchantments.withLegacyTags). Every other registry, and CenturyBridge's stand-in registry of
 * 1.20.6 objects, binds what it is given.
 */
@Mixin(net.minecraft.class_2370.class)
public abstract class CbEnchTagsMixin {

    @SuppressWarnings({"rawtypes"})
    @ModifyVariable(method = "method_40257", at = @At("HEAD"), argsOnly = true)
    private Map cbEnch$legacyTags(Map tags) {
        Object self = this;
        if (self instanceof CbEnchantments.Legacy) {
            return tags;
        }
        net.minecraft.class_2378 registry = (net.minecraft.class_2378) self;
        if (!net.minecraft.class_7924.field_41265.equals(registry.method_30517())) {
            return tags;
        }
        return CbEnchantments.withLegacyTags(registry, tags);
    }
}
