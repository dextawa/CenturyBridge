package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.compat.fapi.event.registry.FabricRegistry;

/** Every Registry is a Fabric API FabricRegistry (1.20.1's registry sync RegistryMixin). */
@Mixin(net.minecraft.class_2378.class)
public interface CbFapiRegistryAliasInterface extends FabricRegistry {
}
