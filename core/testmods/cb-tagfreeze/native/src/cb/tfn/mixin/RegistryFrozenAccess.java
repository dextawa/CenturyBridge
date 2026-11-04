package cb.tfn.mixin;

import net.minecraft.class_2370;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** the native control's unfreeze: MappedRegistry.frozen set back to false, so vanilla code can refreeze */
@Mixin(class_2370.class)
public interface RegistryFrozenAccess {
    @Accessor(value = "field_36463", remap = false)
    void cbtfn$setFrozen(boolean frozen);
}
