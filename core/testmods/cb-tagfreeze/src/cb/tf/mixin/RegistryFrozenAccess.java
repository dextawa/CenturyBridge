package cb.tf.mixin;

import net.minecraft.class_2370;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** a 1.21.1 mod's unfreeze, as Kilt's MappedRegistryInject.unfreeze does it: MappedRegistry.frozen set back to false */
@Mixin(class_2370.class)
public interface RegistryFrozenAccess {
    @Accessor(value = "field_36463", remap = false)
    void cbtf$setFrozen(boolean frozen);
}
