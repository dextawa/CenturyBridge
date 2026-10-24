package cb.mixintest.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Arrow(Level, x, y, z): 1.20.3 takes the arrow's item too (step 2 ctorRedirects to a factory) */
@Mixin(net.minecraft.class_1667.class)
public interface ArrowAccess {
    @Invoker("<init>")
    static net.minecraft.class_1667 cbtest$newArrow(net.minecraft.class_1937 level, double x, double y, double z) {
        throw new AssertionError();
    }
}
