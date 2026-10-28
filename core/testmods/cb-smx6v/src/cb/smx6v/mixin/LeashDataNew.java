package cb.smx6v.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** a native 1.21 mod's invoker of the package-private LeashData(Either) constructor: a leash saved by holder uuid or knot position */
@Mixin(net.minecraft.class_9817.class_9818.class)
public interface LeashDataNew {
    @Invoker("<init>")
    static net.minecraft.class_9817.class_9818 cbsmx6v$saved(com.mojang.datafixers.util.Either<java.util.UUID, net.minecraft.class_2338> info) {
        throw new AssertionError();
    }
}
