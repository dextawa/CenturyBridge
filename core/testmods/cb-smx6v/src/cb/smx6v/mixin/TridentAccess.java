package cb.smx6v.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** ThrownTrident.onHitEntity (protected) for the driver */
@Mixin(net.minecraft.class_1685.class)
public interface TridentAccess {
    @Invoker(value = "method_7454", remap = false)
    void cbsmx6v$onHitEntity(net.minecraft.class_3966 hit);
}
