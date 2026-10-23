package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Resource: 1.20.4's isBuiltin() -- whether the resource came from a pack the game ships -- which 1.20.5 replaced
 * with the pack's known-pack identity: built in when that is vanilla's.
 */
@Mixin(net.minecraft.class_3298.class)
public abstract class CbFinResourceMixin {

    @Shadow
    public abstract java.util.Optional<net.minecraft.class_9226> method_56936();

    /** was isBuiltin() */
    public boolean method_45305() {
        return this.method_56936().map(net.minecraft.class_9226::method_56931).orElse(false);
    }
}
