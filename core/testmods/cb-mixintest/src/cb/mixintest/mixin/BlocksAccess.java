package cb.mixintest.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Blocks.PUMPKIN, renamed in 1.20.3 (step 2 fieldRenames) */
@Mixin(net.minecraft.class_2246.class)
public interface BlocksAccess {
    @Accessor("PUMPKIN")
    static net.minecraft.class_2248 cbtest$pumpkin() {
        throw new AssertionError();
    }
}
